package dao;

import model.Produit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProduitDAO {
    void ajouterProduit(Produit produit);
    void modifierProduit(Produit produit) throws SQLException;
    void supprimerProduit(int id);
    List<Produit> getAllProducts(Connection conn) throws SQLException;
    int getNombreProduits(Connection conn) throws SQLException;
    boolean existeNomProduit(String nom);
    void mettreAJourQuantite(Connection conn, int idProduit, int nouvelleQuantite) throws SQLException;
    Produit getProduitById(Connection conn, int id) throws SQLException;
    List<Produit> getProduitsFaiblesQuantites(int limit) throws SQLException;
     List<Produit> rechercherProduitsParNom(Connection conn, String nom) throws SQLException;
    List<Produit> rechercherProduitsParStock(Connection conn, int stock) throws SQLException;
    List<Produit> rechercherProduitsParPrix(Connection conn, double prix) throws SQLException;
}
