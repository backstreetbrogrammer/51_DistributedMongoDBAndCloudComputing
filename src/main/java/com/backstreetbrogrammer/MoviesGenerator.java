package com.backstreetbrogrammer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Launching a Sharded Distributed MongoDB
 */
public class MoviesGenerator {
    private static final String MONGO_DB_URL = "mongodb://127.0.0.1:27023";
    private static final String DB_NAME = "videodb";
    private static final String COLLECTION_NAME = "movies";

    private static final Random random = new Random();

    public static void main(final String[] args) {
        final MongoDatabase onlineSchoolDb = connectToMongoDB(MONGO_DB_URL, DB_NAME);
        generateMovies(10000, onlineSchoolDb, COLLECTION_NAME);
    }

    private static MongoDatabase connectToMongoDB(final String url, final String dbName) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(url));
        return mongoClient.getDatabase(dbName);
    }

    private static void generateMovies(final int numberOfMovies, final MongoDatabase database, final String collectionName) {
        final MongoCollection<Document> collection = database.getCollection(collectionName);

        final List<Document> documents = new ArrayList<>();
        System.out.println("Generating " + numberOfMovies + " movies");
        for (int movieIndex = 0; movieIndex < numberOfMovies; movieIndex++) {
            final Document document = new Document();
            document.append("_id", UUID.randomUUID().toString())
                    .append("name", generateName())
                    .append("directors", generateDirectorNames())
                    .append("year", generateYear())
                    .append("cast", generateCast())
                    .append("rating", generateRating());
            documents.add(document);
        }

        collection.insertMany(documents);

        System.out.println("Finished generating movies");
    }

    /**
     * Returns a list of random movie directors
     */
    private static List<String> generateDirectorNames() {
        final int numberOfDirectors = random.nextInt(3) + 1;
        final List<String> directors = new ArrayList<>(numberOfDirectors);

        for (int i = 0; i < numberOfDirectors; i++) {
            final String firstName = generateName();
            final String lastName = generateName();
            directors.add(firstName + " " + lastName);
        }

        return directors;
    }

    /**
     * Returns a random year
     */
    private static int generateYear() {
        return random.nextInt(119) + 1900;
    }

    /**
     * Returns a random rating
     */
    private static float generateRating() {
        return random.nextFloat() * 10.0f;
    }

    /**
     * Returns a list of random actors
     */
    private static List<String> generateCast() {
        final int numberOfActors = random.nextInt(20) + 10;
        final List<String> actors = new ArrayList<>(numberOfActors);

        for (int i = 0; i < numberOfActors; i++) {
            final String firstName = generateName();
            final String lastName = generateName();
            actors.add(firstName + " " + lastName);
        }

        return actors;
    }

    /**
     * Returns a random full name
     */
    private static String generateName() {
        final StringBuilder name = new StringBuilder();

        name.append(RandomStringUtils.randomAlphabetic(1).toUpperCase());
        name.append(RandomStringUtils.randomAlphabetic(5, 10).toLowerCase());

        return name.toString();
    }
}
