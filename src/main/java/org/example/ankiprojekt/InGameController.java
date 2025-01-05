package org.example.ankiprojekt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    @FXML
    private Label label_doneAmount;

    // Reference to the selected deck
    Deck selectedDeck;

    Card currentCard;


    // Scheduler service for timing cards
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Cards waiting for timeout //
    // Cards will be moved to this arraylist, and when the time is up,
    // whey will be moved back to the available cards arraylist.
    private final List<Card> pendingCards = new ArrayList<>();

    private void calculateCardsLeft() {
        int cardsLeft = 0;
        for (Card card : selectedDeck.getDeckDemplate()) {
            if (card.getLearnedType() != Card.Learned.Korrekt) {
                cardsLeft++;
            }
        }
        label_doneAmount.setText("Kort tilbage: " + cardsLeft);
    }

    public void customInit(Deck selectedDeck) {

        // Set the reference to the selected deck
        this.selectedDeck = selectedDeck;

        // Hide and show stuff
        label_Top.setVisible(false);
        hbox_AnswerOptions.setVisible(false);
        btn_ShowAnswer.setVisible(false);
        btn_StartGame.setVisible(true);

        updateStats();
    }


    @FXML
    private void showAnswer() {
        label_Answer.setText(currentCard.getBackArtist() + " " + currentCard.getBackTitle() + " " + currentCard.getBackYear()); // Set the text for the answer label
        hbox_AnswerOptions.setVisible(true);
    }

    private void showRandomCard() {
        // Try to select a random card from the available cards
        currentCard = getRandomUnlearnedCard(selectedDeck.getAvailableCards());

        if (currentCard == null) {
            // If no unlearned cards are available, try pending cards
            currentCard = getRandomUnlearnedCard(pendingCards);

            if (currentCard == null) {
                // If no unlearned cards exist in either list, finish the game
                System.out.println("No more unlearned cards available. Finishing game");
                finishGame();
                return;
            }

            System.out.println("No more available cards. Showing cards from pending array");
        }

        // Show card information
        displayCard(currentCard);
    }

    private Card getRandomUnlearnedCard(List<Card> cards) {
        List<Card> unlearnedCards = cards.stream()
                .filter(card -> card.getLearnedType() != Card.Learned.Korrekt)
                .toList();

        if (unlearnedCards.isEmpty()) {
            return null;
        }

        int rnd = new Random().nextInt(unlearnedCards.size());
        return unlearnedCards.get(rnd);
    }

    private void displayCard(Card card) {
        if (card.getImagePath() != null) {
            String fileUrl = "file:///" + Paths.get(card.getImagePath()).toUri().getPath();
            img_Image.setImage(new Image(fileUrl));
        }

        hbox_AnswerOptions.setVisible(false);
        label_Answer.setText("");
    }


    @FXML
    private void korrekt() {
        // Finish the current card
        finishCard(currentCard);

        // Update the stats on the screen
        updateStats();

        // Show a random card
        showRandomCard();
    }

    @FXML
    private void næstenKorrekt() {
        // Set the enum on the card
        currentCard.setLearnedType(Card.Learned.NæstenKorrekt);
        currentCard.setAnswered(true);

        if (!isCurrentCardInPendingList()) {
            // Move card to waiting list (1min)
            selectedDeck.getAvailableCards().remove(currentCard);
            pendingCards.add(currentCard);

            // Make the scheduler show the card again after 1 min
            scheduler.schedule(() -> {
                pendingCards.remove(currentCard);
                selectedDeck.getAvailableCards().add(currentCard);
            }, 10, TimeUnit.MINUTES);
        }

        updateStats(); // Opdatér statistikkerne

        showRandomCard();
    }

    @FXML
    private void delvistKorrekt() {
        // Set the enum on the card
        currentCard.setLearnedType(Card.Learned.DelvistKorrekt);
        currentCard.setAnswered(true);

        if (!isCurrentCardInPendingList()) {
            // Move card to waiting list (1min)
            selectedDeck.getAvailableCards().remove(currentCard);
            pendingCards.add(currentCard);

            // Make the scheduler show the card again after 1 min
            scheduler.schedule(() -> {
                pendingCards.remove(currentCard);
                selectedDeck.getAvailableCards().add(currentCard);
            }, 5, TimeUnit.MINUTES);
        }

        updateStats(); // Opdatér statistikkerne

        showRandomCard();
    }

    @FXML
    private void ikkeKorrekt() {
        // Set the enum on the card
        currentCard.setLearnedType(Card.Learned.IkkeKorrekt);
        currentCard.setAnswered(true);

        if (!isCurrentCardInPendingList()) {
            // Move card to waiting list (1min)
            selectedDeck.getAvailableCards().remove(currentCard);
            pendingCards.add(currentCard);

            // Make the scheduler show the card again after 1 min
            scheduler.schedule(() -> {
                pendingCards.remove(currentCard);
                selectedDeck.getAvailableCards().add(currentCard);
            }, 1, TimeUnit.MINUTES);
        }

        updateStats(); // Opdatér statistikkerne

        showRandomCard();
    }

    private void updateStats() {
        for (Card card : selectedDeck.getDeckDemplate()) {
            if (card.getLearnedType() == Card.Learned.Korrekt && card.isAnswered()) {
                label_korrektAmount.setText("Korrekt: " + getLearnedAmount(Card.Learned.Korrekt));
            }

            if (card.getLearnedType() == Card.Learned.NæstenKorrekt && card.isAnswered()) {
                label_næstenKorrektAmount.setText("Næsten Korrekt: " + getLearnedAmount(Card.Learned.NæstenKorrekt));
            }

            if (card.getLearnedType() == Card.Learned.DelvistKorrekt && card.isAnswered()) {
                label_delvistKorrektAmount.setText("Delvist Korrekt: " + getLearnedAmount(Card.Learned.DelvistKorrekt));
            }

            if (card.getLearnedType() == Card.Learned.IkkeKorrekt && card.isAnswered()) {
                label_ikkeKorrektAmount.setText("Ikke Korrekt: " + getLearnedAmount(Card.Learned.IkkeKorrekt));
            }
        }

        System.out.println("Pending Cards: " + pendingCards.size());

        calculateCardsLeft();
    }

    // Helpers //
    public int getLearnedAmount(Card.Learned learnedAmount) {

        int amount = 0;

        for (Card card : selectedDeck.getDeckDemplate()) {
            if (card.getLearnedType() == learnedAmount) {
                amount++;
            }
        }
        return amount;
    }

    private void finishCard(Card cardToFinish) {

        // Set the values of the current card
        cardToFinish.setLearnedType(Card.Learned.Korrekt);
        cardToFinish.setAnswered(true);

        selectedDeck.getAvailableCards().remove(cardToFinish);
        selectedDeck.getUnavailableCards().add(cardToFinish);
    }

    @FXML
    public void startGame() {
        if (selectedDeck != null) {

            // Remember! Create a new scheduler, while the old one shuts down
            if (scheduler.isShutdown()) {
                scheduler = Executors.newScheduledThreadPool(1);
                System.out.println("Scheduler restarted");
            }

            // Empty the pending cards array
            pendingCards.clear();

            // Tøm de tilgængelige kort og tilføj kun ulærte kort
            selectedDeck.getAvailableCards().clear();
            for (Card card : selectedDeck.getDeckDemplate()) {
                if (card.getLearnedType() != Card.Learned.Korrekt) {
                    selectedDeck.getAvailableCards().add(card);
                }
            }

            // Tjek, om der er kort tilbage at vise
            if (selectedDeck.getAvailableCards().isEmpty()) {
                finishGame();
                return;
            }

            // Indstil spørgsmålet baseret på korttypen
            if (!selectedDeck.getDeckDemplate().isEmpty() &&
                    Objects.equals(selectedDeck.getDeckDemplate().getFirst().getNotetype(), "Art")) {
                label_Top.setText("Artist?");
            }

            // Vis og skjul UI-elementerne
            label_Top.setVisible(true);
            hbox_AnswerOptions.setVisible(false);
            btn_ShowAnswer.setVisible(true);
            btn_StartGame.setVisible(false);

            // Vis første kort
            showRandomCard();

            // Opdater stats
            updateStats();
        } else {
            System.out.println("No Deck selected");
        }
    }

    private void finishGame() {

        // Shut down the scheduler for card delay
        scheduler.shutdown();


        // Calculate the total cards in the deck
        int totalCards = selectedDeck.getDeckDemplate().size();

        // Get the statistics
        int korrekt = getLearnedAmount(Card.Learned.Korrekt);
        int næstenKorrekt = getLearnedAmount(Card.Learned.NæstenKorrekt);
        int delvistKorrekt = getLearnedAmount(Card.Learned.DelvistKorrekt);
        int ikkeKorrekt = getLearnedAmount(Card.Learned.IkkeKorrekt);

        // Hide unnecessary UI elements
        label_Top.setVisible(false);
        label_Answer.setVisible(false);
        hbox_AnswerOptions.setVisible(false);
        btn_ShowAnswer.setVisible(false);
        btn_StartGame.setVisible(false);
        img_Image.setVisible(false);

        // Display final statistics on screen
        label_korrektAmount.setText("Korrekt: " + korrekt);
        label_næstenKorrektAmount.setText("Næsten Korrekt: " + næstenKorrekt);
        label_delvistKorrektAmount.setText("Delvist Korrekt: " + delvistKorrekt);
        label_ikkeKorrektAmount.setText("Ikke Korrekt: " + ikkeKorrekt);

        // Show a completion message
        label_Top.setVisible(true);
        label_Top.setText("Spillet er slut! Tak for at spille.\n" +
                "Statistikker:\n" +
                "Korrekt: " + korrekt + "\n" +
                "Næsten Korrekt: " + næstenKorrekt + "\n" +
                "Delvist Korrekt: " + delvistKorrekt + "\n" +
                "Ikke Korrekt: " + ikkeKorrekt + "\n" +
                "Total Kort: " + totalCards);
    }

    @FXML
    private void restartGame() {

        scheduler.shutdown();

        // Reset the card stacks
        selectedDeck.getUnavailableCards().clear();
        selectedDeck.getAvailableCards().clear();
        pendingCards.clear();

        // Reset all cards in the deck
        for (Card card : selectedDeck.getDeckDemplate()) {
            card.setLearnedType(null);
            card.setAnswered(false);
        }

        // Add all cards back to available cards
        selectedDeck.getAvailableCards().addAll(selectedDeck.getDeckDemplate());

        // Reset all statistic labels to 0
        label_korrektAmount.setText("Korrekt: 0");
        label_næstenKorrektAmount.setText("Næsten Korrekt: 0");
        label_delvistKorrektAmount.setText("Delvist Korrekt: 0");
        label_ikkeKorrektAmount.setText("Ikke Korrekt: 0");
        label_doneAmount.setText("Kort tilbage: " + selectedDeck.getDeckDemplate().size());

        // Restart the game
        startGame();
    }

    @FXML
    private void surrender() {

        // Reset the card stacks
        selectedDeck.getUnavailableCards().clear();
        selectedDeck.getAvailableCards().clear();

        // Reset all cards in the deck template
        for (Card card : selectedDeck.getDeckDemplate()) {
            card.setLearnedType(null);
            card.setAnswered(false);
        }

        // Update the stats
        updateStats();

        // finish the game
        finishGame();
    }

    @FXML
    private void backToMainMenu() {

        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("Scheduler successfully shut down.");
        }

        // Gem den aktuelle tilstand
        DataSaver.getInstance().save(); // Sørg for, at databasen bliver gemt korrekt

        // Skift tilbage til hovedmenuen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            BorderPane mainMenuView = loader.load();

            HelloController helloController = loader.getController();

            // Udskift hele roden i scenen med hello-view's BorderPane
            btn_StartGame.getScene().setRoot(mainMenuView);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load hello-view.fxml");
        }
    }


    private boolean isCurrentCardInPendingList() {
        for (Card card : pendingCards) {
            if (card.equals(currentCard)) {
                return true;
            }
        }
        return false;
    }
}
