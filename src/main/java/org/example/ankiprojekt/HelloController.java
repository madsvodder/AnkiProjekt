package org.example.ankiprojekt;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private ListView<Deck> LV_Decks;

    DecksDatabase db = DecksDatabase.getInstance();

    AnkiDeckImporter importer = new AnkiDeckImporter();

    public void initialize() {}
    private void populateDecks() {
        LV_Decks.getItems().clear();
        LV_Decks.getItems().addAll(db.getDecks());
        System.out.println("Decks: " + db.getDecks());
    }

    @FXML
    private void importAnkiDeck() {

        String txtPath;
        String imagesPath;

        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Choose Anki Deck");

        // Show the directory chooser dialog
        File selectedDirectory = directoryChooser.showDialog(welcomeText.getScene().getWindow());

        if (selectedDirectory != null) {
            // Get the txt file
            File[] txtFiles = selectedDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

            assert txtFiles != null;
            txtPath = txtFiles[0].getAbsolutePath();

            imagesPath = selectedDirectory.getAbsolutePath();

            importer.importAnkiDeck(txtPath, imagesPath);

            System.out.println("Anki Deck imported!" + txtPath);
        }

        // Refresh the decks list
        populateDecks();
    }
}