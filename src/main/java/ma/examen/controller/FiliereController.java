// java
package ma.examen.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ma.examen.dao.FiliereDAO;
import ma.examen.model.Filiere;

import java.sql.SQLException;

public class FiliereController {
    @FXML private TextField txtCode, txtNom;
    @FXML private TextArea txtDescription;
    @FXML private TableView<Filiere> tableFilieres;
    @FXML private TableColumn<Filiere, String> colCode;
    @FXML private TableColumn<Filiere, String> colNom;
    @FXML private TableColumn<Filiere, String> colDescription;

    private final FiliereDAO dao = new FiliereDAO();

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableFilieres.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtCode.setText(newSel.getCode());
                txtNom.setText(newSel.getNom());
                txtDescription.setText(newSel.getDescription());
            } else {
                clearForm();
            }
        });

        try {
            refreshTable();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les filières : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleAdd() {
        try {
            Filiere f = new Filiere(0, txtCode.getText().trim(), txtNom.getText().trim(), txtDescription.getText().trim());
            dao.add(f);
            refreshTable();
            clearForm();
            showAlert("Succès", "Filière ajoutée", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleUpdate() {
        Filiere selected = tableFilieres.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Sélectionnez d'abord une filière à modifier.", Alert.AlertType.WARNING);
            return;
        }
        try {
            selected.setCode(txtCode.getText().trim());
            selected.setNom(txtNom.getText().trim());
            selected.setDescription(txtDescription.getText().trim());
            dao.update(selected);
            refreshTable();
            showAlert("Succès", "Filière mise à jour", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleDelete() {
        Filiere selected = tableFilieres.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Sélectionnez d'abord une filière à supprimer.", Alert.AlertType.WARNING);
            return;
        }
        try {
            dao.delete(selected.getId());
            refreshTable();
            clearForm();
            showAlert("Succès", "Filière supprimée", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshTable() throws SQLException {
        tableFilieres.getItems().setAll(dao.findAll());
    }

    private void clearForm() {
        txtCode.clear();
        txtNom.clear();
        txtDescription.clear();
        tableFilieres.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.show();
    }
}
