package oop.tanregister.register;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import oop.tanregister.model.Product;
import org.bson.Document;
import org.bson.types.Binary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class AdminMenuController implements Initializable {

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> productID;
    @FXML
    private TableColumn<Product, String> productName;
    @FXML
    private TableColumn<Product, String> productType;
    @FXML
    private TableColumn<Product, Integer> ProductStock;
    @FXML
    private TableColumn<Product, Double> ProductPrice;
    @FXML
    private TableColumn<Product, String> ProductStatus;  // Fixed: Changed from Integer to String to match the data type
    @FXML
    private TableColumn<Product, Date> ProductDate;

    @FXML
    private TextField IDField;
    @FXML
    private TextField ProductNameField;
    @FXML
    private ComboBox<String> TypeField;
    @FXML
    private TextField StockField;
    @FXML
    private TextField Price;
    @FXML
    private ComboBox<String> StatusField;
    @FXML
    private Button mainButton;
    @FXML
    private Button inventoryButton;
    @FXML
    private Button customerButton;
    @FXML
    private Button UpdateButton;
    @FXML
    private Button ClearButton;
    @FXML
    private Button DeleteButton;
    @FXML
    private Button importButton;
    @FXML
    private Button SignOut;
    @FXML
    private ImageView productImageView;

    private byte[] currentImageBytes;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TypeField.getItems().addAll("Mockups", "Mugs", "Books");
        StatusField.getItems().addAll("Completed", "Delivered", "To Pack");

        productID.setCellValueFactory(new PropertyValueFactory<>("id"));
        productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        productType.setCellValueFactory(new PropertyValueFactory<>("type"));
        ProductStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        ProductPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        ProductDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        ProductStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadProducts();

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                IDField.setText(newSelection.getId());
                ProductNameField.setText(newSelection.getName());
                TypeField.setValue(newSelection.getType());
                StockField.setText(String.valueOf(newSelection.getStock()));
                Price.setText(String.valueOf(newSelection.getPrice()));
                StatusField.setValue(newSelection.getStatus());
                currentImageBytes = newSelection.getImageBytes();

                if (currentImageBytes != null && currentImageBytes.length > 0) {
                    productImageView.setImage(new Image(new ByteArrayInputStream(currentImageBytes)));
                } else {
                    productImageView.setImage(null);
                }
            }
        });
    }

    // ✅ Load products directly from MongoDB
    private void loadProducts() {
        MongoDatabase db = MongoConnection.getDatabase();
        MongoCollection<Document> collection = db.getCollection("products");
        ObservableList<Product> productList = FXCollections.observableArrayList();

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Product p = new Product();

                // ✅ Use custom productID, not MongoDB _id
                p.setId(doc.getString("productID"));
                p.setName(doc.getString("name"));
                p.setType(doc.getString("type"));
                p.setStock(doc.getInteger("stock", 0));
                p.setPrice(doc.getDouble("price"));
                p.setStatus(doc.getString("status"));
                p.setDate(doc.getDate("date"));

                Binary img = doc.get("image", Binary.class);
                if (img != null) p.setImageBytes(img.getData());

                productList.add(p);
            }
        }
        productTable.setItems(productList);
    }

    // ✅ Insert new product to MongoDB
    @FXML
    private void handleAdd(ActionEvent event) {
        String id = IDField.getText().trim();
        String name = ProductNameField.getText().trim();
        String type = (TypeField.getValue() != null) ? TypeField.getValue() : "";
        String stockText = StockField.getText().trim();
        String priceText = Price.getText().trim();
        String status = (StatusField.getValue() != null) ? StatusField.getValue() : "";

        if (id.isEmpty() || name.isEmpty() || type.isEmpty() || stockText.isEmpty() || priceText.isEmpty() || status.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill in all fields including Product ID.");
            return;
        }

        try {
            int stock = Integer.parseInt(stockText);
            double price = Double.parseDouble(priceText);

            MongoDatabase db = MongoConnection.getDatabase();
            MongoCollection<Document> collection = db.getCollection("products");

            // ✅ Check if productID already exists
            Document existing = collection.find(eq("productID", id)).first();
            if (existing != null) {
                showAlert(Alert.AlertType.ERROR, "Duplicate ID", "A product with this ID already exists.");
                return;
            }

            Document doc = new Document("productID", id)
                    .append("name", name)
                    .append("type", type)
                    .append("stock", stock)
                    .append("price", price)
                    .append("status", status)
                    .append("date", new Date())
                    .append("image", currentImageBytes != null ? new Binary(currentImageBytes) : null);

            collection.insertOne(doc);

            loadProducts();
            clearForm();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully!");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Stock and Price must be numbers.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    // ✅ Update product
    @FXML
    private void handleUpdate(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a product to update.");
            return;
        }

        String newId = IDField.getText().trim();
        if (newId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing ID", "Product ID cannot be empty.");
            return;
        }

        try {
            MongoDatabase db = MongoConnection.getDatabase();
            MongoCollection<Document> collection = db.getCollection("products");

            // Check if the new ID is different and already exists
            if (!newId.equals(selected.getId())) {
                Document existing = collection.find(eq("productID", newId)).first();
                if (existing != null) {
                    showAlert(Alert.AlertType.ERROR, "Duplicate ID", "A product with this ID already exists.");
                    return;
                }
            }

            Document update = new Document("productID", newId)
                    .append("name", ProductNameField.getText())
                    .append("type", TypeField.getValue())
                    .append("stock", Integer.parseInt(StockField.getText()))
                    .append("price", Double.parseDouble(Price.getText()))
                    .append("status", StatusField.getValue())
                    .append("date", new Date())
                    .append("image", currentImageBytes != null ? new Binary(currentImageBytes) : null);

            collection.updateOne(eq("productID", selected.getId()), new Document("$set", update));

            loadProducts();
            showAlert(Alert.AlertType.INFORMATION, "Updated", "Product updated successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Update Error", e.getMessage());
        }
    }

    // ✅ Delete product
    @FXML
    public void handleDelete(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a product to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete product " + selected.getId() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            MongoDatabase db = MongoConnection.getDatabase();
            MongoCollection<Document> collection = db.getCollection("products");
            collection.deleteOne(eq("productID", selected.getId()));
            loadProducts();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Product deleted successfully!");
        }
    }

    @FXML
    public void handleImport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] imageBytes = fis.readAllBytes();
                fis.close();

                currentImageBytes = imageBytes;
                productImageView.setImage(new Image(file.toURI().toString()));

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "File Error", "Could not load image.");
            }
        }
    }

    @FXML
    public void handleClear(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        IDField.clear();
        ProductNameField.clear();
        TypeField.getSelectionModel().clearSelection();
        StockField.clear();
        Price.clear();
        StatusField.getSelectionModel().clearSelection();
        productImageView.setImage(null);
        currentImageBytes = null;
    }

    @FXML
    public void handleSignOut(MouseEvent event) throws Exception {
        Stage currentStage = (Stage) SignOut.getScene().getWindow();
        currentStage.close();
        Login loginApp = new Login();
        Stage stage = new Stage();
        loginApp.start(stage);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void switchCustomer(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/oop/tanregister/register/adcustomer.fxml"));
        Parent root = loader.load();

        CustomerController customerController = loader.getController();
        customerController.loadProductShowcase(); // refresh

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1100, 600);
        stage.setScene(scene);
        stage.setTitle("Customer Menu");
        stage.show();
    }

    public void switchInventory(MouseEvent event) throws IOException {
        try {
            Stage currentStage = (Stage) mainButton.getScene().getWindow();
            currentStage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/oop/tanregister/register/adminmenu.fxml"));
            Parent root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not return to Register page:\n" + e.getMessage());
        }
    }

    @FXML
    private void switchMenuView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/oop/tanregister/register/admainview.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 600));
            stage.setTitle("Admin Menu");
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not return to Main Menu:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshProductsFromCustomer() {
        loadProducts();
    }
}
