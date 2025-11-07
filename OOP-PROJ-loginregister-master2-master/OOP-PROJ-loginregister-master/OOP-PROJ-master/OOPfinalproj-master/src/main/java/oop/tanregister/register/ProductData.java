package oop.tanregister.register;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import oop.tanregister.model.Product;
import java.util.*;
import static com.mongodb.client.model.Filters.eq;

public class ProductData {
    private static final MongoDatabase database = MongoConnection.getDatabase();
    private static final MongoCollection<Document> collection = database.getCollection("products");

    public static List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document d = cursor.next();
                list.add(fromDoc(d));
            }
        }
        return list;
    }

    public static void insert(Product p) {
        Document doc = new Document("productId", p.getId())
                .append("name", p.getName())
                .append("type", p.getType())
                .append("stock", p.getStock())
                .append("price", p.getPrice())
                .append("date", new Date())
                .append("status", p.getStatus())
                .append("image", p.getImageBytes());
        collection.insertOne(doc);
    }

    public static void update(Product p) {
        Document update = new Document("$set",
                new Document("name", p.getName())
                        .append("type", p.getType())
                        .append("stock", p.getStock())
                        .append("price", p.getPrice())
                        .append("date", new Date())
                        .append("status", p.getStatus())
                        .append("image", p.getImageBytes()));
        collection.updateOne(eq("productId", p.getId()), update);
    }

    public static void delete(String id) {
        collection.deleteOne(eq("productId", id));
    }

    private static Product fromDoc(Document d) {
        Product p = new Product();
        p.setId(d.getString("productId"));
        p.setName(d.getString("name"));
        p.setType(d.getString("type"));
        p.setStock(d.getInteger("stock", 0));
        p.setPrice(d.getDouble("price"));
        p.setStatus(d.getString("status"));
        p.setDate(d.getDate("date"));

        Object imageData = d.get("image");
        if (imageData instanceof org.bson.types.Binary) {
            p.setImageBytes(((org.bson.types.Binary) imageData).getData());
        } else if (imageData instanceof byte[]) {
            p.setImageBytes((byte[]) imageData);
        } else {
            p.setImageBytes(null);
        }
        return p;
    }
}
