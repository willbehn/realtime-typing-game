package no.behn.typingStomp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import no.behn.typingStomp.model.Room;
import no.behn.typingStomp.service.RoomService;

import java.util.Map;


@Controller
public class PositionController {
    private final RoomService roomService;

    @Autowired
    public PositionController(RoomService roomService) {
        this.roomService = roomService;
    }
   
    @MessageMapping("/room/{roomId}")
    @SendTo("/topic/room/{roomId}/positions")
    public Map<String, Integer> handlePosition(@DestinationVariable String roomId, String message, StompHeaderAccessor headerAccessor) {
        //TODO move to a PositionService
        String sessionId = headerAccessor.getFirstNativeHeader("sessionId");
        Room room = roomService.getRoom(roomId);
        Map<String, Integer> roomClientPositions = room.getClientPositions();
        String text = room.getText().getTextString();

        int position = roomClientPositions.get(sessionId);
        System.out.println("sessionID: " + sessionId + ", position: " + position);

        if (position < text.length() && message.charAt(0) == text.charAt(position)) {
            position++;
            roomClientPositions.put(sessionId, position);
        }
        return roomClientPositions;
    }
}
