package no.behn.typingStomp.controller;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import no.behn.typingStomp.exception.RoomNotFoundException;
import no.behn.typingStomp.model.StateDto;
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
    public StateDto startSession(@DestinationVariable String roomId) {
        System.out.println("Starting game in room: " + roomId);
        
        try {
            return roomService.startGameInRoom(roomId);

        } catch (RoomNotFoundException exc) {
            return new StateDto(false, new ConcurrentHashMap<>(), false);
        }
    }

    @MessageMapping("/room/{roomId}/player/{sessionId}/done")
    @SendTo("/topic/room/{roomId}/status")
    public StateDto playerDone(@DestinationVariable String roomId, @DestinationVariable String sessionId) {
        try {
            return roomService.markPlayerAsDone(roomId, sessionId);
        } catch (RoomNotFoundException e) {
            return new StateDto(false, new ConcurrentHashMap<>(), false);
        }
    }


    @MessageMapping("room/{roomId}/players")
    @SendTo("/topic/room/{roomId}/players")
    public String getPlayerCount(@DestinationVariable String roomId){
        try {
            return roomService.getClientCount(roomId);
        } catch (RoomNotFoundException exc) {
            return "0";
        }
    }
}
