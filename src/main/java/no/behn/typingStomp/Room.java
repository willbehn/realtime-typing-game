package no.behn.typingStomp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    private final String id;
    private final Map<String, Integer> clientPositions = new ConcurrentHashMap<>();
    private final String text;

    public Room(String id, String text) {
        this.id = id;
        this.text = text;
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

    public void removeClient(String sessionId) {
        if (clientPositions.containsKey(sessionId)){
            clientPositions.remove(sessionId);
        }
    }

    public Map<String, Integer> getClientPositions() {
        return clientPositions;
    }

    public int getClientCount(){
        return clientPositions.keySet().size();
    }
}




