package org.example.ankiprojekt;

import lombok.Getter;

public class Card {
    @Getter
    String guid, notetype, deck, imagePath, backArtist, backTitle, backYear;

    public Card(String guid, String notetype, String deck, String imagePath, String backArtist, String backTitle, String backYear) {
        this.guid = guid;
        this.notetype = notetype;
        this.deck = deck;
        this.backArtist = backArtist;
        this.imagePath = imagePath;
        this.backTitle = backTitle;
        this.backYear = backYear;
    }

    @Override
    public String toString() {
        return String.format("GUID: %s\nDeck: %s\nFront: %s\nImage Path: %s\n", guid, deck);
    }
}
