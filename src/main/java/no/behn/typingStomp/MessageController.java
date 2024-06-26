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

    //TODO mby remove
    private static final String FIXED_TEXT = "Dette er en veldig kul text om et eller annet kult hihi";
    private Map<String, Integer> clientPositions = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        clientPositions.put(sessionId, 0);


        System.out.println("New WebSocket connection established. Session ID: " + sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        clientPositions.remove(sessionId);

        System.out.println("WebSocket connection closed. Session ID: " + sessionId);
    }

    @MessageMapping("/hello")
    @SendTo("/topic/messages")
    public Map<String, Integer> greeting(String message, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        int position = clientPositions.getOrDefault(sessionId, 0);
        System.out.println("Position: " + position);

        // Check if the typed letter is correct
        if (position < FIXED_TEXT.length() && message.charAt(0) == FIXED_TEXT.charAt(position)) {
            position++;
            clientPositions.put(sessionId, position);
        }

        // Return all client positions
        return clientPositions;
    }


    @MessageMapping("/fixed-text")
    @SendTo("/topic/fixed-text")
    public String sendFixedText() {
        return FIXED_TEXT;
    }
}
