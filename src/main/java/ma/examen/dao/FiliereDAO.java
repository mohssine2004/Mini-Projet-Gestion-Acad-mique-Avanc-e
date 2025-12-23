// java
package ma.examen.dao;

import ma.examen.model.Filiere;
import ma.examen.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FiliereDAO {

    public void add(Filiere f) throws SQLException {
        String sql = "INSERT INTO Filiere (code, nom, description) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getCode());
            ps.setString(2, f.getNom());
            ps.setString(3, f.getDescription());
            ps.executeUpdate();
        }
    }

    public void update(Filiere f) throws SQLException {
        String sql = "UPDATE Filiere SET code = ?, nom = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getCode());
            ps.setString(2, f.getNom());
            ps.setString(3, f.getDescription());
            ps.setInt(4, f.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Mise à jour impossible : filière introuvable.");
            }
        }
    }

    public void delete(int id) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM Eleve WHERE filiere_id = ?";
        String sql = "DELETE FROM Filiere WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, id);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Impossible de supprimer : cette filière contient des élèves.");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }

    public List<Filiere> findAll() throws SQLException {
        List<Filiere> filieres = new ArrayList<>();
        String sql = "SELECT * FROM Filiere";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                filieres.add(new Filiere(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("nom"),
                        rs.getString("description")
                ));
            }
        }
        return filieres;
    }
}
