package org.example.ankiprojekt;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        Scene scene = new Scene(fxmlLoader.load());
        HelloController controller = fxmlLoader.getController();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("Application is closing. Saving data...");
        if (!DecksDatabase.getInstance().getDecks().isEmpty()) {
            DataSaver.getInstance().save(); // Gem KUN hvis der er data at gemme
        }
    }

    /*
    @Override
    public void init() {
        // Call the loading process early to ensure data is loaded before any access
        DataSaver.getInstance().load();
        System.out.println("App initialized and data loaded.");
    }
    */
    public static void main(String[] args) {
        launch();
    }
}