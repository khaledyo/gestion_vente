package controller;

import application.ConnectToDB;
import dao.ClientDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Client;

import java.sql.Connection;

public class ClientController {

    @FXML private TextField textnomclient;
    @FXML private TextField textprenomclient;
    @FXML private TextField textadressclient;
    @FXML private TextField textteleclient;
    @FXML private Label messageLabel;

    private MainController mainController; // pour communication

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @FXML
    public void AjouterClient(ActionEvent event) {
        try (Connection conn = ConnectToDB.getConnection()) {
            if (textnomclient.getText().isEmpty() || textprenomclient.getText().isEmpty() ||
                textadressclient.getText().isEmpty() || textteleclient.getText().isEmpty()) {
                messageLabel.setText("Tous les champs doivent être remplis !");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }
    
            String tel = textteleclient.getText();
            if (tel.length() != 8 || !tel.matches("\\d+")) {
                messageLabel.setText("Le numéro de téléphone doit contenir exactement 8 chiffres.");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }
    
            Client client = new Client(
                textnomclient.getText(),
                textprenomclient.getText(),
                textadressclient.getText(),
                tel
            );
            
            ClientDAOImpl clientDAO = new ClientDAOImpl();
            clientDAO.ajouterClient(conn, client); 
    
            messageLabel.setText("Client ajouté avec succès ! ID: " + client.getId());
            messageLabel.setStyle("-fx-text-fill: blue;");
    
            // Vider les champs
            textnomclient.clear();
            textprenomclient.clear();
            textadressclient.clear();
            textteleclient.clear();
    
            // MAJ du nombre de clients
            if (mainController != null) {
                mainController.mettreAJourNombreClients();
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors de l'ajout du client: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
