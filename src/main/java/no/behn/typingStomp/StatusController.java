package no.behn.typingStomp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public StateDto startSession(@DestinationVariable String roomId) {
        System.out.println("Starting game in room: " + roomId);
        Room room = roomService.getRoom(roomId);
        if (room != null) {
            room.setStarted();
            return new StateDto(room.getState(), new ConcurrentHashMap<>(), room.getDone());

        } else {
            return new StateDto(false, new ConcurrentHashMap<>(), false);
        }
    }

    @MessageMapping("/room/{roomId}/player/{sessionId}/done")
    @SendTo("/topic/room/{roomId}/status")
    public StateDto playerDone(@DestinationVariable String roomId, @DestinationVariable String sessionId) {
        Room room =roomService.getRoom(roomId);
        if (room != null) {
            room.setDone();

            // Logic to mark player as done
            Map<String, Integer> endTime = new HashMap<>();
            endTime.put(sessionId, 200); // Example endTime
            return new StateDto(room.getState(), endTime, room.getDone());

        } else {
            return new StateDto(false, new ConcurrentHashMap<>(), false);
        }
    }


    @MessageMapping("room/{roomId}/players")
    @SendTo("/topic/room/{roomId}/players")
    public String getPlayerCount(@DestinationVariable String roomId){
        return Integer.toString(roomService.getRoom(roomId).getClientCount());
    }
}
