package org.example.ankiprojekt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

public class DecksDatabase implements Serializable {

    private static final long serialVersionUID = 1L;

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
        // Check if there's no instance already created
        if (instance == null) {
            instance = new DecksDatabase(); // Create a new instance if none exists
        }
        System.out.println("Accessing DecksDatabase instance: " + instance.decks);
        return instance;
    }


    public void addDeck(Deck deck) {
        decks.add(deck);
    }

    public void removeDeck(Deck deck) {
        decks.remove(deck);
    }

    public static void setInstance(DecksDatabase existingInstance) {
        instance = existingInstance;

        if (instance.decks == null) {
            instance.decks = new ArrayList<>();
        }

        System.out.println("Decks after loading:");
        for (Deck deck : instance.decks) {
            System.out.println(deck); // Calls the Deck's toString method
        }
    }


}
