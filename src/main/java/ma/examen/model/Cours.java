package ma.examen.model;

public class Cours {
    private int id; // PK [cite: 22]
    private String code; // UNIQUE [cite: 23, 63]
    private String intitule; // [cite: 41]

    public Cours() {}

    public Cours(int id, String code, String intitule) {
        this.id = id;
        this.code = code;
        this.intitule = intitule;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }

    @Override
    public String toString() {
        return intitule;
    }
}