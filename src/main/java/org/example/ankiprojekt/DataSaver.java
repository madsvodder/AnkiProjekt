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
        try (FileOutputStream fos = new FileOutputStream("output.dat");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            // Gem hele UserDatabase inkl. hver brugers DecksDatabase
            oos.writeObject(UserDatabase.getInstance());

            oos.flush();
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

            UserDatabase loadedUserDatabase = (UserDatabase) oip.readObject();
            UserDatabase.setInstance(loadedUserDatabase);

            System.out.println("Data loaded!");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }




    // Helper methods //
    private boolean doesSaveFileExist() {
        return new File("output.dat").exists();
    }
}
