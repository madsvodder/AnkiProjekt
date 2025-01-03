package org.example.ankiprojekt;

public class Card {
    String guid, notetype, deck, frontHtml, back, imagePath;

    public Card(String guid, String notetype, String deck, String frontHtml, String back, String imagePath) {
        this.guid = guid;
        this.notetype = notetype;
        this.deck = deck;
        this.frontHtml = frontHtml;
        this.back = back;
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return String.format("GUID: %s\nDeck: %s\nFront: %s\nImage Path: %s\n", guid, deck, frontHtml, imagePath);
    }
}
