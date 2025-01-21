package org.example.ankiprojekt;

import lombok.Getter;

public class Statistics {

    private static Statistics instance;

    public Statistics() {
    }

    User activeUser = UserDatabase.getInstance().getActiveUser();
    public static synchronized Statistics getInstance() {
        // Check if there's no instance already created
        if (instance == null) {
            // Create a new instance if none exists
            instance = new Statistics();
        }
        return instance;
    }
    public int getLearnedCards() {
        int totalCardsLearned = 0;
        for (Deck deck : activeUser.getDecksDatabase().getDecks()) {
            totalCardsLearned += deck.getAmountOfLearnedCards();
        }
        return totalCardsLearned;
    }

    public int getNotLearnedCards() {

        int totalCardsNotLearned = 0;

        for (Deck deck : activeUser.getDecksDatabase().getDecks()) {
            for (Card cards : deck.getDeckDemplate()) {
                if (cards.getLearnedType() != Card.Learned.Korrekt) {
                    totalCardsNotLearned++;
                }
            }
        }
        return totalCardsNotLearned;
    }

    public int getTotalCards() {
        return activeUser.getDecksDatabase().getDecks().size() * 10;
    }

}
