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
public record ResourceDefinition(McpSchema.Resource resource, BiFunction<Object, McpSchema.ReadResourceRequest, McpSchema.ReadResourceResult> functor) {
    
    public ResourceDefinition(McpSchema.Resource resource, BiFunction<Object, McpSchema.ReadResourceRequest, McpSchema.ReadResourceResult> functor) {
        this.resource = Objects.requireNonNull(resource);
        this.functor = Objects.requireNonNull(functor);
    }
    
    public BiFunction<McpSyncServerExchange, McpSchema.ReadResourceRequest, McpSchema.ReadResourceResult> syncHandler() {
        return (exchange, request) -> functor().apply(exchange, request);
    }
    
    public BiFunction<McpAsyncServerExchange, McpSchema.ReadResourceRequest, Mono<McpSchema.ReadResourceResult>> asyncHandler() {
        return (exchange, request) -> Mono.fromCallable(() -> functor().apply(exchange, request));
    }
}
