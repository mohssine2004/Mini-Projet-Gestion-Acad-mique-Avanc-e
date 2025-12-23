package ma.examen.model;

public class Filiere {
    private int id; // PK [cite: 11]
    private String code; // UNIQUE [cite: 14]
    private String nom; // [cite: 15]
    private String description; // [cite: 16]

    public Filiere() {}

    public Filiere(int id, String code, String nom, String description) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.description = description;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return nom; // Pour l'affichage dans les ComboBox JavaFX [cite: 49]
    }
}