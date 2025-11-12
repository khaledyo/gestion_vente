package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.DetailCommande;


public class DetailCommandeDAOImpl implements DetailCommandeDAO {
    @Override
    public void ajouterDetail(Connection conn, int numCommande, int numProduit, int quantite, double prix) 
            throws SQLException {
        String sql = "INSERT INTO detailcommande (numerocommande, numeroproduit, quantite, prixunitaire) " +
                    "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numCommande);
            ps.setInt(2, numProduit);
            ps.setInt(3, quantite);
            ps.setDouble(4, prix);
            ps.executeUpdate();
        }
    }

    @Override
    public void modifierDetail(Connection conn, DetailCommande detail) throws SQLException {
        String sql = "UPDATE DetailCommande SET quantite = ?, prixunitaire = ? WHERE numerodetail = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getQuantite());
            ps.setDouble(2, detail.getPrixunitaire());
            ps.setInt(3, detail.getNumerodetail());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimerDetail(Connection conn, int idDetail) throws SQLException {
        String sql = "DELETE FROM DetailCommande WHERE numerodetail = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDetail);
            ps.executeUpdate();
        }
    }

    @Override
public List<DetailCommande> getDetailsByCommande(Connection conn, int numeroCommande) throws SQLException {
    List<DetailCommande> details = new ArrayList<>();
    String sql = "SELECT d.numerodetail, d.numerocommande, d.numeroproduit, d.quantite, d.prixunitaire " +
                "FROM DetailCommande d " +
                "WHERE d.numerocommande = ?";
    
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, numeroCommande);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DetailCommande detail = new DetailCommande(
                    rs.getInt("numerocommande"),
                    rs.getInt("numeroproduit"),
                    rs.getInt("quantite"),
                    rs.getDouble("prixunitaire")
                );
                detail.setNumerodetail(rs.getInt("numerodetail"));
                details.add(detail);
            }
        }
    }
    return details;
}
}