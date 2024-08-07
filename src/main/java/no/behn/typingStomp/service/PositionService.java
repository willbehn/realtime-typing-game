package no.behn.typingStomp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import no.behn.typingStomp.dto.PositionDto;
import no.behn.typingStomp.model.Room;

import java.util.Map;

@Service
public class PositionService {

    private final RoomService roomService;

    @Autowired
    public PositionService(RoomService roomService) {
        this.roomService = roomService;
    }

    public PositionDto handlePosition(String roomId, String message, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getFirstNativeHeader("sessionId");
        Room room = roomService.getRoom(roomId);

        Map<String, Integer> roomClientPositions = room.getClientPositions();
        String text = room.getText().getTextString();

        if (!roomClientPositions.containsKey(sessionId)){
            return new PositionDto(null, "Access Denied: You do not have access to this room.", "error");
        }

        int position = roomClientPositions.get(sessionId);

        if (position < text.length() && message.charAt(0) == text.charAt(position)) {
            position++;
            roomClientPositions.put(sessionId, position);
        }

        return new PositionDto(roomClientPositions, "Position updated successfully.", "success");
    }
}

