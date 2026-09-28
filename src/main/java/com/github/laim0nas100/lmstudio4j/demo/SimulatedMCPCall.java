/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.laim0nas100.lmstudio4j.demo;

import com.github.laim0nas100.lmstudio4j.mcp.AttachmentMcpServer;
import com.github.mizosoft.methanol.Methanol;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

/**
 *
 * @author Lemmin
 */
public class SimulatedMCPCall {

    public static void main(String[] args) throws IOException, Exception {
        System.out.println("Hello World!");

        var jetty = new Server();

        ServerConnector connector
                = new ServerConnector(jetty);

        connector.setHost("127.0.0.1");
        connector.setPort(1337);

        jetty.addConnector(connector);

        AttachmentMcpServer attachmentMcpServer = new AttachmentMcpServer(jetty);
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
