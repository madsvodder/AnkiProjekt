package org.example.ankiprojekt;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AnkiApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AnkiApplication.class.getResource("userselect-view.fxml"));
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        Scene scene = new Scene(fxmlLoader.load());
        UserSelectController controller = fxmlLoader.getController();
        controller.setOwnerStage(stage);
        controller.refresh();
        //HelloController controller = fxmlLoader.getController();
        //controller.setOwnerStage(stage);
        stage.setTitle("Anki projekt");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("Application is closing. Saving data...");
        DataSaver.getInstance().save();
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