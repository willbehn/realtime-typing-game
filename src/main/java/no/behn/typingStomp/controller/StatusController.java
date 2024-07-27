package no.behn.typingStomp.controller;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import no.behn.typingStomp.dto.StateResponseDto;
import no.behn.typingStomp.exception.RoomNotFoundException;
import no.behn.typingStomp.service.RoomService;

@Controller
public class StatusController {
    
    private final RoomService roomService;

    @Autowired
    public StatusController(RoomService roomService) {
        this.roomService = roomService;
    }

    @MessageMapping("/room/{roomId}/start")
    @SendTo("/topic/room/{roomId}/status")
    public StateResponseDto startSession(@DestinationVariable String roomId) {
        System.out.println("Starting game in room: " + roomId);
        
        try {
            return roomService.startGameInRoom(roomId);

        } catch (RoomNotFoundException exc) {
            return new StateResponseDto(false, new ConcurrentHashMap<>(), false,new ConcurrentHashMap<>(),0);
        }
    }

    @MessageMapping("/room/{roomId}/status")
    @SendTo("/topic/room/{roomId}/status")
    public StateResponseDto getStatus(@DestinationVariable String roomId) {
        
        try {
            return roomService.getRoomStatus(roomId);

        } catch (RoomNotFoundException exc) {
            return new StateResponseDto(false, new ConcurrentHashMap<>(), false,new ConcurrentHashMap<>(),0);
        }
    }

    @MessageMapping("/room/{roomId}/player/{sessionId}/done")
    @SendTo("/topic/room/{roomId}/status")
    public StateResponseDto playerDone(@DestinationVariable String roomId, @DestinationVariable String sessionId) {
        try {
            return roomService.markPlayerAsDone(roomId, sessionId);
        } catch (RoomNotFoundException e) {
            return new StateResponseDto(false, new ConcurrentHashMap<>(), false,new ConcurrentHashMap<>(),0);
        }
    }
}
