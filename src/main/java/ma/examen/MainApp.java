package ma.examen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlUrl = getClass().getResource("/ma/examen/view/MainView.fxml");
            if (fxmlUrl == null) {
                System.err.println("FXML introuvable : vérifiez que `src/main/resources/ma/examen/view/MainView.fxml` existe.");
                Platform.exit();
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);

            // Ajouter la feuille de style CSS (créer le fichier ci‑dessus)
            URL cssUrl = getClass().getResource("/ma/examen/style/application.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Feuille de style introuvable : `src/main/resources/ma/examen/style/application.css`");
            }

            primaryStage.setTitle("Système de Gestion Académique Avancée - JavaFX + JDBC");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de l'application : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}