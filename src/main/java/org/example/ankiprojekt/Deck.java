package org.example.ankiprojekt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck implements Serializable {

    private static final long serialVersionUID = 2L;

    ArrayList<Card> deck = new ArrayList<Card>();

    @Setter
    String name;

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
