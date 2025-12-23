// java
package ma.examen.dao;

import ma.examen.model.Eleve;
import ma.examen.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EleveDAO {

    public void add(Eleve e) throws SQLException {
        String sql = "INSERT INTO Eleve (matricule, nom, prenom, email, statut, filiere_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getMatricule());
            ps.setString(2, e.getNom());
            ps.setString(3, e.getPrenom());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getStatut());
            ps.setInt(6, e.getFiliereId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                throw new SQLException("Matricule déjà utilisé.");
            }
            throw ex;
        }
    }

    public void update(Eleve e) throws SQLException {
        String sql = "UPDATE Eleve SET matricule = ?, nom = ?, prenom = ?, email = ?, statut = ?, filiere_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getMatricule());
            ps.setString(2, e.getNom());
            ps.setString(3, e.getPrenom());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getStatut());
            ps.setInt(6, e.getFiliereId());
            ps.setInt(7, e.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("Élève introuvable pour mise à jour.");
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Eleve WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Eleve> findAll() throws SQLException {
        List<Eleve> list = new ArrayList<>();
        String sql = "SELECT * FROM Eleve";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Eleve(
                        rs.getInt("id"),
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("statut"),
                        rs.getInt("filiere_id")
                ));
            }
        }
        return list;
    }

    public Eleve findById(int id) throws SQLException {
        String sql = "SELECT * FROM Eleve WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Eleve(
                        rs.getInt("id"),
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("statut"),
                        rs.getInt("filiere_id")
                    );
                }
            }
        }
        return null;
    }

    // Récupère les ids des cours auxquels l'élève est inscrit
    public List<Integer> getCoursIdsForEleve(int eleveId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT cours_id FROM eleve_cours WHERE eleve_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("cours_id"));
                }
            }
        }
        return ids;
    }

    // Inscrire plusieurs cours transactionnellement (vérifie statut et appartenance filière)
    public void inscrireACoursMultiple(int eleveId, List<Integer> coursIds) throws SQLException {
        if (coursIds == null || coursIds.isEmpty()) return;

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // Vérifier statut et filière de l'élève
            String sqlStatut = "SELECT statut, filiere_id FROM Eleve WHERE id = ?";
            try (PreparedStatement psStatut = conn.prepareStatement(sqlStatut)) {
                psStatut.setInt(1, eleveId);
                try (ResultSet rs = psStatut.executeQuery()) {
                    if (rs.next()) {
                        if ("SUSPENDU".equalsIgnoreCase(rs.getString("statut"))) {
                            throw new SQLException("Un élève suspendu ne peut pas s'inscrire.");
                        }
                        int filiereId = rs.getInt("filiere_id");

                        // Préparer vérification et insertion
                        String sqlCheckCours = "SELECT 1 FROM filiere_cours WHERE filiere_id = ? AND cours_id = ?";
                        String sqlInsert = "INSERT INTO eleve_cours (eleve_id, cours_id) VALUES (?, ?)";
                        try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckCours);
                             PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {

                            for (Integer coursId : coursIds) {
                                // vérifier que le cours est dans la filière
                                psCheck.setInt(1, filiereId);
                                psCheck.setInt(2, coursId);
                                try (ResultSet rs2 = psCheck.executeQuery()) {
                                    if (!rs2.next()) {
                                        throw new SQLException("Cours id=" + coursId + " n'appartient pas à la filière de l'élève.");
                                    }
                                }
                                // éviter doublons
                                String sqlExist = "SELECT 1 FROM eleve_cours WHERE eleve_id = ? AND cours_id = ?";
                                try (PreparedStatement psExist = conn.prepareStatement(sqlExist)) {
                                    psExist.setInt(1, eleveId);
                                    psExist.setInt(2, coursId);
                                    try (ResultSet rs3 = psExist.executeQuery()) {
                                        if (rs3.next()) continue; // déjà inscrit -> ignorer
                                    }
                                }
                                psInsert.setInt(1, eleveId);
                                psInsert.setInt(2, coursId);
                                psInsert.executeUpdate();
                            }
                        }
                    } else {
                        throw new SQLException("Élève introuvable.");
                    }
                }
            }

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }
}