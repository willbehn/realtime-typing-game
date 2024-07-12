package no.behn.typingStomp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StateDto {
    private boolean gameStarted;
    private boolean isDone;
    private Map<String, Long> endTime = new ConcurrentHashMap<>();

    public StateDto(boolean gameStarted, Map<String, Long> endTime, boolean isDone) {
        this.gameStarted = gameStarted;
        this.endTime = endTime;
        this.isDone = isDone;
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

    public void setEndTime(Map<String, Long> endTime) {
        this.endTime = endTime;
    }
}


