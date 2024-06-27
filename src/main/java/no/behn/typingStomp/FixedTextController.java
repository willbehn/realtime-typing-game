package no.behn.typingStomp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FixedTextController {

    private static final String FIXED_TEXT = "Welcome to the typing challenge! Type as fast as you can.";

    @GetMapping("/fixed-text")
    public String getFixedText() {
        return FIXED_TEXT;
    }
}