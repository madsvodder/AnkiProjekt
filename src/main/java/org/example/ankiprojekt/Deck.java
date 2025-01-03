package org.example.ankiprojekt;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Deck {
    @Getter
    ArrayList<Card> deck = new ArrayList<Card>();

    @Setter
    String name;

    public void add(Card card) {
        deck.add(card);
    }

    public void remove(Card card) {
        deck.remove(card);
    }

    @Override
    public String toString() {
        return name;
    }
}
