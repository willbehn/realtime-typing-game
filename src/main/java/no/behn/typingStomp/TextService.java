package no.behn.typingStomp;

import org.springframework.stereotype.Service;

@Service
public class TextService {

    public String getText() {
        // TODO bytt ut med faktisk henting av tekst fra en database etterhvert
        //return "Bees play a vital role in our ecosystem as pollinators, facilitating the reproduction of plants and crops essential for human and animal food sources. They communicate through intricate dances to share information about food sources. With their collective efforts, bees contribute significantly to biodiversity and agricultural productivity, highlighting their crucial role in maintaining a balanced and sustainable environment.";
        return "This is a test for testing purposes";
    }
}

