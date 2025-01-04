package org.example.ankiprojekt;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.nio.file.Paths;
import java.util.*;

public class InGameController {

    @FXML
    private Label label_Top;

    @FXML
    private Label label_Answer;

    @FXML
    private ImageView img_Image;

    @FXML
    private Button btn_StartGame;

    @FXML
    private Button btn_ShowAnswer;

    @FXML
    private HBox hbox_AnswerOptions;

    @FXML
    private Label label_delvistKorrektAmount;

    @FXML
    private Label label_ikkeKorrektAmount;

    @FXML
    private Label label_korrektAmount;

    @FXML
    private Label label_næstenKorrektAmount;

    // Reference to the selected deck
    Deck selectedDeck;

    Card currentCard;

    // Opret en liste til at gemme kort, der ikke er "Korrekt"
    List<Card> availableCards = new ArrayList<>();


    public void customInit(Deck selectedDeck) {

        // Set the reference to the selected deck
        this.selectedDeck = selectedDeck;

        // Hide and show stuff
        label_Top.setVisible(false);
        hbox_AnswerOptions.setVisible(false);
        btn_ShowAnswer.setVisible(false);
        btn_StartGame.setVisible(true);

        // Add all cards to the available cards array
        availableCards.addAll(selectedDeck.getDeck());

        // Remove cards which are already correct
        availableCards.removeIf(card -> card.getLearnedType() == Card.Learned.Korrekt);

        System.out.println("Deck size" + selectedDeck.getDeck().size());

        updateStats();
    }
    @FXML
    public void startGame() {
        if (selectedDeck != null) {
            // Set the question label based on card type
            if (Objects.equals(selectedDeck.getDeck().getFirst().getNotetype(), "Art")) {
                label_Top.setText("Artist?");
            }

            // Show and hide UI elements
            label_Top.setVisible(true);
            hbox_AnswerOptions.setVisible(true);
            btn_ShowAnswer.setVisible(true);
            btn_StartGame.setVisible(false);

            // Show a card
            showRandomCard();

        } else {
            System.out.println("No Deck selected");
        }
    }

    @FXML
    private void showAnswer() {
        label_Answer.setText(currentCard.getBackArtist() + " " + currentCard.getBackTitle() + " " + currentCard.getBackYear()); // Set the text for the answer label
        hbox_AnswerOptions.setVisible(true);
    }

    private void showRandomCard() {

        // Get a random number in the deck
        int rnd = new Random().nextInt(availableCards.size());

        // Get the card from the random number
        Card newCard = availableCards.get(rnd);

        if (newCard.getImagePath() != null) {
            // Create a valid file URL
            String fileUrl = "file:///" + Paths.get(newCard.getImagePath()).toUri().getPath();

            // Load and set the image
            img_Image.setImage(new Image(fileUrl));

            // Set reference to the current card
            currentCard = newCard;

            // Hide answer options until user answers
            hbox_AnswerOptions.setVisible(false);
            label_Answer.setText("");
        }
    }

    @FXML
    private void korrekt() {
        currentCard.setLearnedType(Card.Learned.Korrekt);
        currentCard.setAnswered(true);


        availableCards.remove(currentCard);

        updateStats();

        if (availableCards.isEmpty()) {
            System.out.println("Game complete!");
        } else {
            showRandomCard();
        }
    }

    @FXML
    private void næstenKorrekt() {
        // Set the enum on the card
        currentCard.setLearnedType(Card.Learned.NæstenKorrekt);
        currentCard.setAnswered(true);

        updateStats(); // Opdatér statistikkerne

        showRandomCard();
    }

    @FXML
    private void delvistKorrekt() {
        // Set the enum on the card
        currentCard.setLearnedType(Card.Learned.DelvistKorrekt);
        currentCard.setAnswered(true);

        updateStats(); // Opdatér statistikkerne

        showRandomCard();
    }

    @FXML
    private void ikkeKorrekt() {
        // Set the enum on the card
        currentCard.setLearnedType(Card.Learned.IkkeKorrekt);
        currentCard.setAnswered(true);

        updateStats(); // Opdatér statistikkerne

        showRandomCard();
    }

    private void updateStats() {
        for (Card card : selectedDeck.getDeck()) {
            if (card.getLearnedType() == Card.Learned.Korrekt) {
                label_korrektAmount.setText("Korrekt: " + getLearnedAmount(Card.Learned.Korrekt));
            }

            if (card.getLearnedType() == Card.Learned.NæstenKorrekt) {
                label_næstenKorrektAmount.setText("Næsten Korrekt: " + getLearnedAmount(Card.Learned.NæstenKorrekt));
            }

            if (card.getLearnedType() == Card.Learned.DelvistKorrekt) {
                label_delvistKorrektAmount.setText("Delvist Korrekt: " + getLearnedAmount(Card.Learned.DelvistKorrekt));
            }

            if (card.getLearnedType() == Card.Learned.IkkeKorrekt) {
                label_ikkeKorrektAmount.setText("Ikke Korrekt: " + getLearnedAmount(Card.Learned.IkkeKorrekt));
            }
        }
    }

    public int getLearnedAmount(Card.Learned learnedAmount) {

        int amount = 0;

        for (Card card : selectedDeck.getDeck()) {
            if (card.getLearnedType() == learnedAmount) {
                amount++;
            }
        }
        return amount;
    }
}
