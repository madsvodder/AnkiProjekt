package org.example.ankiprojekt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnkiDeckImporter {

    DecksDatabase db = DecksDatabase.getInstance();

    public void importAnkiDeck(String filePath, String imageDirectory) {

        // Create the new deck
        Deck newDeck = new Deck();

        // add the deck to the database
        db.addDeck(newDeck);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<Card> cards = new ArrayList<Card>();

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
                String back = columns[4]; // Example for additional fields

                // Extract the image
                String imagePath = extractImagePath(frontHtml);

                // Create a new card object
                cards.add(new Card(guid, notetype, deck, frontHtml, back, imagePath));

                // Add all cards to the new deck
                cards.forEach(newDeck::add);

                // Set the name of the deck
                newDeck.setName(deck);
            }

            // Print the cards for debugging
            //cards.forEach(System.out::println);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String extractImagePath(String html) {
        // Parse the HTML content to extract the src attribute of <img>
        Document doc = Jsoup.parse(html);
        Element img = doc.selectFirst("img");
        return img != null ? img.attr("src") : null;
    }

}
