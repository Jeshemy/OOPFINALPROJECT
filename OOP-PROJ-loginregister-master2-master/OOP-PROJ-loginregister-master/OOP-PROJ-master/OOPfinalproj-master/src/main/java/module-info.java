module oop.tanregister.register {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.logging;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires javafx.base;
    requires java.sql;
    requires java.desktop;

    opens oop.tanregister.register to javafx.fxml;
    exports oop.tanregister.register;
}
