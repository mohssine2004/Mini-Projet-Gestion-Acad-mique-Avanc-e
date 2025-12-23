// java
package ma.examen.dao;

import ma.examen.model.DossierAdministratif;
import ma.examen.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class DossierDAO {

    public void create(DossierAdministratif d) throws SQLException {
        // Vérifier s'il existe déjà un dossier pour l'élève
        String check = "SELECT COUNT(*) FROM DossierAdministratif WHERE eleve_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(check)) {
            psCheck.setInt(1, d.getEleveId());
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Cet élève possède déjà un dossier administratif.");
                }
            }
        }

        String sql = "INSERT INTO DossierAdministratif (numero_inscription, date_creation, eleve_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getNumeroInscription());
            ps.setDate(2, Date.valueOf(d.getDateCreation()));
            ps.setInt(3, d.getEleveId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) d.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException("Cet élève possède déjà un dossier administratif.");
            }
            throw e;
        }
    }

    public void update(DossierAdministratif d) throws SQLException {
        String sql = "UPDATE DossierAdministratif SET numero_inscription = ?, date_creation = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getNumeroInscription());
            ps.setDate(2, Date.valueOf(d.getDateCreation()));
            ps.setInt(3, d.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("Dossier administratif introuvable pour mise à jour.");
        }
    }

    public DossierAdministratif findByEleveId(int eleveId) throws SQLException {
        String sql = "SELECT * FROM DossierAdministratif WHERE eleve_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DossierAdministratif(
                            rs.getInt("id"),
                            rs.getString("numero_inscription"),
                            rs.getDate("date_creation").toLocalDate(),
                            rs.getInt("eleve_id")
                    );
                }
            }
        }
        return null;
    }
}