package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import model.DetailCommande;

public interface DetailCommandeDAO {
    void ajouterDetail(Connection conn, int numCommande, int numProduit, int quantite, double prix) throws SQLException;
    void modifierDetail(Connection conn, DetailCommande detail) throws SQLException;
    void supprimerDetail(Connection conn, int idDetail) throws SQLException;
    List<DetailCommande> getDetailsByCommande(Connection conn, int numeroCommande) throws SQLException;
}