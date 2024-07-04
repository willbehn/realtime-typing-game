package no.behn.typingStomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final TextService textService;

    @Autowired
    public RoomController(RoomService roomService, TextService textService) {
        this.roomService = roomService;
        this.textService = textService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Room createRoom() {
        String text = textService.getText();
        Room room = roomService.createRoom(text);
        System.out.println("Creating room with id: " + room.getId());
        return room;
    }

    @GetMapping("/{roomId}")
    public Room getRoom(@PathVariable String roomId) {
        return roomService.getRoom(roomId);
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable String roomId) {
        if (roomService.getRoom(roomId) != null){
            String sessionId = UUID.randomUUID().toString();
            roomService.addClientToRoom(roomId, sessionId);
            return ResponseEntity.ok(sessionId);

        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found.");
        }
    }

    //TODO not in use
    /*@PostMapping("/{roomId}/leave")
    public ResponseEntity<String> leaveRoom(@PathVariable String roomId, @RequestParam String sessionId) {
        if (roomService.getRoom(roomId) != null){
            //roomService.removeClientFromRoom(roomId, sessionId);
            return ResponseEntity.ok("Room leaves");
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found.");
        }
    }*/
    

    @GetMapping("/{roomId}/text")
    public String getMethodName(@RequestParam String roomId) {
        return roomService.getRoom(roomId).getText();
    }
    
}

