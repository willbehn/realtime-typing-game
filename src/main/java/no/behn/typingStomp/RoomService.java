package no.behn.typingStomp;

import org.springframework.stereotype.Service;

import no.behn.typingStomp.exception.RoomNotFoundException;

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
        Room room = rooms.get(roomId);

        if (room != null){
            return room;
        } else throw new RoomNotFoundException("Room with id: " + roomId + " not found");
    }

    public StateDto startGameInRoom(String roomId) {
        Room room = getRoom(roomId);

        room.setStarted();
        System.out.println(room.getStartTime() + ": Started room with id: " + roomId);
        return new StateDto(room.getState(), room.getClientEndtimes(), room.getDone());
        
    }

    public StateDto markPlayerAsDone(String roomId, String sessionId) {
        Room room = getRoom(roomId);

        room.setDone();
        room.addClientEndTime(sessionId, room.getDurationInSeconds());
        return new StateDto(room.getState(), room.getClientEndtimes(), room.getDone());
    }

    public String addClientToRoom(String roomId) {
        Room room = rooms.get(roomId);
        
        String sessionId = UUID.randomUUID().toString();
        System.out.println("Adding client with sessionId: " + sessionId + " to roomId: " + roomId);
        room.addClient(sessionId);
        return sessionId;
    }

    public void removeClientFromRoom(String roomId, String sessionId) {
        Room room = rooms.get(roomId);
        System.out.println("Removing client with sessionId: " + sessionId + " from roomId: " + roomId);
        room.removeClient(sessionId);

        // Removes the room if there are no players remaining
        if (room.getClientCount() == 0){
            rooms.remove(roomId);
        }
    }

    public String getClientCount(String roomId){
        return Integer.toString(getRoom(roomId).getClientCount());
    }

    public String getRoomText(String roomId){
        return getRoom(roomId).getText();
    }

    private String generateRoomId() {
        return UUID.randomUUID().toString();
    }
}

