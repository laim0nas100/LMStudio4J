package com.github.laim0nas100.lmstudio4j.mcp;

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

    public static class Builder {

        protected boolean sync = true;
        protected String serverName = "Generic MCP server";
        protected String version = "1.0.0";
        protected String mcpEndpoint = "/mcp";

        protected McpSchema.ServerCapabilities capabilities; // can be overriden
        
        protected boolean logging = false;
        protected Map<String,Object> experimental;
        

        protected List<ToolDefinition> tools = new ArrayList<>();
        protected List<CompletionDefinition> completions = new ArrayList<>();
        protected List<PromptDefinition> prompts = new ArrayList<>();
        protected List<ResourceTemplateDefinition> resourceTemplates = new ArrayList<>();
        protected List<ResourceDefinition> resources = new ArrayList<>();

        protected String instructions;
        protected boolean immediateExecution = false;
        protected Duration requestTimeout;

        public Builder withSync(boolean sync) {
            this.sync = sync;
            return this;
        }
        
        public Builder withLoggingCap(boolean logging){
            this.logging = logging;
            return this;
        }
        
        public Builder withExperimentalCap(Map<String,Object> experimental){
            this.experimental = experimental;
            return this;
        }

        public Builder withImmediateExecution(boolean immediateExecution) {
            this.immediateExecution = immediateExecution;
            return this;
        }

        public Builder withRequestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public Builder withServerName(String name) {
            this.serverName = Objects.requireNonNull(name);
            return this;
        }

        public Builder withServerVersion(String ver) {
            this.version = Objects.requireNonNull(ver);
            return this;
        }

        public Builder withMcpEndpoint(String mcpEndpoint) {
            this.mcpEndpoint = Objects.requireNonNull(mcpEndpoint);
            return this;
        }

        public Builder withTool(Tool tool, BiFunction<Object, McpSchema.CallToolRequest, McpSchema.CallToolResult> functor) {
            this.tools.add(new ToolDefinition(tool, functor));
            return this;
        }

        public Builder withCompletion(McpSchema.CompleteReference reference, BiFunction<Object, McpSchema.CompleteRequest, McpSchema.CompleteResult> functor) {
            this.completions.add(new CompletionDefinition(reference, functor));
            return this;
        }

        public Builder withPrompt(McpSchema.Prompt prompt, BiFunction<Object, McpSchema.GetPromptRequest, McpSchema.GetPromptResult> functor) {
            this.prompts.add(new PromptDefinition(prompt, functor));
            return this;
        }

        public Builder withResourceTemplate(McpSchema.ResourceTemplate template, BiFunction<Object, McpSchema.ReadResourceRequest, McpSchema.ReadResourceResult> functor) {
            this.resourceTemplates.add(new ResourceTemplateDefinition(template, functor));
            return this;
        }

        public Builder withResource(McpSchema.Resource resource, BiFunction<Object, McpSchema.ReadResourceRequest, McpSchema.ReadResourceResult> functor) {
            this.resources.add(new ResourceDefinition(resource, functor));
            return this;
        }

        protected McpSchema.ServerCapabilities autoCapabilities(){
            
            McpSchema.ServerCapabilities.Builder capBuild = McpSchema.ServerCapabilities.builder();
            if(!resources.isEmpty() || !resourceTemplates.isEmpty()){
                capBuild.resources(true, true);
            }
            if(!tools.isEmpty()){
                capBuild.tools(true);
            }
            if(!prompts.isEmpty()){
                capBuild.prompts(true);
            }
            if(logging){
                capBuild.logging();
            }
            if(experimental != null){
                capBuild.experimental(experimental);
            }
            
            return capBuild.build();
            
        }

        public Builder withCapabilities(McpSchema.ServerCapabilities capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder withInstructions(String instructions) {
            this.instructions = instructions;
            return this;
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
                builder.capabilities == null ? builder.autoCapabilities() : builder.capabilities,
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
