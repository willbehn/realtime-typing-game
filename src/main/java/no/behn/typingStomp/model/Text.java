package no.behn.typingStomp.model;

public class Text {
    String text;
    int wordCount;

    public Text(String text, int wordCount){
        this.text = text;
        this.wordCount = wordCount;
    }

    public String getTextString() {
        return text;
    }

    public int getWordCount() {
        return wordCount;
    }
}
