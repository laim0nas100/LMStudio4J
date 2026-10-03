package com.github.laim0nas100.lmstudio4j.demo;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import io.modelcontextprotocol.spec.McpSchema.Tool;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;

import java.util.List;
import java.util.Map;

public final class AttachmentMcpServer {

    protected final McpSyncServer mcpServer;
    protected final HttpServletStreamableServerTransportProvider transport;
    protected boolean isBound;

    public AttachmentMcpServer(Server jetty) {

        /*
         * ------------------------------------------------------------
         * 1. MCP HTTP transport
         * ------------------------------------------------------------
         *
         * This is the actual MCP Streamable HTTP endpoint.
         *
         * LM Studio will connect to:
         *
         *     http://127.0.0.1:<port>/mcp
         *
         * The MCP SDK handles JSON-RPC, sessions, tool discovery,
         * tool calls, etc.
         */
        transport
                = HttpServletStreamableServerTransportProvider.builder()
                        .mcpEndpoint("/mcp")
                        .build();


        /*
         * ------------------------------------------------------------
         * 2. Define the MCP server
         * ------------------------------------------------------------
         */
        this.mcpServer = McpServer.sync(transport)
                .serverInfo(
                        "attachment-file-server",
                        "1.0.0"
                )
                .capabilities(
                        ServerCapabilities.builder()
                                .tools(false)
                                .build()
                )
                /*
                 * ----------------------------------------------------
                 * 3. Register our tool
                 * ----------------------------------------------------
                 */
                .toolCall(
                        createReadAttachmentTool(),
                        (exchange, request) -> {

                            /*
                             * Arguments supplied by the LLM:
                             *
                             * {
                             *     "file_id": "abc123"
                             * }
                             */
                            Object value
                            = request.arguments().get("file_id");

                            if (value == null) {

                                return CallToolResult.builder(
                                        List.of(new McpSchema.TextContent(null, "Missing required argument: file_id", null)))
                                        .isError(true)
                                        .build();
                            }

                            String fileId = value.toString();

                            try {

                                /*
                                 * YOUR IMPLEMENTATION GOES HERE
                                 */
                                String contents
                                = readAttachment(fileId);


                                /*
                                 * Give the file contents back to
                                 * LM Studio / the model.
                                 */
                                return CallToolResult.builder()
                                        .content(List.of(
                                                new McpSchema.TextContent(
                                                        contents
                                                )
                                        ))
                                        .isError(false)
                                        .build();

                            } catch (Exception e) {

                                /*
                                 * This is a tool-level error.
                                 * The model receives the message and
                                 * can potentially react to it.
                                 */
                                return CallToolResult.builder()
                                        .content(List.of(
                                                new McpSchema.TextContent(
                                                        "Unable to read attachment: "
                                                        + e.getMessage()
                                                )
                                        ))
                                        .isError(true)
                                        .build();
                            }
                        }
                )
                .build();

    }

    public AttachmentMcpServer bindJetty(Server jetty) {
        /*
         * ServletContextHandler lets us run the MCP servlet
         * directly from our Java process.
         *
         * No WAR.
         * No Spring.
         * No external server.
         */
        ServletContextHandler context
                = new ServletContextHandler();

        context.setContextPath("/");


        /*
         * The MCP SDK transport itself is a Servlet.
         *
         * Map it to /mcp/*
         */
        context.addServlet(
                transport,
                "/mcp/*"
        );

        jetty.setHandler(context);
        
        
        return this;
    }

    /**
     * Defines the tool visible to the LLM.
     */
    private static Tool createReadAttachmentTool() {

        /*
         * JSON Schema describing:
         *
         * {
         *     "file_id": "..."
         * }
         *
         * The SDK validates incoming tool arguments against
         * this schema by default.
         */
        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "file_id", Map.of(
                                "type", "string",
                                "description",
                                "ID of the file attached to the current chat session."
                        )
                ),
                "required", List.of("file_id")
        );

        return Tool.builder(
                "read_attachment",
                schema
        )
                .description(
                        """
                        Reads the contents of a file that was attached \
                        by the user to the current chat session.

                        Use this tool when you need to inspect the \
                        contents of an attached file.

                        The file_id is supplied by the application in \
                        the conversation context.
                        """
                )
                .build();
    }

    /**
     * Replace this with your real file lookup.
     */
    private static String readAttachment(String fileId)
            throws Exception {

        /*
         * TODO:
         *
         * Look up the file using fileId.
         *
         * For example:
         *
         * Path file = attachmentDirectory.resolve(fileId);
         *
         * return Files.readString(file);
         */
        return """
                THIS IS A PLACEHOLDER FILE.

                The requested file ID was:

                    %s

                Replace readAttachment() with your actual
                attachment lookup implementation.
                """.formatted(fileId);
    }

    /**
     * Stop the server.
     */
    public void stop() throws Exception {
        mcpServer.close();
    }

}
