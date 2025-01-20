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

    // Refference to the current card on the screen
    Card currentCard;

    // Game state for when the user chooses to start, restart etc... //
    public enum GameState {
        START,
        RESTART,
        SURRENDER,
        FINISH
    }

    // Scheduler service for timing cards //
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Cards waiting for timeout //
    // Cards will be moved to this arraylist, and when the time is up, they will be moved to the frontCards array list
    private final List<Card> pendingCards = new ArrayList<>();

    // Cards to show FIRST - Priority cards
    private List<Card> frontCards = new ArrayList<>();

    // Initialize method!!!!! //
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

    // Buttons - These buttons handles the start, restart and surrender methods //

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            BorderPane mainMenuView = loader.load();

            MainViewController helloController = loader.getController();

            // Udskift hele roden i scenen med hello-view's BorderPane
            btn_StartGame.getScene().setRoot(mainMenuView);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load main-view.fxml");
        }
    }
    @FXML
    public void startGame() {
        handleGameState(GameState.START);
    }

    @FXML
    public void restartGame() {
        handleGameState(GameState.RESTART);
    }

    @FXML
    public void surrender() {
        handleGameState(GameState.SURRENDER);
    }

    // Buttons - These buttons handles the user input, when the user sees a card //

    @FXML
    private void showAnswer() {
        label_Answer.setVisible(true);
        //label_Answer.setText(currentCard.getBackAnswer1() + " " + currentCard.getBackAnswer2() + " " + currentCard.getBackAnswer3()); // Set the text for the answer label
        hbox_AnswerOptions.setVisible(true);
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
        handleCardAction(Card.Learned.NæstenKorrekt, 5, TimeUnit.MINUTES);
    }

    @FXML
    private void delvistKorrekt() {
        handleCardAction(Card.Learned.DelvistKorrekt, 2, TimeUnit.MINUTES);
    }

    @FXML
    private void ikkeKorrekt() {
        handleCardAction(Card.Learned.IkkeKorrekt, 10, TimeUnit.MINUTES);
    }
    private void handleGameState(GameState state) {
        // Ryd og nulstil kort afhængigt af tilstanden
        switch (state) {
            case START:
                resetScheduler();
                prepareDeck();
                showRandomCard();
                updateStats();
                updateUIForGameStart();
                break;

            case RESTART:
                scheduler.shutdown();
                resetAllCards();
                prepareDeck();
                showRandomCard();
                updateStats();
                updateUIForGameStart();
                break;

            case SURRENDER:
                resetAllCards();
                updateStats();
                finishGame();
                break;

            case FINISH:
                scheduler.shutdown(); // Luk planlægger
                hideGameUI();
                showStatistics();
                break;
        }
    }

    // Helpers - The methods are helpers. Usually used for other methods in this class, but some are also standalone //

    private void calculateCardsLeft() {
        int cardsLeft = 0;
        for (Card card : selectedDeck.getDeckDemplate()) {
            if (card.getLearnedType() != Card.Learned.Korrekt) {
                cardsLeft++;
            }
        }
        label_doneAmount.setText("Kort tilbage: " + cardsLeft);
    }
    private void showRandomCard() {
        // Hvis der er kort i frontCards, vises de som højeste prioritet
        if (!frontCards.isEmpty()) {
            System.out.println("Showing front card: " + frontCards.get(0));
            displayCard(frontCards.get(0));
            Card card = frontCards.remove(0); // Fjern kortet, når det vises
            return;
        }

        // Find et tilfældigt ulært kort
        currentCard = getRandomUnlearnedCard(selectedDeck.getAvailableCards());

        if (currentCard == null) {
            System.out.println("No more unlearned cards available. Finishing game");
            finishGame();
            return;
        }

        // Vis tilfældigt kort
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

        if (card != null) {
            label_Answer.setText(
                      card.getBackAnswer1() + "\n"
                    + card.getBackAnswer2() + "\n"
                    + card.getBackAnswer3() + "\n"
            );
        }

        if (card.getImagePath() != null) {
            String fileUrl = "file:///" + Paths.get(card.getImagePath()).toUri().getPath();
            img_Image.setImage(new Image(fileUrl));
        }

        label_Top.setText(currentCard.getQuestion());
        System.out.println("Card Question: " + currentCard.getQuestion());

        hbox_AnswerOptions.setVisible(false);
        label_Answer.setVisible(false);
    }

    private void handleCardAction(Card.Learned status, long delay, TimeUnit unit) {
        Card cardToProcess = currentCard;
        cardToProcess.setLearnedType(status);
        cardToProcess.setAnswered(true);

        if (!isCardInList(pendingCards, cardToProcess)) {
            pendingCards.add(cardToProcess);
            scheduleCardReturn(cardToProcess, delay, unit);
        }

        updateStats();
        showRandomCard();
    }

    private boolean isCardInList(List<Card> cardList, Card cardToCheck) {
        return cardList.stream().anyMatch(card -> card.equals(cardToCheck));
    }

    private void scheduleCardReturn(Card card, long delay, TimeUnit unit) {
        scheduler.schedule(() -> {
            synchronized (frontCards) {
                pendingCards.remove(card);
                frontCards.add(card);
                System.out.println("Card added to frontCards after timeout: " + card);
            }
        }, delay, unit);
    }
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

    // Helpers - These are used for managing the game state. Resetting the cards, preparing the game etc... //
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
    private void resetScheduler() {
        if (scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(1);
        }
    }
    private void prepareDeck() {
        pendingCards.clear();
        frontCards.clear();
        selectedDeck.getAvailableCards().clear();

        for (Card card : selectedDeck.getDeckDemplate()) {
            if (card.getLearnedType() != Card.Learned.Korrekt) {
                selectedDeck.getAvailableCards().add(card);
            }
        }
    }
    private void resetAllCards() {
        selectedDeck.getUnavailableCards().clear();
        selectedDeck.getAvailableCards().clear();

        for (Card card : selectedDeck.getDeckDemplate()) {
            card.setLearnedType(null);
            card.setAnswered(false);
        }

        selectedDeck.getAvailableCards().addAll(selectedDeck.getDeckDemplate());
    }

    // Helpers - These are mainly used for updating the UI //
    private void updateUIForGameStart() {
        label_Top.setVisible(true);
        hbox_AnswerOptions.setVisible(false);
        btn_ShowAnswer.setVisible(true);
        btn_StartGame.setVisible(false);
        img_Image.setVisible(true);
    }
    private void hideGameUI() {
        label_Top.setVisible(false);
        label_Answer.setVisible(false);
        hbox_AnswerOptions.setVisible(false);
        btn_ShowAnswer.setVisible(false);
        btn_StartGame.setVisible(false);
        img_Image.setVisible(false);
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
    private void showStatistics() {
        int totalCards = selectedDeck.getDeckDemplate().size();
        int korrekt = getLearnedAmount(Card.Learned.Korrekt);
        int næstenKorrekt = getLearnedAmount(Card.Learned.NæstenKorrekt);
        int delvistKorrekt = getLearnedAmount(Card.Learned.DelvistKorrekt);
        int ikkeKorrekt = getLearnedAmount(Card.Learned.IkkeKorrekt);

        label_Top.setVisible(true);
        label_Top.setText("Spillet er slut! Tak for at spille.\n" +
                "Statistikker:\n" +
                "Korrekt: " + korrekt + "\n" +
                "Næsten Korrekt: " + næstenKorrekt + "\n" +
                "Delvist Korrekt: " + delvistKorrekt + "\n" +
                "Ikke Korrekt: " + ikkeKorrekt + "\n" +
                "Total Kort: " + totalCards);
    }

}
