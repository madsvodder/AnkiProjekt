package org.example.ankiprojekt;

import lombok.Getter;

import java.util.ArrayList;

public class DecksDatabase {

    // The single instance of this class
    private static DecksDatabase instance;

    @Getter
    private ArrayList<Deck> decks;

    // Constructor
    private DecksDatabase() {
        decks = new ArrayList<>();
    }

    // Public method to get acces to the single instance
    public static synchronized DecksDatabase getInstance() {
        if (instance == null) {
            instance = new DecksDatabase();
        }
        return instance;
    }

    public void addDeck(Deck deck) {
        decks.add(deck);
    }

    public void removeDeck(Deck deck) {
        decks.remove(deck);
    }
}
