package no.behn.typingStomp;

import org.springframework.stereotype.Service;

@Service
public class TextService {

    public String getText() {
        // TODO bytt ut med faktisk henting av tekst fra en database etterhvert
        return "Bier er flittige insekter som spiller en avgjørende rolle i økosystemet ved å pollinere blomster og bidra til avlingene våre. Deres organiserte samfunnsstruktur og evne til å produsere honning har fascinert mennesker i århundrer. Biers truede status har ledet til økt fokus på bevaring og beskyttelse av deres levesteder. Forskning på biers adferd og kommunikasjon gir verdifull innsikt i kompleksiteten i det naturlige verden.";
    }
}

