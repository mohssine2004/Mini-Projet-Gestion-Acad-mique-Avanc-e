package ma.examen.model;

public class Eleve {
    private int id; // PK [cite: 17]
    private String matricule; // UNIQUE [cite: 17, 62]
    private String nom; // [cite: 24]
    private String prenom; // [cite: 25]
    private String email; // [cite: 25]
    private String statut; // Bonus: ACTIF / SUSPENDU [cite: 76]
    private int filiereId; // FK vers Filiere [cite: 26]

    public Eleve() {}

    public Eleve(int id, String matricule, String nom, String prenom, String email, String statut, int filiereId) {
        this.id = id;
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.statut = statut;
        this.filiereId = filiereId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public int getFiliereId() { return filiereId; }
    public void setFiliereId(int filiereId) { this.filiereId = filiereId; }
}