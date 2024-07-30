package no.behn.typingStomp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import no.behn.typingStomp.dto.JoinRoomRequest;
import no.behn.typingStomp.exception.RoomNotFoundException;
import no.behn.typingStomp.model.Room;
import no.behn.typingStomp.model.Text;
import no.behn.typingStomp.service.RoomService;
import no.behn.typingStomp.service.TextService;

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
        Text text = textService.getText();
        return roomService.createRoom(text);
        
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
    public ResponseEntity<String> joinRoom(@PathVariable String roomId, @RequestBody JoinRoomRequest joinRoomRequest, HttpServletResponse response) {
        try {
            String playerName = joinRoomRequest.getPlayerName();
            
            String sessionId = roomService.addClientToRoom(roomId, playerName);

            // TODO cookies is correctly set when joining
            Cookie sessionCookie = new Cookie("sessionId", sessionId);
            sessionCookie.setPath("/"); //which paths
            sessionCookie.setHttpOnly(false); //For restrictiing js access, set to true when working
            sessionCookie.setMaxAge(10000); 
            response.addCookie(sessionCookie);
            return ResponseEntity.ok("{\"id\":\"" + sessionId + "\"}");
        
        } catch (RoomNotFoundException exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found", exc);
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
    public Text getRoomText(@PathVariable String roomId) {
        try {
            return roomService.getRoomText(roomId);

        } catch (RoomNotFoundException exc){
            throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Room not found", exc);
       }
    }
}

