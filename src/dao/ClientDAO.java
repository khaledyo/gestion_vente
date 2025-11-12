package dao;

import java.sql.Connection;
import java.sql.SQLException;
import model.Client;

public interface ClientDAO {
    void ajouterClient(Connection conn, Client client) throws SQLException;
}
