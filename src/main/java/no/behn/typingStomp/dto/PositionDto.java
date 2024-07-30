package no.behn.typingStomp.dto;

import java.util.Map;

public class PositionDto {
    private Map<String, Integer> positions;
    private String message;
    private String status;

    public PositionDto(Map<String, Integer> positions, String message, String status) {
        this.positions = positions;
        this.message = message;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

