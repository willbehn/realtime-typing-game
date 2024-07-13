package no.behn.typingStomp;

import org.springframework.stereotype.Service;

@Service
public class TextService {

    public String getText() {
        // TODO bytt ut med faktisk henting av tekst fra en database etterhvert
        return "Maneter er marine dyr som tilhører gruppen nesledyr og er kjent for sine geleaktige kropper og tentakler. De bruker nesleceller for å lamme byttet sitt og forsvare seg mot fiender. Maneter finnes i alle verdenshav og kan variere i størrelse fra noen få millimeter til flere meter i diameter.";
        //return "This is a test for testing purposes";
    }
}

