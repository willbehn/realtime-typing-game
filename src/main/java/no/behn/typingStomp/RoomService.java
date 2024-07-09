package no.behn.typingStomp;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private Map<String, Room> rooms = new ConcurrentHashMap<>();

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
            System.out.println("Adding client with sessionId: " + sessionId + " to roomId: " + roomId);
            room.addClient(sessionId);;
        }
    }

    public void removeClientFromRoom(String roomId, String sessionId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            System.out.println("Removing client with sessionId: " + sessionId + " from roomId: " + roomId);
            room.removeClient(sessionId);

            // Removes the room if there are no players remaining
            if (room.getClientCount() == 0){
                rooms.remove(roomId);
            }
        }
    }

    private String generateRoomId() {
        return UUID.randomUUID().toString();
    }
}

