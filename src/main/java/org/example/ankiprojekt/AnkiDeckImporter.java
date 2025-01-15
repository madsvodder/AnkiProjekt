package org.example.ankiprojekt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnkiDeckImporter {

    public String decksFolderPath = PathManager.decksFolderPath;

    DecksDatabase decksDatabase;
    public AnkiDeckImporter(DecksDatabase decksDatabase) {
        this.decksDatabase = decksDatabase;
    }
    private static final Logger LOGGER = Logger.getLogger(AnkiDeckImporter.class.getName());

    private static final int MIN_COLUMNS = 7;
    private String directory;


    // Import an Anki deck from a folder (including txt file and associated images). //
    public void importDeckFromFolder(File selectedDirectory, DecksDatabase decksDatabase) {
        if (selectedDirectory == null || !selectedDirectory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory provided.");
        }

        File destinationFolder;

        // Copy the folder to the `decksFolderPath`
        try {
            destinationFolder = new File(decksFolderPath, selectedDirectory.getName());
            copyFolder(selectedDirectory.toPath(), destinationFolder.toPath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to copy folder to decks folder path", e);
            throw new RuntimeException("Failed to copy folder to decks folder path", e);
        }

        // Get txt files in the copied directory
        File[] txtFiles = destinationFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (txtFiles == null || txtFiles.length == 0) {
            throw new IllegalArgumentException("No .txt file found in the selected folder.");
        }

        // Use the paths from the copied folder
        String txtFilePath = txtFiles[0].getAbsolutePath();
        String imagesPath = destinationFolder.getAbsolutePath();

        // Import it
        importAnkiDeck(txtFilePath, imagesPath);
    }


    public void importAnkiDeck(String filePath, String imageDirectory) {

        directory = imageDirectory;

        // Create the new deck
        Deck newDeck = new Deck();

        // add the deck to the database
        User activeUser = UserDatabase.getInstance().getActiveUser();
        activeUser.getDecksDatabase().addDeck(newDeck);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<Card> cards = new ArrayList<>();

            // Skip the metadata lines
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue;

                // Split by tab
                String[] columns = line.split("\t");

                // Check that we have enough columns
                if (columns.length < MIN_COLUMNS) {
                    LOGGER.warning("Skipping line - not enough columns: " + line);
                    continue;
                }

                try {
                    // Extract data
                    String guid = columns[0];
                    String notetype = columns[1];
                    String deck = columns[2];
                    String frontHtml = columns[3];
                    String backArtist = cleanHtmlTags(columns[4]);
                    String backTitle = cleanHtmlTags(columns[5]);
                    String backYear = columns[7];

                    // Extract the image
                    String imagePath = extractImagePath(frontHtml);

                    if (imagePath == null) {
                        LOGGER.warning("No image found for line: " + line);
                    }

                    String fullImagePath = createFullImagePath(imagePath, imageDirectory);

                    // Create a new card object

                    Card card = new Card(guid, notetype, deck, fullImagePath, backArtist, backTitle, backYear);

                    setCardQuestions(card);

                    cards.add(card);

                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error reading line: " + line, e);
                }
            }

            // Add all cards to the new deck after processing the file
            cards.forEach(newDeck::add);

            // Set the name of the deck. Using the first card,
            // since they all SHOULD have the same name
            if (!cards.isEmpty()) {
                newDeck.setName(cards.getFirst().getDeckName());
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read the file: " + filePath, e);
            throw new RuntimeException(e);
        }
    }



    private void setCardQuestions(Card card) {
        if (card.getNotetype().equals("Art")) {
            card.setQuestion("Artist?");
        }
    }
    private static String extractImagePath(String html) {

        try {
            // Clean up extra quotes and trim whitespace
            String cleanedHtml = html.replace("\"\"", "\"").trim();  // Replace "" with "
            // Parse the cleaned HTML content
            Document doc = Jsoup.parse(cleanedHtml);

            // Select the first <img> element
            Element img = doc.selectFirst("img");
            if (img != null) {
                return img.attr("src");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error extracting image path from HTML: " + html, e);
        }
        // Return null if no image is found
        return null;
    }
    private String createFullImagePath(String imageName, String imagePath) {

        // Make sure that the image path is valid
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        // Combine
        return Paths.get(imagePath, imageName).toString();
    }
    public static String cleanHtmlTags(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("<[^>]*>", "").trim();
    }


    // Utility to copy folder
    private void copyFolder(Path source, Path destination) throws IOException {
        Files.walk(source).forEach(sourcePath -> {
            try {
                Path targetPath = destination.resolve(source.relativize(sourcePath));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

}
