module org.example.ankiprojekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires transitive org.jsoup;
    requires static lombok;


    opens org.example.ankiprojekt to javafx.fxml;
    exports org.example.ankiprojekt;
}