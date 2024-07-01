package no.behn.typingStomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Controller
public class PositionController {
    private int playerCount = 0;
    private Map<String, Integer> clientPositions = new ConcurrentHashMap<>();

    private final RoomService roomService;

    @Autowired
    public PositionController(RoomService roomService) {
        this.roomService = roomService;
    }
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        clientPositions.put(sessionId, 0);
        playerCount++;

        System.out.println("New WebSocket connection established. Session ID: " + sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        clientPositions.remove(sessionId);
        playerCount--;

        System.out.println("WebSocket connection closed. Session ID: " + sessionId);
    }

    @MessageMapping("/room/{roomId}")
    @SendTo("/topic/room/{roomId}/positions")
    public Map<String, Integer> handlePosition(@DestinationVariable String roomId, String message, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Room room = roomService.getRoom(roomId);
        Map<String, Integer> clientPositions = room.getClientPositions();
        String text = room.getText();

        int position = clientPositions.getOrDefault(sessionId, 0);
        System.out.println("sessionID: " + sessionId + ", position: " + position);

        if (position < text.length() && message.charAt(0) == text.charAt(position)) {
            position++;
            clientPositions.put(sessionId, position);
        }
        return clientPositions;
    }

    @MessageMapping("/players")
    @SendTo("/topic/players")
    public String sendPlayerCount(){
        return Integer.toString(playerCount);
    }
}
