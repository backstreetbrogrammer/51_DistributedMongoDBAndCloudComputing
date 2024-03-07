package com.backstreetbrogrammer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class GuidemyOnline {

    private static final String MONGO_DB_URL
            = "mongodb://127.0.0.1:27017,127.0.0.1:27018,127.0.0.1:27019/?replicaSet=rs0";
    private static final String DB_NAME = "guidemy-online";
    private static final double MIN_PASSING_MARKS_PERCENTAGE = 90.0D;

    public static void main(final String[] args) {
        final String courseName = args[0];
        final String studentName = args[1];
        final int age = Integer.parseInt(args[2]);
        final double testMarksPercentage = Double.parseDouble(args[3]);

        final MongoDatabase guidemyOnlineDb = connectToMongoDB(MONGO_DB_URL, DB_NAME);
        enroll(guidemyOnlineDb, courseName, studentName, age, testMarksPercentage);
    }

    private static void enroll(final MongoDatabase database, final String courseName, final String studentName,
                               final int age, final double testMarksPercentage) {
        if (!isValidCourse(database, courseName)) {
            System.err.printf("Invalid course: %s%n", courseName);
            return;
        }

        final MongoCollection<Document> courseCollection
                = database.getCollection(courseName)
                          .withWriteConcern(WriteConcern.MAJORITY)
                          .withReadPreference(ReadPreference.primaryPreferred());

        if (courseCollection.find(eq("name", studentName)).first() != null) {
            System.out.printf("Student %s has already enrolled%n", studentName);
            return;
        }

        if (testMarksPercentage < MIN_PASSING_MARKS_PERCENTAGE) {
            System.out.printf("Please give the entrance test again and get more than cutoff: [%.1f] %n",
                              MIN_PASSING_MARKS_PERCENTAGE);
            return;
        }

        courseCollection.insertOne(new Document("name", studentName)
                                           .append("age", age)
                                           .append("testMarksPercentage", testMarksPercentage));
        System.out.printf("Student [%s] was successfully enrolled in course [%s]%n", studentName, courseName);

        for (final Document document : courseCollection.find()) {
            System.out.println(document);
        }
    }

    private static boolean isValidCourse(final MongoDatabase database, final String courseName) {
        boolean isCollectionFound = false;
        for (final String collectionName : database.listCollectionNames()) {
            if (collectionName.equals(courseName)) {
                isCollectionFound = true;
                break;
            }
        }
        return isCollectionFound;
    }

    public static MongoDatabase connectToMongoDB(final String url, final String dbName) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(url));
        return mongoClient.getDatabase(dbName);
    }

}
