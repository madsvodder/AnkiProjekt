package org.example.ankiprojekt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Deck implements Serializable {

    private static final long serialVersionUID = 2L;

    ArrayList<Card> deckDemplate = new ArrayList<Card>();

    // List of cards which are NOT "korrekt"
    @Getter
    List<Card> availableCards = new ArrayList<>();

    // Cards which are correct gets moved to this arraylist.
    @Getter
    List<Card> unavailableCards = new ArrayList<>();

    @Setter @Getter
    String name;

    public List<Card> getDeckDemplate() {
        return deckDemplate;
    }


    public void add(Card card) {
        Card clonedCard = card.clone(); // Lav en kopi af kortet
        clonedCard.resetCard();         // Sørg for at nulstille status
        deckDemplate.add(clonedCard);
    }


    public void remove(Card card) {
        card.resetCard();
        deckDemplate.remove(card);
        availableCards.remove(card);
        unavailableCards.remove(card);
    }

    public boolean doesDeckContainCard(Card card) {
        return deckDemplate.contains(card);
    }
    public int getAmountOfLearnedCards() {
        if (deckDemplate == null || unavailableCards == null) {
            return 0; // Hvis én af listerne er null, returnér 0
        }

        int learnedCards = 0;

        for (Card card : unavailableCards) {
            if (card.getLearnedType() == Card.Learned.Korrekt);
            learnedCards++;
        }

        return learnedCards;
    }

    @Override
    public String toString() {
        return name + "         " + getPercentageOfLearnedCards() + "%";
    }

    public String getPercentageOfLearnedCards() {
        if (deckDemplate.isEmpty()) {
            return "0.00";
        }
        return String.format("%.2f", (double) unavailableCards.size() / deckDemplate.size() * 100);
    }

    public Double getPercentageOfLearnedCardsDouble() {
        if (deckDemplate.isEmpty()) {
            return 0.0;
        }
        return unavailableCards.size() / (double) deckDemplate.size() * 100;
    }

}
