package org.example.ankiprojekt;

import lombok.Getter;
import lombok.Setter;

public class Card {
    @Getter
    String guid, notetype, deck, imagePath, backArtist, backTitle, backYear;

    public enum Learned {Korrekt, NæstenKorrekt, DelvistKorrekt, IkkeKorrekt}

    @Setter @Getter
    private boolean answered;
    @Getter @Setter
    Learned learnedAmount;
    public Card(String guid, String notetype, String deck, String imagePath, String backArtist, String backTitle, String backYear) {
        this.guid = guid;
        this.notetype = notetype;
        this.deck = deck;
        this.backArtist = backArtist;
        this.imagePath = imagePath;
        this.backTitle = backTitle;
        this.backYear = backYear;
        this.answered = false;
        learnedAmount = null;
    }

    @Override
    public String toString() {
        return String.format("GUID: %s\nDeck: %s\nFront: %s\nImage Path: %s\n", guid, deck, backTitle, imagePath);
    }

}
