module org.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.fasterxml.jackson.databind;

    opens org.example.frontend to javafx.fxml;
    exports org.example.frontend;
}