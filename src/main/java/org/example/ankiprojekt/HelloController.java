package org.example.ankiprojekt;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import org.controlsfx.control.CheckListView;

import java.io.*;
import java.util.UUID;

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
    private TableView<Deck> tbview_decks;

    @FXML
    private BorderPane borderPane_Main;

    @Setter
    private Stage ownerStage;

    AnkiDeckImporter importer = new AnkiDeckImporter();

    public void initialize() {
        initializeTableView();
        load();
    }

    private void initializeTableView() {
        TableColumn<Deck, String> columnName = new TableColumn<>("Navn");
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Deck, Integer> columnSize = new TableColumn<>("Kort mængde");
        columnSize.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDeckDemplate().size()).asObject());

        TableColumn<Deck, Integer> columnLearned = new TableColumn<>("Kort lært");
        columnLearned.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmountOfLearnedCards()));

        TableColumn<Deck, String> columnLearnedPercent = new TableColumn<>("%");
        columnLearnedPercent.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPercentageOfLearnedCards()));

        tbview_decks.getColumns().add(columnName);
        tbview_decks.getColumns().add(columnSize);
        tbview_decks.getColumns().add(columnLearned);
        tbview_decks.getColumns().add(columnLearnedPercent);

        tbview_decks.setRowFactory(tv -> {
            TableRow<Deck> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                Deck selectedDeck = row.getItem();
                if (selectedDeck == null) {
                    System.out.println("No deck selected!");
                    return;
                }

                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    // Dobbeltklik
                    switchToGameView(selectedDeck);
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    // Højreklik
                    editDeck(selectedDeck);
                }
            });
            return row;
        });
    }
    private void editDeck(Deck selectedDeck) {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Remove cards in deck");
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        popupStage.initOwner(ownerStage); // Set the owner stage

        Label listviewLabel = new Label("Cards in deck template:");

        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        CheckListView<Card> checkListView = new CheckListView<>();

        checkListView.getItems().addAll(selectedDeck.getDeckDemplate());

        checkListView.getCheckModel().checkAll();

        okButton.setOnAction(e -> {
            // Get the checklist's check model
            var checkModel = checkListView.getCheckModel();

            // Iterate through all items in the checklist
            for (Card card : checkListView.getItems()) {
                if (checkModel.isChecked(card)) {
                    // If the card is checked and not already in the deck, add it
                    if (!selectedDeck.getDeckDemplate().contains(card)) {
                        selectedDeck.add(card);
                        System.out.println("Added card to deckTemplate: " + card);
                    }
                } else {
                    // If the card is unchecked and in the deck, remove it
                    if (selectedDeck.getDeckDemplate().contains(card)) {
                        selectedDeck.remove(card);
                        System.out.println("Removed card from deckTemplate: " + card);
                    }
                }
            }

            popupStage.close(); // Close the popup
        });

        cancelButton.setOnAction(e -> popupStage.close());

        VBox popupLayout = new VBox(10, listviewLabel, checkListView, okButton, cancelButton);

        popupLayout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Create and set the scene for the popup stage
        Scene popupScene = new Scene(popupLayout, 300, 150);
        popupStage.setScene(popupScene);

        // Show the popup
        popupStage.showAndWait();
    }

    @FXML
    private void createNewCard() {

        String imagePath;

        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Create new card");
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        popupStage.initOwner(ownerStage); // Set the owner stage

        ComboBox<Deck> comboBox_Decks = new ComboBox<>();
        comboBox_Decks.getItems().addAll(DecksDatabase.getInstance().getDecks());

        // OK and Cancel buttons
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        TextField tf_question = new TextField();
        tf_question.setPromptText("Enter question");

        TextField tf_back1 = new TextField();
        tf_back1.setPromptText("Enter back answer line 1");

        TextField tf_back2 = new TextField();
        tf_back2.setPromptText("Enter back answer line 2");

        TextField tf_back3 = new TextField();
        tf_back3.setPromptText("Enter back answer line 3");

        FileChooser imageChooser = new FileChooser();
        imageChooser.setTitle("Choose image");
        imageChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png")
        );

        File selectedImage = imageChooser.showOpenDialog(popupStage);
        if (selectedImage != null) {
            imagePath = (selectedImage.getAbsolutePath());
        } else {
            imagePath = "No image selected";
        }


        okButton.setOnAction(e -> {

            if (!tf_question.getText().isEmpty() && !tf_back1.getText().isEmpty() && comboBox_Decks.getSelectionModel().getSelectedItem() != null) {
                Card c = new Card(UUID.randomUUID().toString(),  imagePath, tf_question.getText(), tf_back1.getText(), tf_back2.getText(), tf_back3.getText());

                Deck d = comboBox_Decks.getSelectionModel().getSelectedItem();

                c.setDeckName(d.name);

                d.add(c);

            } else {
                Alert a = new Alert(Alert.AlertType.ERROR, "Please fill in at least question and back answer line 1!");
                a.showAndWait();
                return;
            }

            popupStage.close(); // Close the popup
        });

        cancelButton.setOnAction(e -> popupStage.close());

        // Arrange components in a layout
        HBox buttonsLayout = new HBox(10, okButton, cancelButton);
        buttonsLayout.setAlignment(Pos.BASELINE_CENTER);
        VBox popupLayout = new VBox(10, comboBox_Decks, tf_question, tf_back1, tf_back2, tf_back3, buttonsLayout);

        popupLayout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Create and set the scene for the popup stage
        Scene popupScene = new Scene(popupLayout, 300, 500);
        popupStage.setScene(popupScene);

        // Show the popup
        popupStage.showAndWait();
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

    /*
    public void populateDecks() {
        LV_Decks.getItems().clear();
        LV_Decks.getItems().addAll(DecksDatabase.getInstance().getDecks());
        System.out.println("Decks: " + DecksDatabase.getInstance().getDecks());
    }
*/

    public void populateDecks() {
        tbview_decks.getItems().clear();
        tbview_decks.getItems().addAll(DecksDatabase.getInstance().getDecks());
        System.out.println("Decks: " + DecksDatabase.getInstance().getDecks());
    }


    @FXML
    private void createNewDeck() {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Create new deck");
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        popupStage.initOwner(ownerStage); // Set the owner stage

        // TextField to input text
        TextField textField = new TextField();
        Label label = new Label("Enter deck name:");
        textField.setPromptText("Enter some text");

        // OK and Cancel buttons
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        // Set button actions
        okButton.setOnAction(e -> {
            String input = textField.getText();

            if (input == null || input.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Please enter a deck name!");
                a.showAndWait();
                return;
            }

            DecksDatabase.getInstance().addDeck(handleCreateNewDeck(input));

            populateDecks();

            save();

            System.out.println("New deck created: " + input); // Perform action with the input
            popupStage.close(); // Close the popup
        });

        cancelButton.setOnAction(e -> popupStage.close());

        // Arrange components in a layout
        HBox buttonsLayout = new HBox(10, okButton, cancelButton);
        buttonsLayout.setAlignment(Pos.BASELINE_CENTER);
        VBox popupLayout = new VBox(10, label, textField, buttonsLayout );

        popupLayout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Create and set the scene for the popup stage
        Scene popupScene = new Scene(popupLayout, 300, 150);
        popupStage.setScene(popupScene);

        // Show the popup
        popupStage.showAndWait();
    }

    private Deck handleCreateNewDeck(String deckName) {
        Deck d = new Deck();
        d.setName(deckName);
        return d;
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

        // Save
        save();
    }

    @FXML
    private void save() {
        DataSaver.getInstance().save();
    }

    @FXML
    public void load() {
        DataSaver.getInstance().load(); // Loads and sets the singleton instance
        populateDecks(); // Refreshes the ListView with the updated database
    }


}