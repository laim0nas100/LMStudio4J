package com.github.laim0nas100.lmstudio4j.mcp;

import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.schema.JsonSchemaValidator;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 *
 * @author Lemmin
 */
public class GenericMcpServer {

    public static class Builder<B extends Builder> {

        protected boolean sync = true;
        protected String serverName = "Generic MCP server";
        protected String version = "1.0.0";
        protected String mcpEndpoint = "/mcp";

        protected McpSchema.ServerCapabilities capabilities; // can be overriden
        protected McpSchema.ServerCapabilities.Builder autoCapabilities = new McpSchema.ServerCapabilities.Builder();

        protected boolean logging = false;
        protected Map<String, Object> experimental;

        protected List<ToolDefinition> tools = new ArrayList<>();
        protected List<CompletionDefinition> completions = new ArrayList<>();
        protected List<PromptDefinition> prompts = new ArrayList<>();
        protected List<ResourceTemplateDefinition> resourceTemplates = new ArrayList<>();
        protected List<ResourceDefinition> resources = new ArrayList<>();

        protected McpJsonMapper jsonMapper = McpJsonDefaults.getMapper();
        protected JsonSchemaValidator schemaValidator = McpJsonDefaults.getSchemaValidator();

        protected String instructions;
        protected boolean immediateExecution = false;
        protected Duration requestTimeout;

        public B me() {
            return (B) this;
        }

        public B withJsonMapper(McpJsonMapper jsonMapper) {
            this.jsonMapper = jsonMapper;
            return me();
        }

        public B withSchemaValidator(JsonSchemaValidator validator) {
            this.schemaValidator = validator;
            return me();
        }

        public B withSync(boolean sync) {
            this.sync = sync;
            return me();
        }

        public B withLoggingCap(boolean logging) {
            this.logging = logging;
            return me();
        }

        public B withExperimentalCap(Map<String, Object> experimental) {
            this.experimental = experimental;
            return me();
        }

        public B withImmediateExecution(boolean immediateExecution) {
            this.immediateExecution = immediateExecution;
            return me();
        }

        public B withRequestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return me();
        }

        public B withServerName(String name) {
            this.serverName = Objects.requireNonNull(name);
            return me();
        }

        public B withServerVersion(String ver) {
            this.version = Objects.requireNonNull(ver);
            return me();
        }

        public B withMcpEndpoint(String mcpEndpoint) {
            this.mcpEndpoint = Objects.requireNonNull(mcpEndpoint);
            return me();
        }

        public B withTool(Tool tool, BiFunction<Object, McpSchema.CallToolRequest, McpSchema.CallToolResult> functor) {
            this.tools.add(new ToolDefinition(tool, functor));
            return me();
        }

        public B withCompletion(McpSchema.CompleteReference reference, BiFunction<Object, McpSchema.CompleteRequest, McpSchema.CompleteResult> functor) {
            this.completions.add(new CompletionDefinition(reference, functor));
            return me();
        }

        public B withPrompt(McpSchema.Prompt prompt, BiFunction<Object, McpSchema.GetPromptRequest, McpSchema.GetPromptResult> functor) {
            this.prompts.add(new PromptDefinition(prompt, functor));
            return me();
        }

        public B withResourceTemplate(McpSchema.ResourceTemplate template, BiFunction<Object, McpSchema.ReadResourceRequest, McpSchema.ReadResourceResult> functor) {
            this.resourceTemplates.add(new ResourceTemplateDefinition(template, functor));
            return me();
        }

        public B withResource(McpSchema.Resource resource, BiFunction<Object, McpSchema.ReadResourceRequest, McpSchema.ReadResourceResult> functor) {
            this.resources.add(new ResourceDefinition(resource, functor));
            return me();
        }

        protected McpSchema.ServerCapabilities.Builder setAutoCapabilities() {

            if (!resources.isEmpty() || !resourceTemplates.isEmpty()) {
                autoCapabilities.resources(true, true);
            }
            if (!tools.isEmpty()) {
                autoCapabilities.tools(true);
            }
            if (!prompts.isEmpty()) {
                autoCapabilities.prompts(true);
            }
            if (logging) {
                autoCapabilities.logging();
            }
            if (experimental != null) {
                autoCapabilities.experimental(experimental);
            }

            return autoCapabilities;

        }

        public Builder withCapabilities(McpSchema.ServerCapabilities capabilities) {
            this.capabilities = capabilities;
            return me();
        }

        public Builder withInstructions(String instructions) {
            this.instructions = instructions;
            return me();
        }

        public GenericMcpServer build() {
            return new GenericMcpServer(this);
        }

    }

    protected final McpSyncServer mcpSync;
    protected final McpAsyncServer mcpAsync;
    protected final HttpServletStreamableServerTransportProvider transport;
    protected final boolean sync;

    protected final String serverVersion;
    protected final String serverName;
    protected McpServletBind bind;

    protected JsonSchemaValidator schemaValidator;
    protected McpJsonMapper jsonMapper;

    public GenericMcpServer(Builder builder) {
        Objects.requireNonNull(builder);
        this(
                builder.serverName,
                builder.version,
                builder.sync,
                builder.immediateExecution,
                builder.requestTimeout,
                builder.mcpEndpoint,
                builder.instructions,
                builder.jsonMapper,
                builder.schemaValidator,
                builder.capabilities == null ? builder.setAutoCapabilities().build() : builder.capabilities,
                builder.tools,
                builder.completions,
                builder.prompts,
                builder.resourceTemplates,
                builder.resources
        );
    }

    public GenericMcpServer(
            String serverName,
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
            List<ResourceDefinition> resources
    ) {

        this.sync = sync;
        this.serverName = Objects.requireNonNull(serverName);
        this.serverVersion = Objects.requireNonNull(serverVersion);
        this.jsonMapper = Objects.requireNonNullElseGet(jsonMapper, McpJsonDefaults::getMapper);
        this.schemaValidator = Objects.requireNonNullElseGet(jsonSchemaValidator, McpJsonDefaults::getSchemaValidator);

        this.jsonMapper = jsonMapper;
        this.schemaValidator = jsonSchemaValidator;

        transport
                = HttpServletStreamableServerTransportProvider.builder()
                        .mcpEndpoint(Objects.requireNonNull(mcpEndpoint))
                        .build();

        if (sync) {
            McpServer.SyncSpecification<McpServer.StreamableSyncSpecification> builder = McpServer
                    .sync(transport)
                    .serverInfo(serverName, serverVersion)
                    .immediateExecution(immediateExecution);

            if (requestTimeout != null) {
                builder.requestTimeout(requestTimeout);
            }

            if (instructions != null) {
                builder.instructions(instructions);
            }
            builder.jsonMapper(this.jsonMapper);
            builder.jsonSchemaValidator(this.schemaValidator);

            if (capabilities != null) {
                builder.capabilities(capabilities);
            }

            if (toolDefinitions != null) {
                for (ToolDefinition toolDef : toolDefinitions) {
                    Objects.requireNonNull(toolDef);
                    builder.toolCall(toolDef.tool(), toolDef.syncHandler());
                }
            }

            if (completions != null) {
                List<McpServerFeatures.SyncCompletionSpecification> toList = completions.stream().map(com -> {
                    return new McpServerFeatures.SyncCompletionSpecification(com.reference(), com.syncHandler());
                }).toList();

                builder.completions(toList);
            }

            if (prompts != null) {
                List<McpServerFeatures.SyncPromptSpecification> toList = prompts.stream().map(pr -> {
                    return new McpServerFeatures.SyncPromptSpecification(pr.prompt(), pr.syncHandler());
                }).toList();
                builder.prompts(toList);
            }

            if (templates != null) {
                List<McpServerFeatures.SyncResourceTemplateSpecification> toList = templates.stream().map(tem -> {
                    return new McpServerFeatures.SyncResourceTemplateSpecification(tem.template(), tem.syncHandler());
                }).toList();
                builder.resourceTemplates(toList);
            }

            if (resources != null) {
                List<McpServerFeatures.SyncResourceSpecification> toList = resources.stream().map(res -> {
                    return new McpServerFeatures.SyncResourceSpecification(res.resource(), res.syncHandler());
                }).toList();
                builder.resources(toList);
            }

            this.mcpSync = builder.build();
            this.mcpAsync = null;

        } else {
            McpServer.AsyncSpecification<?> builder = McpServer
                    .async(transport)
                    .serverInfo(serverName, serverVersion);

            if (requestTimeout != null) {
                builder.requestTimeout(requestTimeout);
            }

            if (instructions != null) {
                builder.instructions(instructions);
            }
            builder.jsonMapper(this.jsonMapper);
            builder.jsonSchemaValidator(this.schemaValidator);

            if (capabilities != null) {
                builder.capabilities(capabilities);
            }

            if (completions != null) {
                List<McpServerFeatures.AsyncCompletionSpecification> toList = completions.stream().map(com -> {
                    return new McpServerFeatures.AsyncCompletionSpecification(com.reference(), com.asyncHandler());
                }).toList();

                builder.completions(toList);
            }

            if (toolDefinitions != null) {

                for (ToolDefinition toolDef : toolDefinitions) {
                    Objects.requireNonNull(toolDef);
                    builder.toolCall(toolDef.tool(), toolDef.asyncHandler());
                }
            }

            if (prompts != null) {
                List<McpServerFeatures.AsyncPromptSpecification> toList = prompts.stream().map(pr -> {
                    return new McpServerFeatures.AsyncPromptSpecification(pr.prompt(), pr.asyncHandler());
                }).toList();
                builder.prompts(toList);
            }

            if (templates != null) {
                List<McpServerFeatures.AsyncResourceTemplateSpecification> toList = templates.stream().map(tem -> {
                    return new McpServerFeatures.AsyncResourceTemplateSpecification(tem.template(), tem.asyncHandler());
                }).toList();
                builder.resourceTemplates(toList);
            }

            if (resources != null) {
                List<McpServerFeatures.AsyncResourceSpecification> toList = resources.stream().map(res -> {
                    return new McpServerFeatures.AsyncResourceSpecification(res.resource(), res.asyncHandler());
                }).toList();
                builder.resources(toList);
            }
            this.mcpAsync = builder.build();
            this.mcpSync = null;
        }

    }

    public GenericMcpServer addResource(ResourceDefinition resourceDef) {
        Objects.requireNonNull(resourceDef);
        if (sync) {
            mcpSync.addResource(resourceDef.toSyncSpec());
        } else {
            mcpAsync.addResource(resourceDef.toAsyncSpec());
        }
        return this;
    }

    public GenericMcpServer removeResource(String resourceUri) {
        Objects.requireNonNull(resourceUri);
        if (sync) {
            mcpSync.removeResource(resourceUri);
        } else {
            mcpAsync.removeResource(resourceUri);
        }
        return this;
    }

    public GenericMcpServer addTool(ToolDefinition toolDef) {
        Objects.requireNonNull(toolDef);
        if (sync) {
            mcpSync.addTool(McpServerFeatures.SyncToolSpecification.builder()
                    .tool(toolDef.tool())
                    .callHandler(toolDef.syncHandler())
                    .build()
            );
        } else {
            mcpAsync.addTool(McpServerFeatures.AsyncToolSpecification.builder()
                    .tool(toolDef.tool())
                    .callHandler(toolDef.asyncHandler())
                    .build()
            );
        }
        return this;
    }

    public GenericMcpServer removeTool(String toolName) {
        Objects.requireNonNull(toolName);
        if (sync) {
            mcpSync.removeTool(toolName);
        } else {
            mcpAsync.removeTool(toolName);
        }
        return this;
    }

    public GenericMcpServer bind(McpServletBind bind) {
        Objects.requireNonNull(bind);
        if (this.bind != null) {
            throw new IllegalStateException(serverName + " was already bound");
        }

        bind.bind(transport);
        this.bind = bind;
        return this;
    }

    public GenericMcpServer unbind() throws Exception {
        if (this.bind == null) {
            throw new IllegalStateException(serverName + " was not bound");
        }
        this.bind.unbind();
        this.bind = null;

        return this;
    }

    /**
     * Stop the server.
     */
    public void stop() throws Exception {
        Exception failure = null;

        try {
            if (sync) {
                mcpSync.closeGracefully();
            } else {
                mcpAsync.closeGracefully();
            }
        } catch (Exception e) {
            failure = e;
        }

        if (bind != null) {
            try {
                bind.unbind();
                bind = null;
            } catch (Exception e) {
                if (failure == null) {
                    failure = e;
                } else {
                    failure.addSuppressed(e);
                }
            }
        }

        if (failure != null) {
            throw failure;
        }
    }
}
