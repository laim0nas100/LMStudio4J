package com.github.laim0nas100.lmstudio4j.mcp;

import io.modelcontextprotocol.server.McpAsyncServerExchange;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.Objects;
import java.util.function.BiFunction;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lemmin
 */
public record ToolDefinition(McpSchema.Tool tool, BiFunction<Object, McpSchema.CallToolRequest, McpSchema.CallToolResult> functor) {
    
    public ToolDefinition(McpSchema.Tool tool, BiFunction<Object, McpSchema.CallToolRequest, McpSchema.CallToolResult> functor) {
        this.tool = Objects.requireNonNull(tool);
        this.functor = Objects.requireNonNull(functor);
    }
    
    public BiFunction<McpSyncServerExchange, McpSchema.CallToolRequest, McpSchema.CallToolResult> syncHandler() {
        return (exchange, request) -> functor().apply(exchange, request);
    }
    
    public BiFunction<McpAsyncServerExchange, McpSchema.CallToolRequest, Mono<McpSchema.CallToolResult>> asyncHandler() {
        return (exchange, request) -> Mono.fromCallable(() -> functor().apply(exchange, request));
    }
}
