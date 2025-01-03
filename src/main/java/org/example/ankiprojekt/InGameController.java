package org.example.ankiprojekt;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.Setter;

import java.nio.file.Paths;
import java.util.Objects;
import java.util.Random;

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
    private HBox hbox_AnswerOptions;

    @Setter
    Deck selectedDeck;

    Card currentCard;

    @FXML
    public void startGame() {
        if (selectedDeck != null) {

            if (Objects.equals(selectedDeck.getDeck().getFirst().getNotetype(), "Art")) {
                label_Top.setText("Artist?");
            }

            showCard(selectedDeck.getDeck().getFirst());

            btn_StartGame.setDisable(true);
            btn_StartGame.setVisible(false);

            hbox_AnswerOptions.setVisible(false);

        } else {
            System.out.println("No Deck selected");
        }
    }

    @FXML
    private void showAnswer() {
        label_Answer.setText(currentCard.getBackArtist() + " " + currentCard.getBackTitle() + " " + currentCard.getBackYear()); // Set the text for the answer label
        hbox_AnswerOptions.setVisible(true);
    }

    private void showCard(Card card) {
        if (card.getImagePath() != null) {
            // Create a valid file URL
            String fileUrl = "file:///" + Paths.get(card.getImagePath()).toUri().getPath();

            // Load and set the image
            img_Image.setImage(new Image(fileUrl));

            // Set reference to the current card
            currentCard = card;

            //
            hbox_AnswerOptions.setVisible(false);
            label_Answer.setText("");
        }
    }

    @FXML
    private void korrekt() {
        int rnd = new Random().nextInt(selectedDeck.getDeck().size());
        showCard(selectedDeck.getDeck().get(rnd));
    }

    @FXML
    private void næstenKorrekt() {

    }

    @FXML
    private void delvistKorrekt() {

    }

    @FXML
    private void ikkeKorrekt() {

    }


}
