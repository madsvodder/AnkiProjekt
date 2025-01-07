package org.example.ankiprojekt;

import lombok.Getter;

public class Statistics {

    private static Statistics instance;

    public Statistics() {
    }

    public static synchronized Statistics getInstance() {
        // Check if there's no instance already created
        if (instance == null) {
            instance = new Statistics(); // Create a new instance if none exists
        }
        return instance;
    }
    public int getLearnedCards() {
        int totalCardsLearned = 0;
        for (Deck deck : DecksDatabase.getInstance().getDecks()) {
            totalCardsLearned += deck.getAmountOfLearnedCards();
        }
        return totalCardsLearned;
    }

    public int getNotLearnedCards() {

        int totalCardsNotLearned = 0;

        for (Deck deck : DecksDatabase.getInstance().getDecks()) {
            for (Card cards : deck.getDeckDemplate()) {
                if (cards.getLearnedType() != Card.Learned.Korrekt) {
                    totalCardsNotLearned++;
                }
            }
        }
        return totalCardsNotLearned;
    }

    public int getTotalCards() {
        return DecksDatabase.getInstance().getDecks().size() * 10;
    }


}
