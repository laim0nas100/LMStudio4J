/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
public record CompletionDefinition(McpSchema.CompleteReference reference, BiFunction<Object, McpSchema.CompleteRequest, McpSchema.CompleteResult> functor) {
    
    public CompletionDefinition(McpSchema.CompleteReference reference, BiFunction<Object, McpSchema.CompleteRequest, McpSchema.CompleteResult> functor) {
        this.reference = Objects.requireNonNull(reference);
        this.functor = Objects.requireNonNull(functor);
    }
    
    public BiFunction<McpSyncServerExchange, McpSchema.CompleteRequest, McpSchema.CompleteResult> syncHandler() {
        return (exchange, request) -> functor().apply(exchange, request);
    }
    
    public BiFunction<McpAsyncServerExchange, McpSchema.CompleteRequest, Mono<McpSchema.CompleteResult>> asyncHandler() {
        return (exchange, request) -> Mono.fromCallable(() -> functor().apply(exchange, request));
    }
}
