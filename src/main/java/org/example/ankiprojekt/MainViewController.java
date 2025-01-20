package org.example.ankiprojekt;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

public class MainViewController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private ListView<Deck> LV_Decks;

    @FXML
    private Label label_cardsLearned;

    @FXML
    private TableView<Deck> tbview_decks;

    @FXML
    private BorderPane borderPane_Main;

    @FXML
    private ImageView img_deck1;

    @FXML
    private Label label_deck1;

    @FXML
    private ProgressBar progress_deck1;

    @FXML
    private ImageView img_deck2;

    @FXML
    private Label label_deck2;

    @FXML
    private ProgressBar progress_deck2;

    @FXML
    private ImageView img_deck3;

    @FXML
    private Label label_deck3;

    @FXML
    private ProgressBar progress_deck3;

    @Setter
    private Stage ownerStage;

    User activeUser = UserDatabase.getInstance().getActiveUser();

    @Setter
    private DecksDatabase decksDatabase = activeUser.getDecksDatabase();

    AnkiDeckImporter importer = new AnkiDeckImporter(decksDatabase);

    public void initialize() {
        initializeTableView();

        load();

        updateStatistics();
    }



    private void refreshRecentDecksUI() {

        List<Deck> recentDecks = decksDatabase.getRecentDecks();

        // Null
        if (recentDecks == null) {
            label_deck1.setText("");
            label_deck2.setText("");
            label_deck3.setText("");
            return;
        }

        // Setup recent decks UI
        if (recentDecks.size() > 0 && recentDecks.get(0) != null) {
            setDeckUI(label_deck1, progress_deck1, img_deck1, recentDecks.get(0));
        } else {
            clearDeckUI(label_deck1, progress_deck1, img_deck1);
        }

        if (recentDecks.size() > 1 && recentDecks.get(1) != null) {
            setDeckUI(label_deck2, progress_deck2, img_deck2, recentDecks.get(1));
        } else {
            clearDeckUI(label_deck2, progress_deck2, img_deck2);
        }

        if (recentDecks.size() > 2 && recentDecks.get(2) != null) {
            setDeckUI(label_deck3, progress_deck3, img_deck3, recentDecks.get(2));
        } else {
            clearDeckUI(label_deck3, progress_deck3, img_deck3);
        }
    }

    private void setDeckUI(Label label, ProgressBar progressBar, ImageView imageView, Deck deck) {
        label.setText(deck.getName());
        progressBar.setProgress(deck.getPercentageOfLearnedCardsDouble() / 100.0);
        progressBar.setVisible(true);

        if (deck.getDeckDemplate() != null && !deck.getDeckDemplate().isEmpty()) {

            String imagePath = deck.getDeckDemplate().get(0).getImagePath();

            if (imagePath != null && !imagePath.isEmpty()) {

                System.out.println("Setting deck image path: " + imagePath + " For: " + deck.getName());

                Image image = new Image(new File(imagePath).toURI().toString());
                imageView.setImage(image);
                imageView.setFitHeight(150);
                imageView.setFitWidth(200);
            } else {
                System.out.println("Setting deck image placeholder!");
                imageView.setImage(new Image(getClass().getResource("/images/DeckPlaceholder.png").toExternalForm()));
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
            }
        }
    }

    private void clearDeckUI(Label label, ProgressBar progressBar, ImageView imageView) {
        label.setText("");
        progressBar.setProgress(0.0);
        progressBar.setVisible(false);
        imageView.setImage(null);
    }

    private void updateStatistics() {

        int totalLearnedCards = Statistics.getInstance().getLearnedCards();
        int notLearnedCards = Statistics.getInstance().getNotLearnedCards();

        label_cardsLearned.setText(
                  "Du har lært " + totalLearnedCards + " kort.\n"
                + "Du har " + notLearnedCards + " kort tilbage."
        );

        refreshRecentDecksUI();
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

            // Create context menu
            ContextMenu contextMenu = new ContextMenu();

            // Create the menu items
            MenuItem editMenuItem = new MenuItem("Rediger");
            MenuItem removeMenuItem = new MenuItem("Fjern");

            editMenuItem.setOnAction(e -> {
                Deck selectedDeck = row.getItem();
                if (selectedDeck == null) {
                    System.out.println("No deck selected!");
                    return;
                } else {
                    editDeck(selectedDeck);
                }
            });

            removeMenuItem.setOnAction(e -> {
                Deck selectedDeck = row.getItem();
                if (selectedDeck == null) {
                    System.out.println("No deck selected!");
                    return;
                } else {
                    decksDatabase.removeDeck(selectedDeck);
                    populateDecks();
                    updateStatistics();
                    save();
                }
            });

            // Add the menu items to the context menu
            contextMenu.getItems().addAll(editMenuItem, removeMenuItem);

            row.setContextMenu(contextMenu);

            row.setOnMouseClicked(event -> {
                Deck selectedDeck = row.getItem();
                if (selectedDeck == null) {
                    System.out.println("No deck selected!");
                    return;
                }

                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    // Add deck to recent
                    decksDatabase.addDeckToRecentDecks(selectedDeck);

                    // Dobbeltklik
                    switchToGameView(selectedDeck);
                }
            });
            return row;
        });
    }
    private void editDeck(Deck selectedDeck) {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Rediger deck");
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        popupStage.initOwner(ownerStage); // Set the owner stage

        Label listviewLabel = new Label("Cards in deck template:");

        Button addUserCardButton = new Button("Add user card");
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        CheckListView<Card> checkListView = new CheckListView<>();

        checkListView.getItems().addAll(selectedDeck.getDeckDemplate());

        checkListView.getCheckModel().checkAll();

        addUserCardButton.setOnAction(e -> {
            addUserCardPopup(selectedDeck, () -> {
                // Refresh the CheckListView with updated deck data
                checkListView.getItems().clear();
                checkListView.getItems().addAll(selectedDeck.getDeckDemplate());
                checkListView.getCheckModel().checkAll();
            });
        });

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

            populateDecks();

            popupStage.close(); // Close the popup
        });

        cancelButton.setOnAction(e -> {

            populateDecks();

            popupStage.close();
        });

        VBox popupLayout = new VBox(10, listviewLabel, checkListView, okButton, cancelButton, addUserCardButton);

        popupLayout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Create and set the scene for the popup stage
        Scene popupScene = new Scene(popupLayout, 350, 600);
        popupStage.setScene(popupScene);

        // Show the popup
        popupStage.showAndWait();
    }

    @FXML
    private void editUserCards() {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Fjern kort i bruger database");
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        popupStage.initOwner(ownerStage); // Set the owner stage

        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        Label listviewLabel = new Label("Kort i bruger database:");

        CheckListView<Card> checkListView = new CheckListView<>();

        checkListView.getItems().addAll(decksDatabase.getUserCards());

        checkListView.getCheckModel().checkAll();

        okButton.setOnAction(e -> {
            var checkModel = checkListView.getCheckModel();

            // Brug en iterator for sikker fjernelse
            var iterator = decksDatabase.getUserCards().iterator();
            while (iterator.hasNext()) {
                Card card = iterator.next();
                if (!checkModel.isChecked(card)) {
                    iterator.remove(); // Fjern kortet sikkert fra listen
                    System.out.println("Removed card from user database: " + card);

                    // Fjern kortet fra alle decks, der indeholder det
                    for (Deck deck : decksDatabase.getDecks()) {
                        deck.remove(card);
                    }
                }
            }

            populateDecks();    // Opdater decks i visningen
            updateStatistics(); // Opdater statistik
            save();             // Gem ændringer
            popupStage.close(); // Luk popup-vinduet
        });

        cancelButton.setOnAction(e -> {

            populateDecks();

            popupStage.close();
        });


        VBox popupLayout = new VBox(10, listviewLabel, checkListView, okButton, cancelButton);

        popupLayout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Create and set the scene for the popup stage
        Scene popupScene = new Scene(popupLayout, 350, 600);
        popupStage.setScene(popupScene);

        // Show the popup
        popupStage.showAndWait();
    }
    private void addUserCardPopup(Deck selectedDeck, Runnable callbackAfterClose) {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Add card from user database");
        popupStage.initOwner(ownerStage); // Set the owner stage

        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        CheckListView<Card> checkListUserCards = new CheckListView<>();

        for (Card card : decksDatabase.getUserCards()) {
            if (!selectedDeck.doesDeckContainCard(card) && !decksDatabase.isCardInAnyDeck(card)) {
                checkListUserCards.getItems().add(card);
            }
        }

        okButton.setOnAction(e -> {

            // Get the checklist's check model
            var checkModel = checkListUserCards.getCheckModel();

            for (Card card : checkListUserCards.getItems()) {
                if (checkModel.isChecked(card)) {
                    if (!selectedDeck.getDeckDemplate().contains(card)) {
                        selectedDeck.add(card);
                        System.out.println("Added card to deckTemplate: " + card);
                    } else {
                        System.out.println("Card already in deckTemplate: " + card);
                    }
                }
            }

            // Call the refresh callback after data modification
            if (callbackAfterClose != null) {
                callbackAfterClose.run();
            }

            popupStage.close(); // Close the popup
        });

        cancelButton.setOnAction(e -> {

            // Call the refresh callback after data modification
            if (callbackAfterClose != null) {
                callbackAfterClose.run();
            }

            popupStage.close();
        });


        VBox popupLayout = new VBox(10, checkListUserCards, okButton, cancelButton);

        Scene popupScene = new Scene(popupLayout, 300, 150);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    @FXML
    private void createNewCard() {

        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Opret nyt kort");
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        popupStage.initOwner(ownerStage); // Set the owner stage

        // OK and Cancel buttons
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        Button addImageButton = new Button("Tilføj billede");

        TextField tf_question = new TextField();
        tf_question.setPromptText("Spørgsmål");

        TextField tf_back1 = new TextField();
        tf_back1.setPromptText("Svar linje 1");

        TextField tf_back2 = new TextField();
        tf_back2.setPromptText("Svar linje 2");

        TextField tf_back3 = new TextField();
        tf_back3.setPromptText("Svar linje 3");

        // Use a mutable array to hold the image path
        final String[] imagePath = {null};
        final String destinationImagePath = null;

        addImageButton.setOnAction(e -> {
            FileChooser imageChooser = new FileChooser();
            imageChooser.setTitle("Choose image");
            imageChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png")
            );

            File selectedImage = imageChooser.showOpenDialog(popupStage);
            if (selectedImage != null) {
                imagePath[0] = selectedImage.getAbsolutePath();
            } else {
                imagePath[0] = null;
            }
        });

        okButton.setOnAction(e -> {

            if (!tf_question.getText().isEmpty() && !tf_back1.getText().isEmpty()) {
                String selectedImagePath = imagePath[0];

                if (selectedImagePath != null) {
                    try {
                        // Copy the image to the userCardsFolderPath
                        Path sourcePath = Paths.get(selectedImagePath);
                        Path destinationPath = Paths.get(PathManager.userCardsFolderPath, sourcePath.getFileName().toString());

                        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        selectedImagePath = destinationPath.toString();
                    } catch (IOException ex) {
                        Alert a = new Alert(Alert.AlertType.ERROR, "Failed to copy image: " + ex.getMessage());
                        a.showAndWait();
                        return;
                    }
                }

                Card c = new Card(
                        UUID.randomUUID().toString(),
                        destinationImagePath,
                        tf_question.getText(),
                        tf_back1.getText(),
                        tf_back2.getText(),
                        tf_back3.getText()
                );

                // Add the new custom card to the custom cards database
                decksDatabase.getUserCards().add(c);

                // Save the card as a .dat-file in the UserCards folder
                saveCardToFile(c);

            } else {
                Alert a = new Alert(Alert.AlertType.ERROR, "Please fill in at least question and back answer line 1!");
                a.showAndWait();
                return;
            }

            popupStage.close(); // Close the popup
        });

        cancelButton.setOnAction(e -> popupStage.close());

        // Arrange components in a layout
        HBox buttonsLayout = new HBox(10, addImageButton, okButton, cancelButton);
        buttonsLayout.setAlignment(Pos.BASELINE_CENTER);
        VBox popupLayout = new VBox(10, tf_question, tf_back1, tf_back2, tf_back3, buttonsLayout);

        popupLayout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Create and set the scene for the popup stage
        Scene popupScene = new Scene(popupLayout, 300, 500);
        popupStage.setScene(popupScene);

        // Show the popup
        popupStage.showAndWait();
    }


    @FXML
    private void importCustomCard() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose custom card");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Custom Card Files", "*.dat")
        );
        File selectedFile = fileChooser.showOpenDialog(ownerStage);

        if (selectedFile == null) {
            System.out.println("No file selected!");
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream(selectedFile);
             ObjectInputStream oip = new ObjectInputStream(fileInputStream)) {

            Card loadedUserCard = (Card) oip.readObject();

            decksDatabase.getUserCards().add(loadedUserCard);

            System.out.println("Kort indlæst fra: " + selectedFile.getAbsolutePath());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveCardToFile(Card card) {
        // Skab filstien baseret på UUID og gem den i UserCards-mappen
        File cardFile = new File(PathManager.userCardsFolderPath, card.getGuid() + ".dat");

        try (FileOutputStream fos = new FileOutputStream(cardFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            // Gem Card-objektet som en fil
            oos.writeObject(card);
            oos.flush();

            System.out.println("Card saved to file: " + cardFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save card: " + e.getMessage());
            alert.showAndWait();
        }
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

    public void populateDecks() {
        tbview_decks.getItems().clear();
        tbview_decks.getItems().addAll(decksDatabase.getDecks());
        System.out.println("Decks: " + decksDatabase.getDecks());
    }

    @FXML
    private void createNewDeck() {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Opret nyt deck");
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        popupStage.initOwner(ownerStage); // Set the owner stage

        // TextField to input text
        TextField textField = new TextField();
        Label label = new Label("Deck navn:");
        textField.setPromptText("Navn");

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

            decksDatabase.addDeck(handleCreateNewDeck(input));

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
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose Anki Deck");

            File selectedDirectory = directoryChooser.showDialog(welcomeText.getScene().getWindow());
            if (selectedDirectory == null) return;

            // Import the deck using the importer
            importer.importDeckFromFolder(selectedDirectory, decksDatabase);

            // Update UI
            populateDecks();
            save();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to import Anki Deck: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void save() {
        DataSaver.getInstance().save();
    }



    @FXML
    public void load() {
        // Brug den tildelte decksDatabase til at vise data
        if (decksDatabase != null) {
            tbview_decks.getItems().clear();
            tbview_decks.getItems().addAll(decksDatabase.getDecks());
            System.out.println("Loaded decks for the active user: " + decksDatabase.getDecks());
        }
    }


}