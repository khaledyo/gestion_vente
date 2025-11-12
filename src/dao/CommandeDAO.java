package dao;

import model.Commande;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CommandeDAO {
    void ajouterCommande(Connection conn, Commande commande) throws SQLException;
    void modifierCommande(Connection conn, Commande commande) throws SQLException;
    void supprimerCommande(Connection conn, int numeroCommande) throws SQLException;
    List<Commande> getAllCommandes(Connection conn) throws SQLException;
    List<Commande> getCommandesByClient(Connection conn, int clientId) throws SQLException;
    Commande getCommandeById(Connection conn, int numeroCommande) throws SQLException;
    int getNombreCommandes(Connection conn) throws SQLException;
}