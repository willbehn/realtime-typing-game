package no.behn.typingStomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
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

    private final RoomService roomService;

    @Autowired
    public PositionController(RoomService roomService) {
        this.roomService = roomService;
    }
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        playerCount++;

    }
    
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {  
       playerCount--;
    }

    @MessageMapping("/room/{roomId}")
    @SendTo("/topic/room/{roomId}/positions")
    public Map<String, Integer> handlePosition(@DestinationVariable String roomId, String message, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getFirstNativeHeader("sessionId");
        Room room = roomService.getRoom(roomId);
        Map<String, Integer> roomClientPositions = room.getClientPositions();
        String text = room.getText();

        int position = roomClientPositions.get(sessionId);
        System.out.println("sessionID: " + sessionId + ", position: " + position);

        if (position < text.length() && message.charAt(0) == text.charAt(position)) {
            position++;
            roomClientPositions.put(sessionId, position);
        }
        return roomClientPositions;
    }

    @MessageMapping("/players")
    @SendTo("/topic/players")
    public String sendPlayerCount(){
        return Integer.toString(playerCount);
    }
}
