package com.github.laim0nas100.lmstudio4j.mcp;

import com.github.laim0nas100.uncheckedutils.SafeOpt;
import io.modelcontextprotocol.server.McpAsyncServerExchange;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.Content;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
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

    public static BiFunction<Object, McpSchema.CallToolRequest, McpSchema.CallToolResult> simpleText(Function<McpSchema.CallToolRequest, SafeOpt<String>> functor) {

        return simple(request -> functor.apply(request).map(str -> McpSchema.TextContent.builder(str).build()));
    }

    public static BiFunction<Object, McpSchema.CallToolRequest, McpSchema.CallToolResult> simple(Function<McpSchema.CallToolRequest, SafeOpt<Content>> functor) {
        Objects.requireNonNull(functor);
        return (exchange, request) -> {
            SafeOpt<Content> apply = functor.apply(request);
            if (apply.isPresent()) {
                Content content = apply.get();
                return McpSchema.CallToolResult.builder(List.of(content)).build();
            } else {
                if (apply.hasError()) {
                    Throwable error = apply.getError().get();
                    return McpSchema.CallToolResult.builder(
                            List.of(McpSchema.TextContent.builder(error.getMessage()).build()))
                            .isError(Boolean.TRUE).build();
                } else {// just empty
                    return McpSchema.CallToolResult.builder(List.of()).build();
                }
            }

        };
    }
}
