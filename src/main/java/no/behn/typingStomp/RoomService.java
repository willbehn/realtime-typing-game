package no.behn.typingStomp;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RoomService {

    private Map<String, Room> rooms = new HashMap<>();

    public Room createRoom(String text) {
        String roomId = generateRoomId();
        Room room = new Room(roomId, text);
        rooms.put(roomId, room);

        System.out.println("Creating room with id: " + roomId);
        return room;
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public void addClientToRoom(String roomId, String sessionId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.addClient(sessionId);;
        }
    }

    private String generateRoomId() {
        return UUID.randomUUID().toString();
    }
}

