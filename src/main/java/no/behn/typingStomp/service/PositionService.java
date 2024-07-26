package no.behn.typingStomp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import no.behn.typingStomp.model.Room;

import java.util.Map;

@Service
public class PositionService {

    private final RoomService roomService;

    @Autowired
    public PositionService(RoomService roomService) {
        this.roomService = roomService;
    }

    public Map<String, Integer> handlePosition(String roomId, String message, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getFirstNativeHeader("sessionId");
        Room room = roomService.getRoom(roomId);
        Map<String, Integer> roomClientPositions = room.getClientPositions();
        String text = room.getText().getTextString();

        int position = roomClientPositions.get(sessionId);

        if (position < text.length() && message.charAt(0) == text.charAt(position)) {
            position++;
            roomClientPositions.put(sessionId, position);
        }
        return roomClientPositions;
    }
}

