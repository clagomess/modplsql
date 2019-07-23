package com.github.clagomess.modplsql.server;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.servlet.ServletContainer;

@Slf4j
public class ModPlsqlServer {
    public static void run(){
        new Thread(() -> {
            Server server = new Server(8000);

            // Contexto
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");

            server.setHandler(context);

            // Servlet
            ServletHolder jerseyServlet = context.addServlet(
                    ServletContainer.class,
                    "/*"
            );

            FilterHolder filterHolder = new FilterHolder(CrossOriginFilter.class);
            filterHolder.setInitParameter("allowedOrigins", "*");
            filterHolder.setInitParameter("allowedMethods", "GET, POST");
            context.addFilter(filterHolder, "/*", null);

            jerseyServlet.setInitOrder(0);

            jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", ModPlsqlController.class.getCanonicalName());

            try {
                server.start();
                server.join();
            } catch (Throwable e) {
                log.error(ModPlsqlServer.class.getName(), e);
            }
        }).start();
    }
}
