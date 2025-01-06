package org.example.ankiprojekt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnkiDeckImporter {

    private static final Logger LOGGER = Logger.getLogger(AnkiDeckImporter.class.getName());

    private static final int MIN_COLUMNS = 7;
    private String directory;
    public void importAnkiDeck(String filePath, String imageDirectory) {

        directory = imageDirectory;

        // Create the new deck
        Deck newDeck = new Deck();

        // add the deck to the database
        DecksDatabase.getInstance().addDeck(newDeck);

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
                    cards.add(new Card(guid, notetype, deck, fullImagePath, backArtist, backTitle, backYear));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error reading line: " + line, e);
                }
            }

            // Add all cards to the new deck after processing the file
            cards.forEach(newDeck::add);

            // Set the name of the deck. Using the first card,
            // since they all SHOULD have the same name
            if (!cards.isEmpty()) {
                newDeck.setName(cards.get(0).getDeck());
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read the file: " + filePath, e);
            throw new RuntimeException(e);
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


}
