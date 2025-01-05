module org.example.ankiprojekt {
    requires javafx.fxml;
    requires java.desktop;
    requires transitive org.jsoup;
    requires static lombok;
    requires atlantafx.base;


    opens org.example.ankiprojekt to javafx.fxml;
    exports org.example.ankiprojekt;
}