package no.behn.typingStomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import no.behn.typingStomp.exception.RoomNotFoundException;

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
        return room;
    }

    @GetMapping("/{roomId}")
    public Room getRoom(@PathVariable String roomId) {
        try {
            return roomService.getRoom(roomId);

        } catch (RoomNotFoundException exc){
            throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Room not found", exc);
       }
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable String roomId) {
        try{
            Room room = roomService.getRoom(roomId);

            //TODO add exception for room in progress not joinable
            if (!room.getState()){
                String sessionId = UUID.randomUUID().toString();
                roomService.addClientToRoom(roomId, sessionId);
                return ResponseEntity.ok(sessionId);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room in progress");
            }

        } catch (RoomNotFoundException exc){
            throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Room not found", exc);
       }
    }

    @PostMapping("/{roomId}/leave")
    public ResponseEntity<String> leaveRoom(@PathVariable String roomId, @RequestParam String sessionId) {
        try {
            roomService.removeClientFromRoom(roomId, sessionId);
            return ResponseEntity.ok("Room leaves");

        } catch (RoomNotFoundException exc){
            throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Room not found", exc);
       }
    }
    

    @GetMapping("/{roomId}/text")
    public String getRoomText(@PathVariable String roomId) {
        try {
            Room room = roomService.getRoom(roomId);
            return room.getText();

        } catch (RoomNotFoundException exc){
            throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Room not found", exc);
       }
    }
}

