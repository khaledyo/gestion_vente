package dao;

import model.Livraison;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import application.ConnectToDB;

public class LivraisonDAO {
    private Connection connection;

    public LivraisonDAO() throws SQLException, ClassNotFoundException {
        this.connection = ConnectToDB.getConnection();
        if (this.connection == null) {
            throw new SQLException("La connexion à la base de données a échoué");
        }
    }

    // Ajouter une nouvelle livraison
    public void ajouterLivraison(Livraison livraison) throws SQLException {
        String query = "INSERT INTO Livraison (numeroCommande, dateLivraison, statut, adresseLivraison, transporteur) " +
                      "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, livraison.getNumeroCommande());
            stmt.setDate(2, Date.valueOf(livraison.getDateLivraison()));
            stmt.setString(3, livraison.getStatut());
            stmt.setString(4, livraison.getAdresseLivraison());
            stmt.setString(5, livraison.getTransporteur());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("L'ajout de la livraison a échoué, aucune ligne affectée");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    livraison.setNumeroLivraison(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("L'ajout de la livraison a échoué, aucun ID généré");
                }
            }
        }
    }
public int getNombreLivraisons() throws SQLException {
    String query = "SELECT COUNT(*) AS total FROM Livraison";
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        if (rs.next()) {
            return rs.getInt("total");
        }
        return 0;
    }
}
    // Récupérer toutes les livraisons
    public List<Livraison> getAllLivraisons() throws SQLException {
        List<Livraison> livraisons = new ArrayList<>();
        String query = "SELECT * FROM Livraison ORDER BY dateLivraison DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Livraison livraison = mapResultSetToLivraison(rs);
                livraisons.add(livraison);
            }
        }
        return livraisons;
    }

    // Mettre à jour une livraison
    public void updateLivraison(Livraison livraison) throws SQLException {
        String query = "UPDATE Livraison SET numeroCommande = ?, dateLivraison = ?, statut = ?, " +
                      "adresseLivraison = ?, transporteur = ? WHERE numeroLivraison = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, livraison.getNumeroCommande());
            stmt.setDate(2, Date.valueOf(livraison.getDateLivraison()));
            stmt.setString(3, livraison.getStatut());
            stmt.setString(4, livraison.getAdresseLivraison());
            stmt.setString(5, livraison.getTransporteur());
            stmt.setInt(6, livraison.getNumeroLivraison());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour de la livraison a échoué, aucune ligne affectée");
            }
        }
    }

    // Supprimer une livraison
    public void deleteLivraison(int numeroLivraison) throws SQLException {
        String query = "DELETE FROM Livraison WHERE numeroLivraison = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, numeroLivraison);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La suppression de la livraison a échoué, aucune ligne affectée");
            }
        }
    }

    // Récupérer une livraison par son ID
    public Livraison getLivraisonById(int numeroLivraison) throws SQLException {
        String query = "SELECT * FROM Livraison WHERE numeroLivraison = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, numeroLivraison);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLivraison(rs);
                }
            }
        }
        return null;
    }

    // Récupérer les livraisons par statut
    public List<Livraison> getLivraisonsByStatut(String statut) throws SQLException {
        List<Livraison> livraisons = new ArrayList<>();
        String query = "SELECT * FROM Livraison WHERE statut = ? ORDER BY dateLivraison DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, statut);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    livraisons.add(mapResultSetToLivraison(rs));
                }
            }
        }
        return livraisons;
    }

    // Récupérer les livraisons par commande
    public List<Livraison> getLivraisonsByCommande(int numeroCommande) throws SQLException {
        List<Livraison> livraisons = new ArrayList<>();
        String query = "SELECT * FROM Livraison WHERE numeroCommande = ? ORDER BY dateLivraison DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, numeroCommande);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    livraisons.add(mapResultSetToLivraison(rs));
                }
            }
        }
        return livraisons;
    }

    // Méthode utilitaire pour mapper un ResultSet à un objet Livraison
    private Livraison mapResultSetToLivraison(ResultSet rs) throws SQLException {
        Livraison livraison = new Livraison();
        livraison.setNumeroLivraison(rs.getInt("numeroLivraison"));
        livraison.setNumeroCommande(rs.getInt("numeroCommande"));
        livraison.setDateLivraison(rs.getDate("dateLivraison").toLocalDate());
        livraison.setStatut(rs.getString("statut"));
        livraison.setAdresseLivraison(rs.getString("adresseLivraison"));
        livraison.setTransporteur(rs.getString("transporteur"));
        return livraison;
    }

    // Fermer la connexion
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }
    public int getNombreLivraisonsParStatut(String statut) throws SQLException {
    String query = "SELECT COUNT(*) AS total FROM Livraison WHERE statut = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setString(1, statut);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }
}
}