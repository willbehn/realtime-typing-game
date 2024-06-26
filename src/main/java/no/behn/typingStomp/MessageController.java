package no.behn.typingStomp;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @MessageMapping("/hello")
    @SendTo("/topic/messages")
    public Message greeting(String message) {
        return new Message(message);
    }
}