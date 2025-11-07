package oop.tanregister.register;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button exitButton;

    @FXML
    private void handleLogin() throws Exception {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Missing Information", "Please fill in both fields.");
            return;
        }

        if (email.equals("admin@gmail.com") && password.equals("1234")) {
            showAlert(AlertType.INFORMATION, "Login Successful", "Welcome back, Admin!");

            try {
                Stage currentStage = (Stage) loginButton.getScene().getWindow();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/oop/tanregister/register/AdminMenu.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root, 1100, 600);
                currentStage.setTitle("Admin Menu");
                currentStage.setScene(scene);
                currentStage.show();

            } catch (IOException e) {
                showAlert(AlertType.ERROR, "Error", "Could not open Admin Menu:\n" + e.getMessage());
            }

            return;
        }

        if (oop.tanregister.db.UserData.validateLogin(email, password)) {
            showAlert(AlertType.INFORMATION, "Login Successful", "Welcome back!");

            try {
                Stage currentStage = (Stage) loginButton.getScene().getWindow();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/oop/tanregister/register/Customer.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root, 1100, 600);
                currentStage.setTitle("Main Menu");
                currentStage.setScene(scene);
                currentStage.show();

            } catch (IOException e) {
                showAlert(AlertType.ERROR, "Error", "Could not open Customer Menu:\n" + e.getMessage());
            }
        }
        else {
            showAlert(AlertType.ERROR, "Login Failed", "Invalid email or password.");
        }
    }

    @FXML
    private void handleExit() {
        try {
            Stage currentStage = (Stage) exitButton.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/oop/tanregister/register/register.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setTitle("Register");
            registerStage.setScene(new Scene(root, 800, 500));
            registerStage.show();

        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Could not return to Register page:\n" + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
