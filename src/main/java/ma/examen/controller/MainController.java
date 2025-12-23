// java
package ma.examen.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
    @FXML private StackPane contentArea;

    @FXML
    public void showFiliereModule() {
        loadView("/ma/examen/view/FiliereView.fxml");
    }

    @FXML
    public void showEleveModule() {
        loadView("/ma/examen/view/EleveView.fxml");
    }

    @FXML
    public void showCoursModule() {
        loadView("/ma/examen/view/CoursView.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger la vue : " + fxmlPath + "\n" + e.getMessage());
            alert.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur inattendue");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du chargement de la vue : " + ex.getMessage());
            alert.showAndWait();
        }
    }
}