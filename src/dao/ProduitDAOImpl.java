package dao;

import model.Produit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAOImpl implements ProduitDAO {

    private Connection conn;

    // Constructeur avec connexion passée en paramètre
    public ProduitDAOImpl(Connection conn) {
        this.conn = conn;
    }

    
    @Override
public int getNombreProduits(Connection conn) throws SQLException {
    String sql = "SELECT COUNT(*) FROM produit";
    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
            return rs.getInt(1);
        }
    }
    return 0;
}

    @Override
    public void ajouterProduit(Produit produit) {
        String sql = "INSERT INTO produit(nomproduit, quantite, prix) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produit.getNomproduit());
            pstmt.setInt(2, produit.getQuantite());
            pstmt.setDouble(3, produit.getPrix());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit: " + e.getMessage());
        }
    }

    @Override
public void modifierProduit(Produit produit) throws SQLException {
    // Vérifier si le nom existe déjà pour un autre produit
    String checkSql = "SELECT COUNT(*) FROM produit WHERE nomproduit = ? AND numeroproduit != ?";
    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
        checkStmt.setString(1, produit.getNomproduit());
        checkStmt.setInt(2, produit.getId());
        
        try (ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Un produit avec ce nom existe déjà");
            }
        }
    }

    // Mettre à jour le produit
    String updateSql = "UPDATE produit SET nomproduit = ?, quantite = ?, prix = ? WHERE numeroproduit = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
        pstmt.setString(1, produit.getNomproduit());
        pstmt.setInt(2, produit.getQuantite());
        pstmt.setDouble(3, produit.getPrix());
        pstmt.setInt(4, produit.getId());
        
        int rowsUpdated = pstmt.executeUpdate();
        if (rowsUpdated == 0) {
            throw new SQLException("Le produit n'a pas été trouvé");
        }
    }
}

    @Override
    public void supprimerProduit(int id) {
        String sql = "DELETE FROM produit WHERE numeroproduit=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit: " + e.getMessage());
        }
    }

    @Override
public List<Produit> getAllProducts(Connection conn) throws SQLException {
    List<Produit> products = new ArrayList<>();
    String sql = "SELECT * FROM produit";
    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            Produit product = new Produit(
                rs.getString("nomproduit"),
                rs.getInt("quantite"),
                rs.getDouble("prix")
            );
            product.setId(rs.getInt("numeroproduit"));
            products.add(product);
        }
    }
    return products;
}
    
    @Override
public boolean existeNomProduit(String nom) {
    String sql = "SELECT COUNT(*) FROM produit WHERE nomproduit = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) { // ✅ Corrigé ici
        stmt.setString(1, nom);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
@Override
public void mettreAJourQuantite(Connection conn, int idProduit, int nouvelleQuantite) throws SQLException {
    String sql = "UPDATE produit SET quantite = ? WHERE numeroproduit = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, nouvelleQuantite);
        ps.setInt(2, idProduit);
        ps.executeUpdate();
    }
}
@Override
public Produit getProduitById(Connection conn, int id) throws SQLException {
    String sql = "SELECT * FROM produit WHERE numeroproduit = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Produit produit = new Produit(
                    rs.getString("nomproduit"),
                    rs.getInt("quantite"),
                    rs.getDouble("prix")
                );
                produit.setId(rs.getInt("numeroproduit"));
                return produit;
            }
        }
    }
    return null;
}
@Override
public List<Produit> getProduitsFaiblesQuantites(int limit) throws SQLException {
    List<Produit> produits = new ArrayList<>();
    String sql = "SELECT * FROM produit ORDER BY quantite ASC LIMIT ?";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, limit);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Produit produit = new Produit(
                rs.getString("nomproduit"),
                rs.getInt("quantite"),
                rs.getDouble("prix")
            );
            produit.setId(rs.getInt("numeroproduit"));
            produits.add(produit);
        }
    }
    return produits;
}
@Override
    public List<Produit> rechercherProduitsParNom(Connection conn, String nom) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE nomproduit LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nom + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Produit produit = new Produit(
                    rs.getString("nomproduit"),
                    rs.getInt("quantite"),
                    rs.getDouble("prix")
                );
                produit.setId(rs.getInt("numeroproduit"));
                produits.add(produit);
            }
        }
        return produits;
    }

    @Override
    public List<Produit> rechercherProduitsParStock(Connection conn, int stock) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE quantite = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stock);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Produit produit = new Produit(
                    rs.getString("nomproduit"),
                    rs.getInt("quantite"),
                    rs.getDouble("prix")
                );
                produit.setId(rs.getInt("numeroproduit"));
                produits.add(produit);
            }
        }
        return produits;
    }

    @Override
    public List<Produit> rechercherProduitsParPrix(Connection conn, double prix) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE prix = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, prix);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Produit produit = new Produit(
                    rs.getString("nomproduit"),
                    rs.getInt("quantite"),
                    rs.getDouble("prix")
                );
                produit.setId(rs.getInt("numeroproduit"));
                produits.add(produit);
            }
        }
        return produits;
    }
}

    

