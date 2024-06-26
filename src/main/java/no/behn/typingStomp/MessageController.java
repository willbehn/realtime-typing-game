package no.behn.typingStomp;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class MessageController {
    //TODO jeg må fjerne etterhvert
    private int tempCounter = 0;
    private Map<String, String> sessions = new ConcurrentHashMap<>(); 

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String clientId = generateClientId();
        sessions.put(sessionId, clientId);
        System.out.println("New WebSocket connection established. Client ID: " + clientId + ", Session ID: " + sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        sessions.remove(sessionId);
        System.out.println("WebSocket connection closed. Session ID: " + sessionId);
    }

    @MessageMapping("/hello")
    @SendTo("/topic/messages")
    public Message greeting(String message, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String clientId = sessions.get(sessionId);

        if (clientId != null) {
            System.out.println("Received message from Client ID: " + clientId);
        } else {
            System.out.println("Received message from unknown session.");
        }

        return new Message(clientId +": "+ message);
    }

    //TODO jeg må fikse bedre client id generering
    private synchronized String generateClientId() {
        return Integer.toString(++tempCounter);
    }
}
