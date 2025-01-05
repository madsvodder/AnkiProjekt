package org.example.ankiprojekt;

import javafx.fxml.FXML;

import java.io.*;

public class DataSaver {
    private static DataSaver instance;
    public static synchronized DataSaver getInstance() {
        if (instance == null) {
            instance = new DataSaver();
        }
        return instance;
    }


    @FXML
    public void save() {

        // Create the FileOutputStream
        try (FileOutputStream fos = new FileOutputStream("output.dat");) {

            // Create the ObjectOutputStream
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Write the database object
            oos.writeObject(DecksDatabase.getInstance());

            System.out.println("Saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!doesSaveFileExist()) {
            System.out.println("No save file found!");
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream("output.dat");
             ObjectInputStream oip = new ObjectInputStream(fileInputStream)) {

            DecksDatabase loadedDatabase = (DecksDatabase) oip.readObject();

            // Set the loaded instance and reinitialize observable list
            DecksDatabase.setInstance(loadedDatabase);

            System.out.println("Data loaded!");

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }




    // Helper methods //
    private boolean doesSaveFileExist() {
        return new File("output.dat").exists();
    }
}
