package no.behn.typingStomp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {

    private Room room;

    @BeforeEach
    public void setUp() {
        room = new Room("roomId", new Text("Sample text",2));
    }

    @Test
    public void testAddClient() {
        room.addClient("client1");
        assertEquals(1, room.getClientCount());
    }

    @Test
    public void testRemoveClient() {
        room.addClient("client1");
        room.removeClient("client1");
        assertEquals(0, room.getClientCount());
    }

    @Test
    public void testSetStartedAndGetState() {
        assertFalse(room.getState());
        room.setStarted();
        assertTrue(room.getState());
    }

    @Test
    public void testSetDoneAndGetDone() {
        assertFalse(room.getDone());
        room.setDone();
        assertTrue(room.getDone());
    }

    /*@Test
    public void testGetWordsPerMinuteOneMinute() {
        int wordCount = 100;
        Long timeInSeconds = 60L;

        Long wordsPerMinute = room.getWordsPerMinute(wordCount, timeInSeconds);
        assertEquals(100, wordsPerMinute);
    }

    @Test
    public void testGetWordsPerMinute() {
        int wordCount = 100;
        long timeInSeconds = 30L;

        long wordsPerMinute = room.getWordsPerMinute(wordCount, timeInSeconds);
        assertEquals(200, wordsPerMinute);
    }*/

}

