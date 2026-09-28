/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.laim0nas100.lmstudio4j.demo;

import com.github.laim0nas100.lmstudio4j.mcp.GenericMcpServer;
import com.github.laim0nas100.lmstudio4j.mcp.McpServletBind;
import com.github.laim0nas100.lmstudio4j.mcp.ToolDefinition;
import com.github.laim0nas100.uncheckedutils.SafeOpt;
import com.github.mizosoft.methanol.Methanol;
import io.modelcontextprotocol.spec.McpSchema;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

/**
 *
 * @author Lemmin
 */
public class SimulatedMCPCall {
    
    private static McpSchema.Tool createReadAttachmentTool() {

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

        return McpSchema.Tool.builder(
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

    public static void main(String[] args) throws IOException, Exception {
        System.out.println("Hello World!");

        var jetty = new Server();

        ServerConnector connector
                = new ServerConnector(jetty);

        connector.setHost("127.0.0.1");
        connector.setPort(1337);

        jetty.addConnector(connector);

//        AttachmentMcpServer attachmentMcpServer = new AttachmentMcpServer(jetty);
        GenericMcpServer build = new GenericMcpServer.Builder().withTool(createReadAttachmentTool(), ToolDefinition.simpleText(request ->{
            return SafeOpt.of("FILE TEMPLATE");
        })).build();
        
        build.bind(McpServletBind.jetty(jetty));
        jetty.start();

        String sessionID = callMCP();
        sendInitialized(sessionID);
        toolCall(sessionID);
        readAttachment(sessionID);

    }

    static HttpClient client = Methanol.newHttpClient();

    public static String callMCP() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:1337/mcp"))
                .header("Accept", "application/json, text/event-stream")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                {
                  "jsonrpc": "2.0",
                  "id": 1,
                  "method": "initialize",
                  "params": {
                    "protocolVersion": "2025-03-26",
                    "capabilities": {},
                    "clientInfo": {
                      "name": "test-client",
                      "version": "1.0.0"
                    }
                  }
                }
                """))
                .build();

        HttpResponse<String> response
                = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        String sessionID = response.headers().firstValue("Mcp-Session-Id").orElse("<none>");
        System.out.println("Session: " + sessionID);
        System.out.println("Body:");
        System.out.println(response.body());

        return sessionID;
    }

    public static void sendInitialized(String sessionID) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:1337/mcp"))
                .header("Accept", "application/json, text/event-stream")
                .header("Content-Type", "application/json")
                .header("Mcp-Session-Id", sessionID)
                .POST(HttpRequest.BodyPublishers.ofString("""
                    {
                      "jsonrpc": "2.0",
                      "method": "notifications/initialized"
                    }
                    """))
                .build();

        HttpResponse<String> response
                = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        System.out.println("initialized status: "
                + response.statusCode());

        System.out.println("initialized body: "
                + response.body());
    }

    public static void toolCall(String sessionID) throws Exception {

        HttpRequest toolsRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:1337/mcp"))
                .header("Accept", "application/json, text/event-stream")
                .header("Content-Type", "application/json")
                .header("Mcp-Session-Id", sessionID)
                .POST(HttpRequest.BodyPublishers.ofString("""
                {
                  "jsonrpc": "2.0",
                  "id": 2,
                  "method": "tools/list",
                  "params": {}
                }
                """))
                .build();

        HttpResponse<String> toolsResponse
                = client.send(
                        toolsRequest,
                        HttpResponse.BodyHandlers.ofString()
                );

        System.out.println(toolsResponse.body());
    }

    public static void readAttachment(String sessionID) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:1337/mcp"))
                .header("Accept", "application/json, text/event-stream")
                .header("Content-Type", "application/json")
                .header("Mcp-Session-Id", sessionID)
                .header("MCP-Protocol-Version", "2025-03-26")
                .POST(HttpRequest.BodyPublishers.ofString("""
                    {
                      "jsonrpc": "2.0",
                      "id": 3,
                      "method": "tools/call",
                      "params": {
                        "name": "read_attachment",
                        "arguments": {
                          "file_id": "7d8e3a"
                        }
                      }
                    }
                    """))
                .build();

        HttpResponse<String> response
                = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        System.out.println("Status: " + response.statusCode());
        System.out.println(response.body());
    }
}
