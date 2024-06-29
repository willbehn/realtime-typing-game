package no.behn.typingStomp;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import jakarta.annotation.PostConstruct;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

@Controller
public class PositionController {

    private String text = "Dette er et avsnitt for testing. Dette er da et veldig kult avsnitt som er veldig kult!";
    private Map<String, Integer> clientPositions = new ConcurrentHashMap<>();


    /*public PositionController() throws IOException {
        //Path filePath = Paths.get("src/main/resources/testParagraph.txt");
        //text = new String(Files.readAllBytes(filePath));
        text = "Dette er en test tekst hihihihi. Denne skal egentlig være mye lengre";
    }*/

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
    @SendTo("/topic/positions")
    public Map<String, Integer> handlePosition(String message, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        int position = clientPositions.getOrDefault(sessionId, 0);
        System.out.println("sessionID: " + sessionId + ", position: " + position);

        if (position < text.length() && message.charAt(0) == text.charAt(position)) {
            position++;
            clientPositions.put(sessionId, position);
        }
        return clientPositions;
    }


    @MessageMapping("/fixed-text")
    @SendTo("/topic/fixed-text")
    public String sendFixedText() {
        return text;
    }
}
