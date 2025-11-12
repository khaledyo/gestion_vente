package controller;

import dao.ClientDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.Client;
import application.ConnectToDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ClientSuppModifController {

    @FXML private TableView<Client> TableClient;
    @FXML private TableColumn<Client, String> nomcolon, prenomcolone, adresscolone, telecolone;
    @FXML private ComboBox<String> combobox;
    @FXML private TextField choosetext;
    @FXML private TextField nomtext, prenomtext, adresstext, telephonetext;

    private Client selectedClient;
    private final ClientDAOImpl clientDAO = new ClientDAOImpl();
    private final ObservableList<Client> clientList = FXCollections.observableArrayList();
    private MainController mainController;

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @FXML
    public void initialize() {
        combobox.setItems(FXCollections.observableArrayList("nom", "prenom", "adresse", "telephone"));

        nomcolon.setCellValueFactory(data -> data.getValue().nomProperty());
        prenomcolone.setCellValueFactory(data -> data.getValue().prenomProperty());
        adresscolone.setCellValueFactory(data -> data.getValue().adresseProperty());
        telecolone.setCellValueFactory(data -> data.getValue().telephoneProperty());

        refreshClientList();
    }

    private void refreshClientList() {
        try (Connection conn = ConnectToDB.getConnection()) {
            List<Client> clients = clientDAO.getAllClients(conn);
            clientList.setAll(clients);
            TableClient.setItems(clientList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les clients.");
        }
    }

    @FXML
    public void cherche() {
        String searchText = choosetext.getText();
        String selectedCategory = combobox.getValue();

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            showAlert("Attention", "Veuillez sélectionner un critère de recherche.");
            return;
        }

        try (Connection conn = ConnectToDB.getConnection()) {
            List<Client> clients = clientDAO.rechercherClients(conn, selectedCategory, searchText);
            clientList.setAll(clients);
            TableClient.setItems(clientList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert("Erreur", "Recherche échouée.");
        }
    }

    @FXML
    public void modifier(MouseEvent event) {
        selectedClient = TableClient.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            nomtext.setText(selectedClient.getNom());
            prenomtext.setText(selectedClient.getPrenom());
            adresstext.setText(selectedClient.getAdresse());
            telephonetext.setText(selectedClient.getTelephone());
        }
    }

    @FXML
    public void updateClient() {
        if (selectedClient == null) {
            showAlert("Erreur", "Aucun client sélectionné.");
            return;
        }

        selectedClient.setNom(nomtext.getText());
        selectedClient.setPrenom(prenomtext.getText());
        selectedClient.setAdresse(adresstext.getText());
        selectedClient.setTelephone(telephonetext.getText());

        try (Connection conn = ConnectToDB.getConnection()) {
            clientDAO.updateClient(conn, selectedClient);
            refreshClientList();
            if (mainController != null) {
                mainController.mettreAJourNombreClients();
            }
            annuler();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de la mise à jour.");
        }
    }

    @FXML
    public void supprimer() {
        Client selected = TableClient.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce client ?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try (Connection conn = ConnectToDB.getConnection()) {
                        clientDAO.supprimerClient(conn, selected.getNom(), selected.getPrenom());
                        clientList.remove(selected);
                        if (mainController != null) {
                            mainController.mettreAJourNombreClients();
                        }
                        annuler();
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Suppression échouée.");
                    }
                }
            });
        } else {
            showAlert("Information", "Veuillez sélectionner un client à supprimer.");
        }
    }

    @FXML
    public void annuler() {
        nomtext.clear();
        prenomtext.clear();
        adresstext.clear();
        telephonetext.clear();
        selectedClient = null;
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
