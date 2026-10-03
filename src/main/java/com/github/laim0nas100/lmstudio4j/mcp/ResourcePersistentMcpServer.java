package com.github.laim0nas100.lmstudio4j.mcp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.laim0nas100.dbstore.filestore.FileStoreDB;
import com.github.laim0nas100.dbstore.filestore.ResourceMetadata;
import com.github.laim0nas100.dbstore.keystore.KeyValueStore;
import com.github.laim0nas100.lmstudio4j.AttachmentTextDetector;
import com.github.laim0nas100.uncheckedutils.SafeOpt;
import com.github.laim0nas100.uncheckedutils.func.UncheckedFunction;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.TypeRef;
import io.modelcontextprotocol.json.schema.JsonSchemaValidator;
import io.modelcontextprotocol.spec.McpSchema;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.apache.tika.io.TikaInputStream;
import org.jdbi.v3.core.Jdbi;

/**
 *
 * @author Lemmin
 */
public class ResourcePersistentMcpServer extends GenericMcpServer {
    
    public static class ResourcePersistanntBuilder<B extends ResourcePersistanntBuilder<B>> extends GenericMcpServer.Builder<B> {
        
        protected Jdbi jdbIntance;
        protected String resourceTableName;
        protected String blobTableName;
        protected String keyValueTableName;
        
        public ResourcePersistanntBuilder() {
        }
        
        public B withJdbi(Jdbi jdbi) {
            this.jdbIntance = jdbi;
            return me();
        }
        
        public B withPersistantResources(String resourceTableName, String blobTableName) {
            this.resourceTableName = resourceTableName;
            this.blobTableName = blobTableName;
            this.autoCapabilities.resources(true, true);
            return me();
        }
        
        public B withKeyValueStore(String keyValueTableName) {
            this.keyValueTableName = keyValueTableName;
            this.autoCapabilities.tools(true);
            return me();
        }
        
    }
    
    public ResourcePersistentMcpServer(ResourcePersistanntBuilder builder) {
        super(builder);
    }
    
    public ResourcePersistentMcpServer(String serverName,
            String serverVersion,
            boolean sync,
            boolean immediateExecution,
            Duration requestTimeout,
            String mcpEndpoint,
            String instructions,
            McpJsonMapper jsonMapper,
            JsonSchemaValidator jsonSchemaValidator,
            McpSchema.ServerCapabilities capabilities,
            List<ToolDefinition> toolDefinitions,
            List<CompletionDefinition> completions,
            List<PromptDefinition> prompts,
            List<ResourceTemplateDefinition> templates,
            List<ResourceDefinition> resources,
            FileStoreDB fileStore,
            KeyValueStore keyValueStore
    ) {
        super(serverName,
                serverVersion,
                sync,
                immediateExecution,
                requestTimeout,
                mcpEndpoint,
                instructions,
                jsonMapper,
                jsonSchemaValidator,
                capabilities,
                toolDefinitions,
                completions,
                prompts,
                templates,
                resources
        );
        
        this.fileStore = fileStore;
        this.keyValueStore = keyValueStore;
        
    }
    
    protected FileStoreDB fileStore;
    protected KeyValueStore keyValueStore;
    
    protected <T> SafeOpt<T> usingFileStore(UncheckedFunction<FileStoreDB, T> functor, Object... nullable) {
        for (Object ob : nullable) {
            if (ob == null) {
                return SafeOpt.empty();
            }
        }
        return SafeOpt.ofNullable(fileStore).map(functor);
    }
    
    protected <T> SafeOpt<T> usingFileStoreFlat(UncheckedFunction<FileStoreDB, SafeOpt<T>> functor, Object... nullable) {
        for (Object ob : nullable) {
            if (ob == null) {
                return SafeOpt.empty();
            }
        }
        return SafeOpt.ofNullable(fileStore).flatMap(functor);
    }
    
    protected McpSchema.ReadResourceResult resourceReadAction(Object exchange, McpSchema.ReadResourceRequest request) {
        String uri = request.uri();
        return fileStore.find(uri).flatMap(meta -> {
            McpSchema.Resource resource = getResourceDefinition(meta).resource();
            if (meta.isText()) {
                return fileStore.openString(uri)
                        .mapClosing(reader -> reader.readAllAsString())
                        .map(content -> {
                            return McpSchema.ReadResourceResult.builder(
                                    List.of(
                                            McpSchema.TextResourceContents.builder(resource.uri(), content)
                                                    .mimeType(resource.mimeType())
                                                    .meta(resource.meta())
                                                    .build()
                                    )
                            ).build();
                        });
            } else {// binary
                return fileStore.open(uri)
                        .mapClosing(stream -> stream.readAllBytes())
                        .map(bytes -> Base64.getEncoder().encodeToString(bytes))
                        .map(content -> {
                            return McpSchema.ReadResourceResult.builder(
                                    List.of(
                                            McpSchema.BlobResourceContents.builder(resource.uri(), content)
                                                    .mimeType(resource.mimeType())
                                                    .meta(resource.meta())
                                                    .build()
                                    )
                            ).build();
                        });
            }
            
        }).throwAnyGet();
    }
    
    protected ResourceDefinition getResourceDefinition(ResourceMetadata metadata) throws IOException {
        McpSchema.Resource.Builder meta = McpSchema.Resource.builder(metadata.getUri(), metadata.getName())
                .description(metadata.getDescription())
                .mimeType(metadata.getMimeType())
                .size(metadata.getSize())
                .meta(jsonMapper.readValue(metadata.getAdditional_info(), new TypeRef<Map<String, Object>>() {
                }));
        
        return new ResourceDefinition(meta.build(), this::resourceReadAction);
    }
    
    public SafeOpt putResource(ResourceMetadata meta, InputStream content) {
        return usingFileStoreFlat(fs -> {
            TikaInputStream stream = TikaInputStream.get(content);
            TikaInputStream.get(Paths.get(""));
            AttachmentTextDetector.TextDetectionResult detected = AttachmentTextDetector.detectText(
                    stream, meta.getName(), meta.getMimeType()
            );
            meta.setMimeType(detected.detectedMimeType());
            meta.setText(detected.isText());
            return fs.put(meta, stream).peek(any -> {
                addResource(getResourceDefinition(meta));
            });
        }, meta, content);
    }
    
    public SafeOpt putResourceString(ResourceMetadata meta, String content) {
        return usingFileStoreFlat(fs -> {
            meta.setMimeType("text/plain");
            meta.setText(true);
            return fs.put(meta, TikaInputStream.get(content.getBytes())).peek(any -> {
                addResource(getResourceDefinition(meta));
            });
        }, meta, content);
    }
    
    public SafeOpt deleteResource(String uri) {
        return usingFileStoreFlat(fs -> {
            return fs.delete(uri).peek(id -> {
                removeResource(uri);
            });
        }, uri);
    }
    
}
