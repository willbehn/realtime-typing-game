package no.behn.typingStomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


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

    //TODO remove if not in use
    /*@PostMapping("/{roomId}/players")
    public void joinRoom(@PathVariable String roomId, @RequestParam String sessionId) {
        roomService.addClientToRoom(roomId, sessionId);
    }*/

    @GetMapping("/{roomId}/text")
    public String getMethodName(@RequestParam String roomId) {
        return roomService.getRoom(roomId).getText();
    }
    
}

