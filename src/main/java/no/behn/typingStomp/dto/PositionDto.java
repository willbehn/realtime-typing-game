package no.behn.typingStomp.dto;

import java.util.Map;

public class PositionDto {
    private Map<String, Integer> positions;
    private String message;

    public PositionDto(Map<String, Integer> positions, String message) {
        this.positions = positions;
        this.message = message;
    }

    public Map<String, Integer> getPositions() {
        return positions;
    }

    public void setPositions(Map<String, Integer> positions) {
        this.positions = positions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

