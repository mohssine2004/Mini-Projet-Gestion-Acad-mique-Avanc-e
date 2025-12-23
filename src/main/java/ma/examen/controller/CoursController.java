// java
package ma.examen.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ma.examen.dao.CoursDAO;
import ma.examen.dao.FiliereDAO;
import ma.examen.model.Cours;
import ma.examen.model.Filiere;

import java.sql.SQLException;
import java.util.List;

public class CoursController {
    @FXML private TextField txtCode, txtIntitule;
    @FXML private TableView<Cours> tableCours;
    @FXML private TableColumn<Cours, String> colCode;
    @FXML private TableColumn<Cours, String> colIntitule;
    @FXML private ComboBox<Filiere> comboFilieres;
    @FXML private Button btnAffecter;

    private final CoursDAO coursDao = new CoursDAO();
    private final FiliereDAO filiereDao = new FiliereDAO();

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colIntitule.setCellValueFactory(new PropertyValueFactory<>("intitule"));

        tableCours.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtCode.setText(newSel.getCode());
                txtIntitule.setText(newSel.getIntitule());
            } else {
                clearForm();
            }
        });

        try {
            refreshTable();
            loadFilieres();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les données : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadFilieres() throws SQLException {
        List<Filiere> filieres = filiereDao.findAll();
        comboFilieres.getItems().setAll(filieres);
    }

    @FXML
    public void handleAdd() {
        try {
            Cours c = new Cours(0, txtCode.getText().trim(), txtIntitule.getText().trim());
            coursDao.add(c);
            refreshTable();
            clearForm();
            showAlert("Succès", "Cours ajouté", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleUpdate() {
        Cours selected = tableCours.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Sélectionnez un cours à modifier.", Alert.AlertType.WARNING);
            return;
        }
        try {
            selected.setCode(txtCode.getText().trim());
            selected.setIntitule(txtIntitule.getText().trim());
            coursDao.update(selected);
            refreshTable();
            showAlert("Succès", "Cours mis à jour", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleDelete() {
        Cours selected = tableCours.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Sélectionnez un cours à supprimer.", Alert.AlertType.WARNING);
            return;
        }
        try {
            coursDao.delete(selected.getId());
            refreshTable();
            clearForm();
            showAlert("Succès", "Cours supprimé", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleAffecter() {
        Cours selected = tableCours.getSelectionModel().getSelectedItem();
        Filiere f = comboFilieres.getValue();
        if (selected == null) {
            showAlert("Attention", "Sélectionnez d'abord un cours.", Alert.AlertType.WARNING);
            return;
        }
        if (f == null) {
            showAlert("Attention", "Sélectionnez une filière pour l'affectation.", Alert.AlertType.WARNING);
            return;
        }
        try {
            coursDao.affecterAFiliere(selected.getId(), f.getId());
            showAlert("Succès", "Cours affecté à la filière \"" + f.getNom() + "\"", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshTable() throws SQLException {
        tableCours.getItems().setAll(coursDao.findAll());
    }

    private void clearForm() {
        txtCode.clear();
        txtIntitule.clear();
        tableCours.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.show();
    }
}