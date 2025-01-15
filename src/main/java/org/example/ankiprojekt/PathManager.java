package org.example.ankiprojekt;

import java.io.File;

public class PathManager {

    // Save strings
    public static String ankiKlonFolderPath;
    public static String saveDataFolderPath;
    public static String decksFolderPath;
    public static String userCardsFolderPath;

    // Initialisering af stier (kald denne én gang ved programmets start)
    public static void initializePaths() {
        // Get the users documents folder
        String userDocumentsPath = System.getProperty("user.home") + File.separator + "Documents";

        // Create the paths
        File ankiKlonFolder = new File(userDocumentsPath, "AnkiKlon");
        File saveDataFolder = new File(ankiKlonFolder, "SaveData");
        File decksFolder = new File(ankiKlonFolder, "Decks");
        File userCardsFolder = new File(ankiKlonFolder, "UserCards");

        // Save the paths as strings
        ankiKlonFolderPath = ankiKlonFolder.getAbsolutePath();
        saveDataFolderPath = saveDataFolder.getAbsolutePath();
        decksFolderPath = decksFolder.getAbsolutePath();
        userCardsFolderPath = userCardsFolder.getAbsolutePath();

        // Create the folders, if they dont already exists
        if (!ankiKlonFolder.exists() && ankiKlonFolder.mkdirs()) {
            System.out.println("Oprettede mappe: " + ankiKlonFolderPath);
        }
        if (!saveDataFolder.exists() && saveDataFolder.mkdirs()) {
            System.out.println("Oprettede SaveData-mappe: " + saveDataFolderPath);
        }
        if (!decksFolder.exists() && decksFolder.mkdirs()) {
            System.out.println("Oprettede Decks-mappe: " + decksFolderPath);
        }
        if (!userCardsFolder.exists() && userCardsFolder.mkdirs()) {
            System.out.println("Oprettede UserCards-mappe: " + userCardsFolderPath);
        }
    }
}
