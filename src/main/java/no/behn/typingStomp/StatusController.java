package no.behn.typingStomp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import no.behn.typingStomp.exception.RoomNotFoundException;

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
            return Integer.toString(roomService.getRoom(roomId).getClientCount());
        } catch (RoomNotFoundException exc) {
            return "0";
        }
    }
}
