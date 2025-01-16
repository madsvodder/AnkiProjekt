package org.example.ankiprojekt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DecksDatabase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    private ArrayList<Deck> decks;

    private ArrayList<Card> userCards;

    @Getter @Setter
    ArrayList<Deck> recentDecks;

    public void addDeckToRecentDecks(Deck deck) {
        System.out.println("Adding deck to recent decks: " + deck.getName());

        // Null-check
        if (recentDecks == null) {
            recentDecks = new ArrayList<>();
        }

        // Check if the recent list already contains the deck
        // Avoids duplication
        if (recentDecks.contains(deck)) {
            recentDecks.remove(deck);
            recentDecks.addFirst(deck);
        } else {
            recentDecks.addFirst(deck);
        }
    }

    public DecksDatabase() {
        decks = new ArrayList<>();
        userCards = new ArrayList<>();
        recentDecks = new ArrayList<>();
    }

    public boolean isCardInAnyDeck(Card card) {
        for (Deck deck : decks) {
            if (deck.doesDeckContainCard(card)) {
                return true;
            }
        }
        return false;
    }

    public void addDeck(Deck deck) {
        decks.add(deck);
    }

    public void removeDeck(Deck deck) {
        decks.remove(deck);
    }

    public List<Card> getUserCards() {
        if (userCards == null) {
            userCards = new ArrayList<>();
        }
        return userCards;
    }
}
