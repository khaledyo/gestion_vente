package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectToDB {
    private static final String URL = "jdbc:mysql://localhost:3306/gigashop";
    private static final String USERNAME = "userTest";
    private static final String PASSWORD = "passwordTest";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver"); 
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        createClientTableIfNotExists(conn);
        return conn;
    }

    private static void createClientTableIfNotExists(Connection conn) {
        String query = "CREATE TABLE IF NOT EXISTS Client ("
                + "numeroclient INT AUTO_INCREMENT PRIMARY KEY,"
                + "nom VARCHAR(255),"
                + "prenom VARCHAR(255),"
                + "adresse VARCHAR(255),"
                + "telephone VARCHAR(15))";
        String commandeTableQuery = "CREATE TABLE IF NOT EXISTS Commande ("
                + "numerocommande INT AUTO_INCREMENT PRIMARY KEY,"
                + "datecommande DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "numeroclient INT,"
                + "FOREIGN KEY (numeroclient) REFERENCES Client(numeroclient) ON DELETE CASCADE)";
        String sql = "CREATE TABLE IF NOT EXISTS produit (" +
                "numeroproduit INT AUTO_INCREMENT PRIMARY KEY," +
                "nomproduit VARCHAR(100) NOT NULL," +
                "quantite INT NOT NULL," +
                "prix DOUBLE NOT NULL)";
        String detailCommandeTableQuery = "CREATE TABLE IF NOT EXISTS DetailCommande ("
                + "numerodetail INT AUTO_INCREMENT PRIMARY KEY,"
                + "numerocommande INT,"
                + "numeroproduit INT,"
                + "quantite INT,"
                + "prixunitaire DOUBLE,"
                + "FOREIGN KEY (numerocommande) REFERENCES Commande(numerocommande) ON DELETE CASCADE,"
                + "FOREIGN KEY (numeroproduit) REFERENCES Produit(numeroproduit) ON DELETE CASCADE)";
        String queryLivraison = "CREATE TABLE IF NOT EXISTS Livraison ("
                + "numerolivraison INT AUTO_INCREMENT PRIMARY KEY,"
                + "numerocommande INT,"
                + "datelivraison DATE,"
                + "statut VARCHAR(50) DEFAULT 'En préparation',"
                + "adresselivraison VARCHAR(255),"
                + "transporteur VARCHAR(100),"
                + "FOREIGN KEY (numerocommande) REFERENCES Commande(numerocommande) ON DELETE CASCADE)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(query);
            stmt.execute(commandeTableQuery);
            stmt.execute(sql);
            stmt.execute(detailCommandeTableQuery);
            stmt.execute(queryLivraison);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
