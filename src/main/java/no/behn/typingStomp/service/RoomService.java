package no.behn.typingStomp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import no.behn.typingStomp.dto.PositionDto;
import no.behn.typingStomp.dto.StateResponseDto;
import no.behn.typingStomp.exception.RoomNotFoundException;
import no.behn.typingStomp.model.Room;
import no.behn.typingStomp.model.Text;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    private static final String SUCCESS_STATUS = "success";
    private static final String ERROR_STATUS = "error";

    public Room createRoom(Text text) {
        String roomId = generateRoomId();
        Room room = new Room(roomId, text);
        rooms.put(roomId, room);

        log.info("Creating room with id: {}", roomId);
        return room;
    }

    public Room getRoom(String roomId) {
        Room room = rooms.get(roomId);

        if (room != null) {
            return room;
        } else {
            throw new RoomNotFoundException("Room with id: " + roomId + " not found");
        }
    }

    public StateResponseDto getRoomStatus(String roomId, StompHeaderAccessor headerAccessor) {
        Room room = getRoom(roomId);
        return createStateResponseDto(room, SUCCESS_STATUS);
    }

    public StateResponseDto startGameInRoom(String roomId, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getFirstNativeHeader("sessionId");
        Room room = getRoom(roomId);

        if (!room.validateSessionId(sessionId)) {
            return createStateResponseDto(room, ERROR_STATUS);
        }

        room.setStarted();
        log.info("{}: Started room with id: {}", room.getStartTime(), roomId);
        return createStateResponseDto(room, SUCCESS_STATUS);
    }

    public StateResponseDto markPlayerAsDone(String roomId, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getFirstNativeHeader("sessionId");
        Room room = getRoom(roomId);

        if (!room.validateSessionId(sessionId)) {
            return createStateResponseDto(room, ERROR_STATUS);
        }

        room.setDone();
        room.addClientEndTime(sessionId, getWordsPerMinute(room.getText().getWordCount(), room.getDurationInSeconds()));
        return createStateResponseDto(room, SUCCESS_STATUS);
    }

    public String addClientToRoom(String roomId, String playerName) {
        Room room = getRoom(roomId);
        
        String sessionId = UUID.randomUUID().toString();
        log.info("Adding client with sessionId: {} to roomId: {}", sessionId, roomId);
        room.addClient(sessionId, playerName);
        return sessionId;
    }

    public ResponseEntity<String> removeClientFromRoom(String roomId, String sessionId) {
        Room room = getRoom(roomId);
        log.info("Removing client with sessionId: {} from roomId: {}", sessionId, roomId);
        room.removeClient(sessionId);

        // Remove the room if there are no players remaining
        if (room.getPlayerCount() == 0) {
            log.info("No players left in room with id {}, deleting room", roomId);
            rooms.remove(roomId);
        }

        return ResponseEntity.ok("Room left successfully");
    }

    public String getPlayerCount(String roomId) {
        return Integer.toString(getRoom(roomId).getPlayerCount());
    }

    public Text getRoomText(String roomId) {
        return getRoom(roomId).getText();
    }

    private String generateRoomId() {
        return UUID.randomUUID().toString();
    }

    private long getWordsPerMinute(int wordCount, long timeInSeconds) {
        if (timeInSeconds == 0) {
            throw new IllegalArgumentException("Time cannot be zero.");
        }

        double timeInMinutes = timeInSeconds / 60.0;
        return (int) (wordCount / timeInMinutes);
    }

    private StateResponseDto createStateResponseDto(Room room, String status) {
        return new StateResponseDto(
            room.getState(), 
            room.getClientEndtimes(), 
            room.getDone(), 
            room.getPlayerNames(), 
            room.getPlayerCount(), 
            status
        );
    }
}


