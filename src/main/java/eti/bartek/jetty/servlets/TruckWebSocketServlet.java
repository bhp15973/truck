package eti.bartek.jetty.servlets;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import eti.bartek.jetty.websockets.TruckWebSocket;

public class TruckWebSocketServlet extends WebSocketServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void configure(WebSocketServletFactory factory) {
            factory.register(TruckWebSocket.class);
    }
}
