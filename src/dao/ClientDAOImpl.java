package dao;

import model.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAOImpl implements ClientDAO {

    
    public void ajouterClient(Connection conn, Client client) throws SQLException {
    //  si le tel existe deja
    String checkSql = "SELECT COUNT(*) FROM client WHERE telephone = ?";
    try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
        checkPs.setString(1, client.getTelephone());
        try (ResultSet rs = checkPs.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Le numéro de téléphone existe déjà.");
            }
        }
    }

    // Ajouter le client
    String sql = "INSERT INTO client (nom, prenom, adresse, telephone) VALUES (?, ?, ?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, client.getNom());
        ps.setString(2, client.getPrenom());
        ps.setString(3, client.getAdresse());
        ps.setString(4, client.getTelephone());
        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                client.setId(rs.getInt(1));
            }
        }
    }
}
    public int getNombreClients(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM client";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    
    public List<Client> rechercherClients(Connection conn, String category, String searchText) throws SQLException {
    List<Client> clients = new ArrayList<>();
    String sql = "SELECT * FROM client WHERE LOWER(TRIM(" + category + ")) LIKE LOWER(TRIM(?))"; // Ignore casse + espaces
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "%" + searchText + "%");
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Client client = new Client(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("adresse"),
                    rs.getString("telephone")
                );
                client.setId(rs.getInt("numeroclient")); // Attention faute de frappe ici ("numeroclient" vs "numeroclient")
                clients.add(client);
            }
        }
    }
    return clients;
}

    public void updateClient(Connection conn, Client client) throws SQLException {
    // si le tel existe deja
    String checkSql = "SELECT COUNT(*) FROM client WHERE telephone = ? AND numeroclient != ?";
    try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
        checkPs.setString(1, client.getTelephone());
        checkPs.setInt(2, client.getId());
        try (ResultSet rs = checkPs.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Le numéro de téléphone existe déjà pour un autre client.");
            }
        }
    }

    // mis a jour 
    String sql = "UPDATE client SET nom = ?, prenom = ?, adresse = ?, telephone = ? WHERE numeroclient = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, client.getNom());
        ps.setString(2, client.getPrenom());
        ps.setString(3, client.getAdresse());
        ps.setString(4, client.getTelephone());
        ps.setInt(5, client.getId());
        ps.executeUpdate();
    }
}

    public void supprimerClient(Connection conn, String nom, String prenom) throws SQLException {
        String sql = "DELETE FROM client WHERE nom = ? AND prenom = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nom);
        ps.setString(2, prenom);
        ps.executeUpdate();
    }
    public List<Client> getAllClients(Connection conn) throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM client";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Client client = new Client(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("adresse"),
                    rs.getString("telephone")
                );
                client.setId(rs.getInt("numeroclient"));
                clients.add(client);
            }
        }
        return clients;
    }

}
