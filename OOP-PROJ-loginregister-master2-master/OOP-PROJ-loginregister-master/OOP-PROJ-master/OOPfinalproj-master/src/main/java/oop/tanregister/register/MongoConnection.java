package oop.tanregister.register;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
    private static final String CONNECTION_STRING =
            "mongodb+srv://jeshemymarie:kUkwADF4GNu4aMgE@test-cluster.6ihleq7.mongodb.net/?retryWrites=true&w=majority";
    private static final String DB_NAME = "Userdata";

    private static MongoClient client;
    private static MongoDatabase database;

    static {
        client = MongoClients.create(CONNECTION_STRING);
        database = client.getDatabase(DB_NAME);
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static void close() {
        if (client != null) client.close();
    }
}
