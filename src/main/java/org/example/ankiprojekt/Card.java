package org.example.ankiprojekt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class Card implements Serializable {

    private static final long serialVersionUID = 3L;

    @Getter @Setter
    String guid, notetype, deckName, imagePath, question, backAnswer1, backAnswer2, backAnswer3;

    public enum Learned {Korrekt, NæstenKorrekt, DelvistKorrekt, IkkeKorrekt}

    @Setter @Getter
    private boolean answered;
    @Getter @Setter
    Learned learnedType;
    public Card(String guid, String notetype, String deck, String imagePath, String backAnswer1, String backAnswer2, String backAnswer3) {
        this.guid = guid;
        this.notetype = notetype;
        this.deckName = deck;
        this.backAnswer1 = backAnswer1;
        this.imagePath = imagePath;
        this.backAnswer2 = backAnswer2;
        this.backAnswer3 = backAnswer3;
        this.answered = false;
        learnedType = null;
    }

    public Card(String guid, String imagePath, String question, String backAnswer1, String backAnswer2, String backAnswer3) {
        this.guid = guid;
        this.question = question;
        this.backAnswer1 = backAnswer1;
        this.imagePath = imagePath;
        this.backAnswer2 = backAnswer2;
        this.backAnswer3 = backAnswer3;
        this.answered = false;
        learnedType = null;
    }

    @Override
    public Card clone() {
        return new Card(this.guid, this.notetype, this.deckName, this.imagePath,
                this.backAnswer1, this.backAnswer2, this.backAnswer3);
    }

    public void resetCard() {
        answered = false;
        learnedType = null;
    }

    @Override
    public String toString() {
        return String.format("Spørgsmål: %s\nSvar1: %s", question, backAnswer1);
    }

}
