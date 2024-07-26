package no.behn.typingStomp.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    private final String id;
    private final Map<String, Integer> clientPositions = new ConcurrentHashMap<>();
    final Map<String, Long> endTime = new ConcurrentHashMap<>(); //TODO refactor to wordsPerMinute?
    private final Map<String,String> playerNames = new ConcurrentHashMap<>();
    private final Text text;
    private Instant startTime;
    private boolean gameStarted;
    private boolean isDone;

    public Room(String id, Text text) {
        this.id = id;
        this.text = text;
        this.gameStarted = false; //State represents if the game is ongoing or not, true if started, else if not
        this.isDone = false;
        this.startTime = null;
    }

    public String getId() {
        return id;
    }

    public Text getText() {
        return text;
    }

    public void addClient(String sessionId, String playerName) {
        clientPositions.put(sessionId, 0);
        playerNames.put(sessionId, playerName);
    }

    public void addClientEndTime(String sessionId, Long timeInSeconds){
        endTime.put(sessionId, timeInSeconds);
    }

    public void removeClient(String sessionId) {
        if (clientPositions.containsKey(sessionId)){
            clientPositions.remove(sessionId);
            playerNames.remove(sessionId);
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

    public Map<String,String> getPlayerNames(){
        return playerNames;
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




