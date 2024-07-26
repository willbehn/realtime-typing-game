package no.behn.typingStomp.dto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StateResponseDto {
    private boolean gameStarted;
    private boolean isDone;
    private Map<String, Long> endTime = new ConcurrentHashMap<>();
    private Map<String, String> playerNames = new ConcurrentHashMap<>();

    public StateResponseDto(boolean gameStarted, Map<String, Long> endTime, boolean isDone, Map<String,String> playerNames) {
        this.gameStarted = gameStarted;
        this.endTime = endTime;
        this.isDone = isDone;
        this.playerNames = playerNames;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public Map<String, Long> getEndTime() {
        return endTime;
    }

    public Map<String,String> getPlayerNames(){
        return playerNames;
    }

    public void setEndTime(Map<String, Long> endTime) {
        this.endTime = endTime;
    }
}


