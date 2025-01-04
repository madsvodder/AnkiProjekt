package org.example.ankiprojekt;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    ArrayList<Card> deck = new ArrayList<Card>();

    @Setter
    String name;

    public Deck() {
        this.name = "BlankNameDeck";
    }

    // Constructor for duplicating
    public Deck(Deck original) {
        this.name = original.name;
        this.deck = new ArrayList<>(original.deck);
    }

    public List<Card> getDeck() {
        return deck;
    }


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
