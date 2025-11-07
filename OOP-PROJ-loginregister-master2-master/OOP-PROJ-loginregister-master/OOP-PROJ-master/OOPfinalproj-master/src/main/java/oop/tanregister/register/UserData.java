package oop.tanregister.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import oop.tanregister.register.MongoConnection;
import org.bson.Document;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

public class UserData {
    private static final MongoDatabase database = MongoConnection.getDatabase();
    private static final MongoCollection<Document> users = database.getCollection("users");

    public static boolean emailExists(String email) {
        return users.find(eq("email", email)).first() != null;
    }

    public static void addUser(String firstName, String lastName, String email,
                               String phone, String address, String password,
                               LocalDateTime createdAt, LocalDateTime updatedAt) throws Exception {

        String passwordHash = hashPassword(password);

        Document doc = new Document("first_name", firstName)
                .append("last_name", lastName)
                .append("email", email)
                .append("phone", phone)
                .append("address", address)
                .append("password_hash", passwordHash)
                .append("role", "customer")
                .append("created_At", Date.from(createdAt.toInstant(ZoneOffset.UTC)))
                .append("updated_At", Date.from(updatedAt.toInstant(ZoneOffset.UTC)));

        users.insertOne(doc);
    }

    public static boolean validateLogin(String email, String password) throws Exception {
        String passwordHash = hashPassword(password);
        Document user = users.find(
                new Document("email", email).append("password_hash", passwordHash)
        ).first();
        return user != null;
    }

    private static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
