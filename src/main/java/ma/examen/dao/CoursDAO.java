// java
package ma.examen.dao;

import ma.examen.model.Cours;
import ma.examen.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursDAO {

    public void add(Cours c) throws SQLException {
        String sql = "INSERT INTO Cours (code, intitule) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setString(2, c.getIntitule());
            ps.executeUpdate();
        }
    }

    public void update(Cours c) throws SQLException {
        String sql = "UPDATE Cours SET code = ?, intitule = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setString(2, c.getIntitule());
            ps.setInt(3, c.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Mise à jour impossible : cours introuvable.");
            }
        }
    }

    public void delete(int id) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM filiere_cours WHERE cours_id = ?";
        String sql = "DELETE FROM Cours WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, id);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Impossible de supprimer : ce cours est affecté à une ou plusieurs filières.");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }

    public List<Cours> findAll() throws SQLException {
        List<Cours> list = new ArrayList<>();
        String sql = "SELECT * FROM Cours";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Cours(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("intitule")
                ));
            }
        }
        return list;
    }

    public Cours findById(int id) throws SQLException {
        String sql = "SELECT * FROM Cours WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cours(rs.getInt("id"), rs.getString("code"), rs.getString("intitule"));
                }
            }
        }
        return null;
    }

    public void affecterAFiliere(int coursId, int filiereId) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM filiere_cours WHERE filiere_id = ? AND cours_id = ?";
        String insertSql = "INSERT INTO filiere_cours (filiere_id, cours_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, filiereId);
            psCheck.setInt(2, coursId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Ce cours est déjà affecté à cette filière.");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, filiereId);
                ps.setInt(2, coursId);
                ps.executeUpdate();
            }
        }
    }
}