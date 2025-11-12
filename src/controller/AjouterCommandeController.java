package controller;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;
import application.ConnectToDB;

import java.sql.Connection;
import java.sql.SQLException;

public class AjouterCommandeController {
    @FXML private ComboBox<String> combobox;
    @FXML private TableView<Client> TabeaulClient;
    @FXML private TableColumn<Client, String> nomclient, prenomclient, adresseclient, teleclient;
    @FXML private ComboBox<String> produitCombobox;
@FXML private TextField produitSearchText;
    @FXML private TableView<Produit> TableauProduit;
    @FXML private TableColumn<Produit, String> nomproduit;
    @FXML private TableColumn<Produit, Integer> quatiteproduit;
    @FXML private TableColumn<Produit, Boolean> checkproduit;
    @FXML private TableColumn<Produit, Integer> quantiteChoisi;
    @FXML private TableColumn<Produit, Double> prixProduit;
    
    @FXML private TextField searchtext;
    @FXML private Button Ajoutercommand;

    private ObservableList<Client> clientList = FXCollections.observableArrayList();
    private ObservableList<Produit> produitList = FXCollections.observableArrayList();
    private ObservableList<Produit> produitsSelectionnes = FXCollections.observableArrayList();
    
    private ClientDAOImpl clientDAO;
    private ProduitDAO produitDAO;
    private CommandeDAO commandeDAO;
    private DetailCommandeDAO detailCommandeDAO;
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        try (Connection conn = ConnectToDB.getConnection()) {
            clientDAO = new ClientDAOImpl();
            produitDAO = new ProduitDAOImpl(conn);
            commandeDAO = new CommandeDAOImpl();
            detailCommandeDAO = new DetailCommandeDAOImpl();
            produitCombobox.setItems(FXCollections.observableArrayList("Nom", "Stock", "Prix"));
            // Config client columns
            nomclient.setCellValueFactory(new PropertyValueFactory<>("nom"));
            prenomclient.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            adresseclient.setCellValueFactory(new PropertyValueFactory<>("adresse"));
            teleclient.setCellValueFactory(new PropertyValueFactory<>("telephone"));

            // Config product columns
            nomproduit.setCellValueFactory(new PropertyValueFactory<>("nomproduit"));
            quatiteproduit.setCellValueFactory(new PropertyValueFactory<>("quantite"));
            prixProduit.setCellValueFactory(new PropertyValueFactory<>("prix"));
            
            // Select column
            checkproduit.setCellValueFactory(cellData -> cellData.getValue().selectionneProperty());
            checkproduit.setCellFactory(column -> new TableCell<>() {
                private final CheckBox checkBox = new CheckBox();
                
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Produit produit = getTableView().getItems().get(getIndex());
                        checkBox.setSelected(produit.isSelectionne());
                        checkBox.setOnAction(event -> {
                            produit.setSelectionne(checkBox.isSelected());
                            if (checkBox.isSelected()) {
                                produitsSelectionnes.add(produit);
                            } else {
                                produitsSelectionnes.remove(produit);
                            }
                        });
                        setGraphic(checkBox);
                    }
                }
            });
            
            // Quantity column
            quantiteChoisi.setCellValueFactory(cellData -> cellData.getValue().quantiteChoisieProperty().asObject());
            quantiteChoisi.setCellFactory(column -> new TableCell<>() {
                private final TextField textField = new TextField();
                
                {
                    textField.setPromptText("Qté");
                    textField.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.matches("\\d*")) {
                            textField.setText(newVal.replaceAll("[^\\d]", ""));
                        }
                    });
                }
                
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Produit produit = getTableView().getItems().get(getIndex());
                        textField.setText(produit.getQuantiteChoisie() > 0 ? 
                            String.valueOf(produit.getQuantiteChoisie()) : "");
                        textField.textProperty().addListener((obs, oldVal, newVal) -> {
                            if (!newVal.isEmpty()) {
                                produit.setQuantiteChoisie(Integer.parseInt(newVal));
                            } else {
                                produit.setQuantiteChoisie(0);
                            }
                        });
                        setGraphic(textField);
                    }
                }
            });

            // Initialize ComboBox
            combobox.setItems(FXCollections.observableArrayList("Nom", "Prénom", "Adresse", "Téléphone"));

            // Load initial data
            loadClients();
            loadProduits();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert("Erreur", "Connexion à la base échouée");
        }
    }

    private void loadClients() throws ClassNotFoundException {
        try (Connection conn = ConnectToDB.getConnection()) {
            clientList.setAll(clientDAO.getAllClients(conn));
            TabeaulClient.setItems(clientList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Chargement clients échoué");
        }
    }

    private void loadProduits() {
        try (Connection conn = ConnectToDB.getConnection()) {
            produitList.setAll(produitDAO.getAllProducts(conn));
            TableauProduit.setItems(produitList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les produits: " + e.getMessage());
        }
    }
     @FXML 
    private void chercheProduit() {
        String searchText = produitSearchText.getText();
        String selectedCategory = produitCombobox.getValue();

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            showAlert("Attention", "Sélectionnez un critère de recherche pour les produits");
            return;
        }

        try (Connection conn = ConnectToDB.getConnection()) {
            switch (selectedCategory) {
                case "Nom":
                    produitList.setAll(produitDAO.rechercherProduitsParNom(conn, searchText));
                    break;
                case "Stock":
                    if (!searchText.matches("\\d+")) {
                        showAlert("Erreur", "Veuillez entrer un nombre valide pour le stock");
                        return;
                    }
                    produitList.setAll(produitDAO.rechercherProduitsParStock(conn, Integer.parseInt(searchText)));
                    break;
                case "Prix":
                    if (!searchText.matches("\\d+(\\.\\d+)?")) {
                        showAlert("Erreur", "Veuillez entrer un nombre valide pour le prix");
                        return;
                    }
                    produitList.setAll(produitDAO.rechercherProduitsParPrix(conn, Double.parseDouble(searchText)));
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert("Erreur", "Recherche de produits échouée: " + e.getMessage());
        }
    }

    @FXML
    private void reinitialiserRechercheProduit() {
        produitCombobox.getSelectionModel().clearSelection();
        produitSearchText.clear();
        loadProduits();
    }
    @FXML
    private void cherche() throws ClassNotFoundException {
        String searchText = searchtext.getText();
        String selectedCategory = combobox.getValue();

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            showAlert("Attention", "Sélectionnez un critère");
            return;
        }

        try (Connection conn = ConnectToDB.getConnection()) {
            clientList.setAll(clientDAO.rechercherClients(conn, selectedCategory, searchText));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Recherche échouée");
        }
    }

    @FXML
    public void Ajoutercommand() throws ClassNotFoundException {
        Client selectedClient = TabeaulClient.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            showAlert("Erreur", "Veuillez sélectionner un client.");
            return;
        }

        if (produitsSelectionnes.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner au moins un produit.");
            return;
        }

        try (Connection conn = ConnectToDB.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Create command (date will be auto-set by database)
                Commande commande = new Commande(selectedClient.getId());
                commandeDAO.ajouterCommande(conn, commande);
                
                // Add selected products
                for (Produit produit : produitsSelectionnes) {
                    if (produit.getQuantiteChoisie() <= 0) {
                        throw new SQLException("Quantité invalide pour: " + produit.getNomproduit());
                    }
                    
                    if (produit.getQuantite() < produit.getQuantiteChoisie()) {
                        throw new SQLException("Stock insuffisant pour: " + produit.getNomproduit());
                    }
                    
                    // Add order detail
                    detailCommandeDAO.ajouterDetail(
                        conn,
                        commande.getNumerocommande(),
                        produit.getId(),
                        produit.getQuantiteChoisie(),
                        produit.getPrix()
                    );
                    
                    // Update stock
                    produitDAO.mettreAJourQuantite(
                        conn,
                        produit.getId(),
                        produit.getQuantite() - produit.getQuantiteChoisie()
                    );
                }
                
                conn.commit();
                showAlert("Succès", "Commande #" + commande.getNumerocommande() + " créée avec " + 
                         produitsSelectionnes.size() + " produits");
                
                // Reset selection
                produitsSelectionnes.clear();
                
                // Refresh data
                loadProduits();
                
                if (mainController != null) {
                    mainController.mettreAJourNombreCommandes();
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de création de commande: " + e.getMessage());
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}