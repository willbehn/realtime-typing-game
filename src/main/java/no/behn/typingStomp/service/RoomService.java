package no.behn.typingStomp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import no.behn.typingStomp.dto.StateResponseDto;
import no.behn.typingStomp.exception.RoomNotFoundException;
import no.behn.typingStomp.model.Room;
import no.behn.typingStomp.model.Text;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    public Room createRoom(Text text) {
        String roomId = generateRoomId();
        Room room = new Room(roomId, text);
        rooms.put(roomId, room);

        log.info("Creating room with id: " + roomId);
        return room;
    }

    public Room getRoom(String roomId) {
        Room room = rooms.get(roomId);

        if (room != null){
            return room;
        } else throw new RoomNotFoundException("Room with id: " + roomId + " not found");
    }

    public StateResponseDto getRoomStatus(String roomId){
        Room room = getRoom(roomId);
        return new StateResponseDto(room.getState(), room.getClientEndtimes(), room.getDone(), room.getPlayerNames(), room.getPlayerCount());
        
    }

    public StateResponseDto startGameInRoom(String roomId) {
        Room room = getRoom(roomId);

        room.setStarted();
        log.info(room.getStartTime() + ": Started room with id: " + roomId);
        return new StateResponseDto(room.getState(), room.getClientEndtimes(), room.getDone(), room.getPlayerNames(), room.getPlayerCount());
        
    }

    public StateResponseDto markPlayerAsDone(String roomId, String sessionId) {
        Room room = getRoom(roomId);

        room.setDone();
        room.addClientEndTime(sessionId, getWordsPerMinute(room.getText().getWordCount(), room.getDurationInSeconds())); //TODO få antall ord fra text objekt
        return new StateResponseDto(room.getState(), room.getClientEndtimes(), room.getDone(), room.getPlayerNames(), room.getPlayerCount());
    }

    public String addClientToRoom(String roomId, String playerName) {
        Room room = getRoom(roomId);
        
        String sessionId = UUID.randomUUID().toString();
        log.info("Adding client with sessionId: " + sessionId + " to roomId: " + roomId);
        room.addClient(sessionId, playerName);
        return sessionId;
    }

    public ResponseEntity<String>removeClientFromRoom(String roomId, String sessionId) {
        Room room = getRoom(roomId);
        log.info("Removing client with sessionId: " + sessionId + " from roomId: " + roomId);
        room.removeClient(sessionId);

        // Removes the room if there are no players remaining
        if (room.getPlayerCount() == 0){
            log.info("No players left in room with id " + roomId +", deleting room");
            rooms.remove(roomId);
        }

        return ResponseEntity.ok("Room left successfully");
    }

    public String getPlayerCount(String roomId){
        return Integer.toString(getRoom(roomId).getPlayerCount());
    }

    public Text getRoomText(String roomId){
        return getRoom(roomId).getText();
    }

    private String generateRoomId() {
        return UUID.randomUUID().toString();
    }

    public long getWordsPerMinute(int wordCount, long timeInSeconds) {
        if (timeInSeconds == 0) {
            throw new IllegalArgumentException("Time cannot be zero.");
        }
        
        double timeInMinutes = timeInSeconds / 60.0;
        int wordsPerMinute = (int) (wordCount / timeInMinutes);
        
        return wordsPerMinute;
    }
}

