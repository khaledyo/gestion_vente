package dao;

import model.Commande;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAOImpl implements CommandeDAO {

    @Override
    public void ajouterCommande(Connection conn, Commande commande) throws SQLException {
        String sql = "INSERT INTO Commande (numeroclient, datecommande) VALUES (?, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, commande.getNumeroclient());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    commande.setNumerocommande(rs.getInt(1));
                    
                    // Get the auto-generated date 
                    try (PreparedStatement dateStmt = conn.prepareStatement(
                            "SELECT datecommande FROM Commande WHERE numerocommande = ?")) {
                        dateStmt.setInt(1, commande.getNumerocommande());
                        try (ResultSet dateRs = dateStmt.executeQuery()) {
                            if (dateRs.next()) {
                                Timestamp timestamp = dateRs.getTimestamp("datecommande");
                                if (timestamp != null) {
                                    commande.setDatecommande(timestamp.toLocalDateTime().toLocalDate());
                                } else {
                                    // Fallback to current date if null
                                    commande.setDatecommande(LocalDate.now());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void modifierCommande(Connection conn, Commande commande) throws SQLException {
        String sql = "UPDATE Commande SET numeroclient = ?, datecommande = ? WHERE numerocommande = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commande.getNumeroclient());
            ps.setDate(2, Date.valueOf(commande.getDatecommande()));
            ps.setInt(3, commande.getNumerocommande());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de la modification, aucune commande trouvée avec ce numéro");
            }
        }
    }

    @Override
    public void supprimerCommande(Connection conn, int numeroCommande) throws SQLException {
        // D'abord supprimer les détails associés
        String sqlDeleteDetails = "DELETE FROM DetailCommande WHERE numerocommande = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteDetails)) {
            ps.setInt(1, numeroCommande);
            ps.executeUpdate();
        }
        
        // Puis supprimer la commande
        String sql = "DELETE FROM Commande WHERE numerocommande = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numeroCommande);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de la suppression, aucune commande trouvée avec ce numéro");
            }
        }
    }

    @Override
    public Commande getCommandeById(Connection conn, int numeroCommande) throws SQLException {
        String sql = "SELECT * FROM Commande WHERE numerocommande = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numeroCommande);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Commande(
                        rs.getInt("numerocommande"),
                        rs.getDate("datecommande").toLocalDate(),
                        rs.getInt("numeroclient")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Commande> getAllCommandes(Connection conn) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("datecommande");
                LocalDate date = (timestamp != null) ? timestamp.toLocalDateTime().toLocalDate() : null;
                
                Commande commande = new Commande(
                    rs.getInt("numerocommande"),
                    date,
                    rs.getInt("numeroclient")
                );
                commandes.add(commande);
            }
        }
        return commandes;
    }

    @Override
    public List<Commande> getCommandesByClient(Connection conn, int clientId) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande WHERE numeroclient = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("datecommande");
                    LocalDate date = (timestamp != null) ? timestamp.toLocalDateTime().toLocalDate() : null;
                    
                    Commande commande = new Commande(
                        rs.getInt("numerocommande"),
                        date,
                        rs.getInt("numeroclient")
                    );
                    commandes.add(commande);
                }
            }
        }
        return commandes;
    }

    @Override
    public int getNombreCommandes(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Commande";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}