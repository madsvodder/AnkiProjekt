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

public class AnkiDeckImporter {

    DecksDatabase db = DecksDatabase.getInstance();

    private String directory;
    public void importAnkiDeck(String filePath, String imageDirectory) {

        directory = imageDirectory;

        // Create the new deck
        Deck newDeck = new Deck();

        // add the deck to the database
        db.addDeck(newDeck);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<Card> cards = new ArrayList<>();

            // Skip the metadata lines
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue;

                // Split by tab
                String[] columns = line.split("\t");

                // Check that we have enough columns
                if (columns.length < 4) continue;

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

                String fullImagePath = createFullImagePath(imagePath, imageDirectory);

                // Create a new card object
                cards.add(new Card(guid, notetype, deck, fullImagePath, backArtist, backTitle, backYear));

                System.out.println("Full image path: " + fullImagePath);
            }

            // Add all cards to the new deck after processing the file
            cards.forEach(newDeck::add);

            // Set the name of the deck (assuming all cards have the same deck name)
            if (!cards.isEmpty()) {
                newDeck.setName(cards.get(0).getDeck());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String extractImagePath(String html) {
        // Clean up extra quotes and trim whitespace
        String cleanedHtml = html.replace("\"\"", "\"").trim();  // Replace "" with "

        // Parse the cleaned HTML content
        Document doc = Jsoup.parse(cleanedHtml);

        // Debug: Print the cleaned HTML and parsed document
        System.out.println("Cleaned HTML: " + cleanedHtml);
        System.out.println("Parsed HTML: " + doc);

        // Select the first <img> element
        Element img = doc.selectFirst("img");

        // Check if an <img> tag is found and extract the src attribute
        if (img != null) {
            String imagePath = img.attr("src");

            // Debug: Print the extracted image path
            System.out.println("Extracted Image Path: " + imagePath);

            return imagePath;
        } else {
            System.out.println("No <img> tag found in the HTML.");
        }

        // Return null if no image is found
        return null;
    }

    private String createFullImagePath(String imageName, String imagePath) {
        // Combine
        String fullImagePath = Paths.get(imagePath, imageName).toString();
        return fullImagePath;
    }

    public static String cleanHtmlTags(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("<[^>]*>", "").trim();
    }


}
