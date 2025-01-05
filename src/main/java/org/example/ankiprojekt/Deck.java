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

    @Setter
    String name;

    public List<Card> getDeckDemplate() {
        return deckDemplate;
    }


    public void add(Card card) {
        deckDemplate.add(card);
    }

    public void remove(Card card) {
        deckDemplate.remove(card);
    }

    @Override
    public String toString() {
        return name + "         " + getPercentageOfLearnedCards() + "%";
    }

    private String getPercentageOfLearnedCards() {
        if (deckDemplate.isEmpty()) {
            return "0.00";
        }
        return String.format("%.2f", (double) unavailableCards.size() / deckDemplate.size() * 100);
    }

}
