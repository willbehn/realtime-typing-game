package no.behn.typingStomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class StatusController {
    
    private final RoomService roomService;

    @Autowired
    public StatusController(RoomService roomService) {
        this.roomService = roomService;
    }

    @MessageMapping("/room/{roomId}/start")
    @SendTo("/topic/room/{roomId}/status")
    public Boolean startSession(@DestinationVariable String roomId) {
        System.out.println("Starting game in room: " + roomId);
        Room room = roomService.getRoom(roomId);
        if (room != null) {
            room.setStarted();
            return room.getState();
        } else {
            return false;
        }
    }

    @MessageMapping("/room/{roomId}/status")
    @SendTo("/topic/room/{roomId}/status")
    public boolean getSessionStatus(@DestinationVariable String roomId) {
        Room room = roomService.getRoom(roomId);
        if (room != null) {
            boolean gameStarted = room.getState();
            return gameStarted;
        } else {
            return false;
        }
    }


    @MessageMapping("room/{roomId}/players")
    @SendTo("/topic/room/{roomId}/players")
    public String getPlayerCount(@DestinationVariable String roomId){
        return Integer.toString(roomService.getRoom(roomId).getClientCount());
    }
}
