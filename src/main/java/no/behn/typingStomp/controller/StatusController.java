package no.behn.typingStomp.controller;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import no.behn.typingStomp.dto.StateResponseDto;
import no.behn.typingStomp.exception.RoomNotFoundException;
import no.behn.typingStomp.service.RoomService;

@Controller
public class StatusController {

    private static final Logger log = LoggerFactory.getLogger(StatusController.class);
    private final RoomService roomService;

    @Autowired
    public StatusController(RoomService roomService) {
        this.roomService = roomService;
    }

    @MessageMapping("/room/{roomId}/start")
    @SendTo("/topic/room/{roomId}/status")
    public StateResponseDto startSession(@DestinationVariable String roomId, StompHeaderAccessor headerAccessor) {
        log.info("Starting game in room: {}", roomId);

        try {
            return roomService.startGameInRoom(roomId, headerAccessor);
        } catch (RoomNotFoundException exc) {
            log.error("Room not found: {}", roomId, exc);
            return createErrorResponse();
        }
    }

    @MessageMapping("/room/{roomId}/status")
    @SendTo("/topic/room/{roomId}/status")
    public StateResponseDto getStatus(@DestinationVariable String roomId, StompHeaderAccessor headerAccessor) {
        log.info("Fetching status for room: {}", roomId);

        try {
            return roomService.getRoomStatus(roomId, headerAccessor);
        } catch (RoomNotFoundException exc) {
            log.error("Room not found: {}", roomId, exc);
            return createErrorResponse();
        }
    }

    @MessageMapping("/room/{roomId}/done")
    @SendTo("/topic/room/{roomId}/status")
    public StateResponseDto playerDone(@DestinationVariable String roomId, StompHeaderAccessor headerAccessor) {
        log.info("Player marked as done in room: {}", roomId);

        try {
            return roomService.markPlayerAsDone(roomId, headerAccessor);
        } catch (RoomNotFoundException exc) {
            log.error("Room not found: {}", roomId, exc);
            return createErrorResponse();
        }
    }

    private StateResponseDto createErrorResponse() {
        return new StateResponseDto(false, new ConcurrentHashMap<>(), false, new ConcurrentHashMap<>(), 0, "error");
    }
}
