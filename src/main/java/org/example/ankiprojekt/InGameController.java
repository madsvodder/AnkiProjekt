package org.example.ankiprojekt;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.Setter;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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
    private Label label_korrektAmount;

    // Reference to the selected deck
    Deck selectedDeck;

    Card currentCard;

    // Opret en liste til at gemme kort, der ikke er "Korrekt"
    List<Card> availableCards = new ArrayList<>();

    @Setter
    private int amountOfKorrektCards = 0;

    public void customInit(Deck selectedDeck) {

        // Set the reference to the selected deck
        this.selectedDeck = selectedDeck;

        // Hide and show stuff
        label_Top.setVisible(false);
        hbox_AnswerOptions.setVisible(false);
        btn_ShowAnswer.setVisible(false);
        btn_StartGame.setVisible(true);

        //card.setLearnedAmount(null);
        //card.setAnswered(false);

        // Add all cards to the available cards array
        availableCards.addAll(selectedDeck.getDeck());

        System.out.println("Deck size" + selectedDeck.getDeck().size());
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
        currentCard.setLearnedAmount(Card.Learned.Korrekt);
        currentCard.setAnswered(true);

        // Remove the card, if it is correct
        availableCards.remove(currentCard);
        System.out.println("Available cards: " + availableCards.size());

        updateStats(); // Opdatér statistikkerne

        // If the card is correct, dont show it again

        showRandomCard();


    }

    @FXML
    private void næstenKorrekt() {
        // Set the enum on the card
        currentCard.setLearnedAmount(Card.Learned.NæstenKorrekt);

        updateStats(); // Opdatér statistikkerne

        showRandomCard();
    }

    @FXML
    private void delvistKorrekt() {
        // Set the enum on the card
        currentCard.setLearnedAmount(Card.Learned.DelvistKorrekt);

        updateStats(); // Opdatér statistikkerne

        showRandomCard();
    }

    @FXML
    private void ikkeKorrekt() {
        // Set the enum on the card
        currentCard.setLearnedAmount(Card.Learned.IkkeKorrekt);

        updateStats(); // Opdatér statistikkerne

        showRandomCard();
    }

    private void updateStats() {
        // Initialiser tællingen med alle Card.Learned-værdier sat til 0
        Map<Card.Learned, Long> learnedCounts = new HashMap<>();
        for (Card.Learned value : Card.Learned.values()) {
            learnedCounts.put(value, 0L);
        }

        // Brug Stream til at tælle LearnedAmount-værdier og opdatere mappen
        selectedDeck.getDeck().stream()
                .filter(card -> card.getLearnedAmount() != null) // Filtrér ud kort uden værdi
                .forEach(card -> learnedCounts.put(
                        card.getLearnedAmount(),
                        learnedCounts.get(card.getLearnedAmount()) + 1
                ));

        // Byg en streng med resultaterne
        String stats = learnedCounts.entrySet().stream()
                .map(entry -> entry.getKey().name() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        // Udskriv/montér statistikken
        System.out.println("Stats: " + stats);
        label_korrektAmount.setText("Stats: " + stats);
    }
}
