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
public record PromptDefinition(McpSchema.Prompt prompt, BiFunction<Object, McpSchema.GetPromptRequest, McpSchema.GetPromptResult> functor) {
    
    public PromptDefinition(McpSchema.Prompt prompt, BiFunction<Object, McpSchema.GetPromptRequest, McpSchema.GetPromptResult> functor) {
        this.prompt = Objects.requireNonNull(prompt);
        this.functor = Objects.requireNonNull(functor);
    }
    
    public BiFunction<McpSyncServerExchange, McpSchema.GetPromptRequest, McpSchema.GetPromptResult> syncHandler() {
        return (exchange, request) -> functor().apply(exchange, request);
    }
    
    public BiFunction<McpAsyncServerExchange, McpSchema.GetPromptRequest, Mono<McpSchema.GetPromptResult>> asyncHandler() {
        return (exchange, request) -> Mono.fromCallable(() -> functor().apply(exchange, request));
    }
}
