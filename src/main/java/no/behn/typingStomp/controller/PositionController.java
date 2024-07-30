package no.behn.typingStomp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import no.behn.typingStomp.dto.PositionDto;
import no.behn.typingStomp.service.PositionService;


@Controller
public class PositionController {
    private final PositionService positionService;

    @Autowired
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @MessageMapping("/room/{roomId}")
    @SendTo("/topic/room/{roomId}/positions")
    public PositionDto handlePosition(@DestinationVariable String roomId, String message, StompHeaderAccessor headerAccessor) {
        String sessionId = (String) headerAccessor.getSessionAttributes().get("sessionId");
        System.out.println("Session ID from WebSocket: " + sessionId);
        return positionService.handlePosition(roomId, message, headerAccessor);
    }
}

