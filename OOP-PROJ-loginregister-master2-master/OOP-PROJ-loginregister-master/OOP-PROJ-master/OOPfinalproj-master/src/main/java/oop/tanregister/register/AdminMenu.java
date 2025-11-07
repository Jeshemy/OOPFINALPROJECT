package oop.tanregister.register;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminMenu extends Application  {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AdminMenu.class.getResource("Main-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000,600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}