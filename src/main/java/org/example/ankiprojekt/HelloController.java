package org.example.ankiprojekt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private ListView<Deck> LV_Decks;

    @FXML
    private BorderPane borderPane_Main;

    DecksDatabase db = DecksDatabase.getInstance();

    AnkiDeckImporter importer = new AnkiDeckImporter();

    public void initialize() {

        LV_Decks.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Deck d = LV_Decks.getSelectionModel().getSelectedItem();
                switchToGameView(d);
            }
        });

        load();
    }

    private void switchToGameView(Deck selectedDeck) {
        try {
            // Load the new FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ingame-view.fxml"));
            BorderPane gameView = fxmlLoader.load();

            // Set the loaded FXML view as the center of the borderPane_Main
            borderPane_Main.setCenter(gameView);

            // Optional: Get the controller of the ingame-view if needed
            InGameController gameController = fxmlLoader.getController();
            gameController.customInit(selectedDeck);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load ingame-view.fxml");
        }
    }

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

    @FXML
    public void save() {

        // Create the FileOutputStream
        try (FileOutputStream fos = new FileOutputStream("output.dat");) {

            // Create the ObjectOutputStream
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Write the database object
            oos.writeObject(db);

            System.out.println("Saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void load() {

        if (!doesSaveFileExist()) {
            System.out.println("No save file found!");
            return;
        }

        // Create the FileInputStream
        try (FileInputStream fileInputStream = new FileInputStream("output.dat");) {

            // Create the ObjectInputStream
            ObjectInputStream oip = new ObjectInputStream(fileInputStream);

            // Read the database object from the .dat file
            DecksDatabase database = (DecksDatabase) oip.readObject();

            // Set the database to the loaded database
            db = database;

            // Repopulate / refresh
            populateDecks();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper methods //
    private boolean doesSaveFileExist() {
        return new File("output.dat").exists();
    }

}