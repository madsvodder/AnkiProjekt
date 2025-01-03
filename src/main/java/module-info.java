module org.example.ankiprojekt {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.ankiprojekt to javafx.fxml;
    exports org.example.ankiprojekt;
}