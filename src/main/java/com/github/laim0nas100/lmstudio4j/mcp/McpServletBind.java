/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.laim0nas100.lmstudio4j.mcp;

import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import java.util.Objects;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

/**
 *
 * @author Lemmin
 */
public interface McpServletBind {

    public void bind(HttpServletStreamableServerTransportProvider transport);

    public void unbind() throws Exception;

    public boolean isBound();

    public static abstract class AbstractServletBind<SERVLET>
            implements McpServletBind {

        protected SERVLET servlet;

        @Override
        public boolean isBound() {
            return servlet != null;
        }

        protected SERVLET getServlet() {
            if (servlet == null) {
                throw new IllegalStateException("Not bound");
            }
            return servlet;
        }
    }

    public static McpServletBind jetty(Server jetty) {

        Objects.requireNonNull(jetty);

        return new AbstractServletBind<ServletHolder>() {

            private ServletContextHandler context;

            @Override
            public void bind(
                    HttpServletStreamableServerTransportProvider transport) {

                if (isBound()) {
                    throw new IllegalStateException("Already bound");
                }

                context = new ServletContextHandler();
                context.setContextPath("/");

                servlet = context.addServlet(
                        transport,
                        "/mcp/*"
                );

                jetty.setHandler(context);
            }

            @Override
            public void unbind() throws Exception {

                if (!isBound()) {
                    throw new IllegalStateException("Not bound");
                }

                context.stop();
                context.destroy();

                if (jetty.getHandler() == context) {
                    jetty.setHandler((Handler) null);
                }

                servlet = null;
                context = null;
            }
        };
    }

}
