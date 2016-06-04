package eti.bartek.jetty.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hibernate.controller.HibernateController;

import eti.bartek.jetty.servlets.TruckHttpServlet;
import eti.bartek.jetty.servlets.TruckWebSocketServlet;

public class JettyServer {

    public static void main(String[] args) throws Exception {
        
    	HibernateController.init();
    	
        Server server = new Server(10000);
        
        //konfiguracja servletu odpowiadajacego za wyswietlenie strony
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        TruckHttpServlet servlet = new TruckHttpServlet();
        ServletHolder holder = new ServletHolder("Truck", servlet);
        holder.setInitParameter("resourceBase", "./src/main/webapp/");
        
        context.addServlet(holder, "/*");
        
        //dodanie socketa
        
        TruckWebSocketServlet wsServlet = new TruckWebSocketServlet();
        ServletHolder wsHolder = new ServletHolder("socket", wsServlet);
        context.addServlet(wsHolder, "/socket");
        
        server.setHandler(context);
        
        server.start();
        server.join();
    }
    
    
    
}
