package porcelli.me.git.integration.webhook;

import java.io.File;

import com.jcraft.jsch.JSch;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public class WebHook {

    private static final String APPLICATION_PATH = "/api";
    private static final String CONTEXT_ROOT = "/";

    public static void main(String[] args) {
        JSch.setConfig("StrictHostKeyChecking", "no");
        try {
            new WebHook().run();
            System.out.println("WebHook Up and running!");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void run() throws Exception {
        int _port;
        try {
            _port = Integer.valueOf(System.getProperty("port", "9090"));
        } catch (final Exception ex) {
            _port = 9090;
        }
        final int port = _port;
        final Server server = new Server(port);

        // Setup the basic Application "context" at "/".
        // This is also known as the handler tree (in Jetty speak).
        final ServletContextHandler context = new ServletContextHandler(server, CONTEXT_ROOT);

        // Setup RESTEasy's HttpServletDispatcher at "/api/*".
        final ServletHolder restEasyServlet = new ServletHolder(new HttpServletDispatcher());
        restEasyServlet.setInitParameter("resteasy.servlet.mapping.prefix", APPLICATION_PATH);
        restEasyServlet.setInitParameter("javax.ws.rs.Application",
                                         "porcelli.me.git.integration.webhook.WebHookApplication");
        context.addServlet(restEasyServlet, APPLICATION_PATH + "/*");

        // Setup the DefaultServlet at "/".
        final ServletHolder defaultServlet = new ServletHolder(new DefaultServlet());
        context.addServlet(defaultServlet, CONTEXT_ROOT);

        server.start();
        server.join();
    }
}
