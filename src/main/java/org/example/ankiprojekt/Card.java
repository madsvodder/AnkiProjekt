package org.example.ankiprojekt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class Card implements Serializable {

    private static final long serialVersionUID = 3L;

    @Getter
    String guid, notetype, deck, imagePath, backArtist, backTitle, backYear;

    public enum Learned {Korrekt, NæstenKorrekt, DelvistKorrekt, IkkeKorrekt}

    @Setter @Getter
    private boolean answered;
    @Getter @Setter
    Learned learnedType;
    public Card(String guid, String notetype, String deck, String imagePath, String backArtist, String backTitle, String backYear) {
        this.guid = guid;
        this.notetype = notetype;
        this.deck = deck;
        this.backArtist = backArtist;
        this.imagePath = imagePath;
        this.backTitle = backTitle;
        this.backYear = backYear;
        this.answered = false;
        learnedType = null;
    }

    @Override
    public String toString() {
        return String.format("GUID: %s\nDeck: %s\nFront: %s\nImage Path: %s\n", guid, deck, backTitle, imagePath);
    }

}
