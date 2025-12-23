// java
package ma.examen.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ma.examen.dao.CoursDAO;
import ma.examen.dao.DossierDAO;
import ma.examen.dao.EleveDAO;
import ma.examen.dao.FiliereDAO;
import ma.examen.model.Cours;
import ma.examen.model.DossierAdministratif;
import ma.examen.model.Eleve;
import ma.examen.model.Filiere;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class EleveController {

    @FXML private TextField txtMatricule, txtNom, txtPrenom, txtEmail;
    @FXML private ComboBox<Filiere> comboFiliere;
    @FXML private ComboBox<String> comboStatut;

    @FXML private TableView<Eleve> tableEleves;
    @FXML private TableColumn<Eleve, String> colMatricule, colNom, colPrenom, colEmail, colStatut;
    @FXML private TableColumn<Eleve, Integer> colFiliere;

    @FXML private ListView<Cours> listViewCourses;
    @FXML private Button btnEnroll;

    // Dossier administratif controls
    @FXML private TextField txtNumeroInscription;
    @FXML private DatePicker dateCreation;
    @FXML private Button btnCreateDossier, btnUpdateDossier;

    private final EleveDAO eleveDao = new EleveDAO();
    private final FiliereDAO filiereDao = new FiliereDAO();
    private final CoursDAO coursDao = new CoursDAO();
    private final DossierDAO dossierDao = new DossierDAO();

    private final ObservableList<Eleve> eleves = FXCollections.observableArrayList();
    private final ObservableList<Filiere> filieres = FXCollections.observableArrayList();
    private final ObservableList<Cours> coursList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Statut
        comboStatut.getItems().addAll("ACTIF", "SUSPENDU");

        // Table columns
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colFiliere.setCellValueFactory(new PropertyValueFactory<>("filiereId"));

        tableEleves.setItems(eleves);

        tableEleves.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateForm(newSel);
            } else {
                clearForm();
            }
        });

        // ListView multi-selection for courses
        listViewCourses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Charger données initiales
        try {
            refreshFilieres();
            refreshCours();
            refreshTable();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les données : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshFilieres() throws SQLException {
        filieres.setAll(filiereDao.findAll());
        comboFiliere.getItems().setAll(filieres);
    }

    private void refreshCours() throws SQLException {
        coursList.setAll(coursDao.findAll());
        listViewCourses.setItems(coursList);
    }

    private void refreshTable() throws SQLException {
        eleves.setAll(eleveDao.findAll());
    }

    private void populateForm(Eleve e) {
        txtMatricule.setText(e.getMatricule());
        txtNom.setText(e.getNom());
        txtPrenom.setText(e.getPrenom());
        txtEmail.setText(e.getEmail());
        comboStatut.setValue(e.getStatut());
        // sélectionner la filière (objet)
        filieres.stream().filter(f -> f.getId() == e.getFiliereId()).findFirst().ifPresent(comboFiliere::setValue);

        // sélectionner les cours déjà inscrits
        try {
            List<Integer> enrolledIds = eleveDao.getCoursIdsForEleve(e.getId());
            listViewCourses.getSelectionModel().clearSelection();
            for (int i = 0; i < coursList.size(); i++) {
                if (enrolledIds.contains(coursList.get(i).getId())) {
                    listViewCourses.getSelectionModel().select(i);
                }
            }
        } catch (SQLException ex) {
            showAlert("Erreur", "Impossible de charger inscriptions : " + ex.getMessage(), Alert.AlertType.ERROR);
        }

        // charger dossier administratif si présent
        try {
            DossierAdministratif d = dossierDao.findByEleveId(e.getId());
            if (d != null) {
                txtNumeroInscription.setText(d.getNumeroInscription());
                dateCreation.setValue(d.getDateCreation());
                btnCreateDossier.setDisable(true);
                btnUpdateDossier.setDisable(false);
            } else {
                txtNumeroInscription.clear();
                dateCreation.setValue(null);
                btnCreateDossier.setDisable(false);
                btnUpdateDossier.setDisable(true);
            }
        } catch (SQLException ex) {
            showAlert("Erreur", "Impossible de charger le dossier administratif : " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clearForm() {
        txtMatricule.clear();
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        comboFiliere.getSelectionModel().clearSelection();
        comboStatut.getSelectionModel().clearSelection();
        listViewCourses.getSelectionModel().clearSelection();
        txtNumeroInscription.clear();
        dateCreation.setValue(null);
        btnCreateDossier.setDisable(true);
        btnUpdateDossier.setDisable(true);
        tableEleves.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleSave() {
        try {
            Eleve e = new Eleve();
            e.setMatricule(txtMatricule.getText().trim());
            e.setNom(txtNom.getText().trim());
            e.setPrenom(txtPrenom.getText().trim());
            e.setEmail(txtEmail.getText().trim());
            e.setStatut(comboStatut.getValue());
            Filiere selectedF = comboFiliere.getValue();
            if (selectedF == null) {
                showAlert("Attention", "Sélectionnez une filière.", Alert.AlertType.WARNING);
                return;
            }
            e.setFiliereId(selectedF.getId());

            eleveDao.add(e);
            refreshTable();
            clearForm();
            showAlert("Succès", "Élève ajouté.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Erreur", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleUpdate() {
        Eleve selected = tableEleves.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Sélectionnez un élève à modifier.", Alert.AlertType.WARNING);
            return;
        }
        try {
            selected.setMatricule(txtMatricule.getText().trim());
            selected.setNom(txtNom.getText().trim());
            selected.setPrenom(txtPrenom.getText().trim());
            selected.setEmail(txtEmail.getText().trim());
            selected.setStatut(comboStatut.getValue());
            Filiere selF = comboFiliere.getValue();
            if (selF != null) selected.setFiliereId(selF.getId());

            eleveDao.update(selected);
            refreshTable();
            showAlert("Succès", "Élève mis à jour.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Erreur", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleDelete() {
        Eleve selected = tableEleves.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Sélectionnez un élève à supprimer.", Alert.AlertType.WARNING);
            return;
        }
        try {
            eleveDao.delete(selected.getId());
            refreshTable();
            clearForm();
            showAlert("Succès", "Élève supprimé.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Erreur", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleEnrollSelectedCourses() {
        Eleve selected = tableEleves.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Sélectionnez d'abord un élève.", Alert.AlertType.WARNING);
            return;
        }
        List<Cours> selectedCourses = listViewCourses.getSelectionModel().getSelectedItems();
        List<Integer> ids = selectedCourses.stream().map(Cours::getId).collect(Collectors.toList());
        try {
            eleveDao.inscrireACoursMultiple(selected.getId(), ids);
            showAlert("Succès", "Inscriptions mises à jour.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Erreur", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleCreateDossier() {
        Eleve selected = tableEleves.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Sélectionnez un élève pour créer un dossier.", Alert.AlertType.WARNING);
            return;
        }
        try {
            String num = txtNumeroInscription.getText().trim();
            LocalDate date = dateCreation.getValue();
            if (num.isEmpty() || date == null) {
                showAlert("Attention", "Renseignez numéro et date.", Alert.AlertType.WARNING);
                return;
            }
            DossierAdministratif d = new DossierAdministratif(0, num, date, selected.getId());
            dossierDao.create(d);
            populateForm(selected); // recharge état du dossier
            showAlert("Succès", "Dossier créé.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Erreur", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleUpdateDossier() {
        Eleve selected = tableEleves.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Sélectionnez un élève.", Alert.AlertType.WARNING);
            return;
        }
        try {
            DossierAdministratif d = dossierDao.findByEleveId(selected.getId());
            if (d == null) {
                showAlert("Attention", "Aucun dossier existant pour cet élève.", Alert.AlertType.WARNING);
                return;
            }
            String num = txtNumeroInscription.getText().trim();
            LocalDate date = dateCreation.getValue();
            if (num.isEmpty() || date == null) {
                showAlert("Attention", "Renseignez numéro et date.", Alert.AlertType.WARNING);
                return;
            }
            d.setNumeroInscription(num);
            d.setDateCreation(date);
            dossierDao.update(d);
            showAlert("Succès", "Dossier mis à jour.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Erreur", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.show();
    }
}