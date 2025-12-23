module org.example.examen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Ouvrir les packages contenant les contrôleurs utilisés par FXMLLoader
    opens ma.examen to javafx.fxml;
    opens ma.examen.controller to javafx.fxml;

    exports ma.examen;
}
