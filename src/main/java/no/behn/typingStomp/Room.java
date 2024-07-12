package no.behn.typingStomp;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    private final String id;
    private final Map<String, Integer> clientPositions = new ConcurrentHashMap<>();
    final Map<String, Long> endTime = new ConcurrentHashMap<>();
    private final String text;
    private Instant startTime;
    private boolean gameStarted;
    private boolean isDone;

    public Room(String id, String text) {
        this.id = id;
        this.text = text;
        this.gameStarted = false; //State represents if the game is ongoing or not, true if started, else if not
        this.isDone = false;
        this.startTime = null;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void addClient(String sessionId) {
        clientPositions.put(sessionId, 0);
    }

    public void addClientEndTime(String sessionId, Long timeInSeconds){
        endTime.put(sessionId, timeInSeconds);
    }

    public void removeClient(String sessionId) {
        if (clientPositions.containsKey(sessionId)){
            clientPositions.remove(sessionId);
        }
    }

    public void setStarted(){
        gameStarted = true;
        startTime = Instant.now();
    }

    public void setDone(){
        isDone = true;
    }

    public boolean getState(){
        return gameStarted;
    }

    public boolean getDone(){
        return isDone;
    }

    public Map<String, Integer> getClientPositions() {
        return clientPositions;
    }

    public Map<String, Long> getClientEndtimes() {
        return endTime;
    }

    public int getClientCount(){
        return clientPositions.keySet().size();
    }

    public String getStartTime(){
        if (startTime != null){
            return startTime.toString();
        } else return "";
    }

    public long getDurationInSeconds() {
        if (startTime != null && isDone) {
            Instant endTime = Instant.now(); 
            Duration duration = Duration.between(startTime, endTime);
            return duration.getSeconds();
        }
        return -1; 
    }
}




