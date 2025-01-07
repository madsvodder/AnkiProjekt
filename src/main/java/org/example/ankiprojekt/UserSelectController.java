package org.example.ankiprojekt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;

public class UserSelectController {

    @Setter
    private Stage ownerStage;

    @FXML
    private ListView<User> listview_userList;

    public void initialize() {
        DataSaver.getInstance().load();
        listview_userList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                User user = listview_userList.getSelectionModel().getSelectedItem();
                if (user != null) {
                    try {
                        // Close the old / this window
                        ownerStage.close();

                        // Load the new FXML
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                        Parent root = fxmlLoader.load();

                        // Get the controller and load
                        HelloController helloController = fxmlLoader.getController();
                        helloController.load();

                        // Create the new stage
                        Stage newStage = new Stage();
                        Scene scene = new Scene(root);
                        newStage.setScene(scene);
                        newStage.setTitle("Hello View");
                        newStage.show();

                        // Set the new owner stage
                        ownerStage = newStage;

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Fejl ved indlæsning af nyt vindue: " + e.getMessage());
                    }
                }
            }
        });
    }

    public void refresh() {
        listview_userList.getItems().clear();

        for (User users : UserDatabase.getInstance().getUsers()) {
            listview_userList.getItems().add(users);
        }
    }

    @FXML
    private void createUser() {
// Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Opret bruger");
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        popupStage.initOwner(ownerStage); // Set the owner stage

        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        Label nameLabel = new Label("Navn");
        TextField nameTextField = new TextField();
        nameTextField.setPromptText("Navn:");

        HBox nameHBox = new HBox(nameLabel, nameTextField);
        HBox buttonHBox = new HBox(cancelButton, okButton);
        nameHBox.setAlignment(Pos.CENTER);
        nameHBox.setSpacing(10);
        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.setSpacing(10);

        cancelButton.setOnAction(e -> {
            popupStage.close();
        });

        okButton.setOnAction(e -> {

            if (!nameTextField.getText().isEmpty()) {
                User user = new User(nameTextField.getText());
                UserDatabase.getInstance().addUser(user);
                System.out.println("Added new user: " + user);
                refresh();
                popupStage.close();
            };
        });

        VBox popupLayout = new VBox(10, nameHBox, buttonHBox);

        popupLayout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Create and set the scene for the popup stage
        Scene popupScene = new Scene(popupLayout, 350, 200);
        popupStage.setScene(popupScene);

        // Show the popup
        popupStage.showAndWait();
    }

    @FXML
    private void deleteUser() {
        if (listview_userList.getSelectionModel().getSelectedItem() != null) {
            User user = listview_userList.getSelectionModel().getSelectedItem();
            UserDatabase.getInstance().removeUser(user);
            System.out.println("Removed user: " + user);
            refresh();
        }
    }
}
