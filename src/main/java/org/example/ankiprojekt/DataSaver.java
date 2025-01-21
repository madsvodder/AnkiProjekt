package org.example.ankiprojekt;

import javafx.fxml.FXML;

import java.io.*;

public class DataSaver {
    private static DataSaver instance;

    // Singleton //
    public static synchronized DataSaver getInstance() {
        if (instance == null) {
            instance = new DataSaver();
        }
        return instance;
    }


    @FXML
    public void save() {
        File saveFile = new File(PathManager.saveDataFolderPath, "output.dat");

        try (FileOutputStream fos = new FileOutputStream(saveFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(UserDatabase.getInstance());
            oos.flush();

            System.out.println("Data gemt til: " + saveFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File saveFile = new File(PathManager.saveDataFolderPath, "output.dat");

        if (!saveFile.exists()) {
            System.out.println("Ingen gemmefil fundet i: " + saveFile.getAbsolutePath());
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream(saveFile);
             ObjectInputStream oip = new ObjectInputStream(fileInputStream)) {

            UserDatabase loadedUserDatabase = (UserDatabase) oip.readObject();
            UserDatabase.setInstance(loadedUserDatabase);

            System.out.println("Data indlæst fra: " + saveFile.getAbsolutePath());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Helper methods //
    private boolean doesSaveFileExist() {
        File saveFile = new File(PathManager.saveDataFolderPath, "output.dat");
        return saveFile.exists();
    }
}
