package ma.examen.model;

import java.time.LocalDate;

public class DossierAdministratif {
    private int id; // PK [cite: 12]
    private String numeroInscription; // UNIQUE [cite: 19]
    private LocalDate dateCreation; // [cite: 20]
    private int eleveId; // FK UNIQUE (Relation 1-1) [cite: 21, 65]

    public DossierAdministratif() {}

    public DossierAdministratif(int id, String numeroInscription, LocalDate dateCreation, int eleveId) {
        this.id = id;
        this.numeroInscription = numeroInscription;
        this.dateCreation = dateCreation;
        this.eleveId = eleveId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroInscription() { return numeroInscription; }
    public void setNumeroInscription(String numeroInscription) { this.numeroInscription = numeroInscription; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public int getEleveId() { return eleveId; }
    public void setEleveId(int eleveId) { this.eleveId = eleveId; }
}