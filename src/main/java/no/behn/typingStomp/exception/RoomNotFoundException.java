package no.behn.typingStomp.exception;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
