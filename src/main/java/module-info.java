module org.example.ankiprojekt {
    requires javafx.fxml;
    requires transitive org.jsoup;
    requires static lombok;
    requires atlantafx.base;
    requires org.controlsfx.controls;
    requires java.logging;


    opens org.example.ankiprojekt to javafx.fxml;
    exports org.example.ankiprojekt;
}