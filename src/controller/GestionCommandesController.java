package controller;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.beans.property.SimpleStringProperty;
import model.*;
import application.ConnectToDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class GestionCommandesController {

    // Références FXML
    @FXML private ComboBox<String> combobox;
    @FXML private TextField cherchetext;
    @FXML private Button cherche, annuler;
    @FXML private MenuButton export;
    @FXML private MenuItem exportToCsv, exportToPdf;
    
    @FXML private TableView<Commande> tableaucommand;
    @FXML private TableColumn<Commande, Integer> numclient, numcommande;
    @FXML private TableColumn<Commande, LocalDate> date;
    @FXML private TableColumn<Commande, String> actionsCommande;
    
    @FXML private TableView<DetailCommande> tableauproduit;
    @FXML private TableColumn<DetailCommande, String> nomproduit;
    @FXML private TableColumn<DetailCommande, Integer> quantiteprod;
    @FXML private TableColumn<DetailCommande, String> actionsProduit;
    
    @FXML private TextField textnumclient, quantitetext;
    @FXML private DatePicker datetext;
    @FXML private Button modifiercomm, modifierproduit;
    
    // Données
    private ObservableList<Commande> commandes = FXCollections.observableArrayList();
    private ObservableList<DetailCommande> detailsCommande = FXCollections.observableArrayList();
    private Commande commandeSelectionnee;
    private DetailCommande detailSelectionne;
    
    // DAOs
    private CommandeDAO commandeDAO;
    private DetailCommandeDAO detailCommandeDAO;
    private ProduitDAO produitDAO;
    private ClientDAO clientDAO;
    private MainController mainController;

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
    @FXML
public void initialize() {
    try (Connection conn = ConnectToDB.getConnection()) {
        commandeDAO = new CommandeDAOImpl();
        detailCommandeDAO = new DetailCommandeDAOImpl();
        produitDAO = new ProduitDAOImpl(conn);
        clientDAO = new ClientDAOImpl();
        
        // Configurer les ComboBox
        combobox.setItems(FXCollections.observableArrayList(
            "Numéro Client", "Numéro Commande", "Date"
        ));
        
        // Configurer les tableaux
        configurerTableauCommandes();
        configurerTableauProduits();
        
        // Charger les données
        chargerCommandes();
        
        // Ajouter un listener pour la sélection des commandes
        tableaucommand.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    chargerDetailsCommande(newSelection.getNumerocommande());
                    commandeSelectionnee = newSelection;
                }
            });
        
    } catch (SQLException | ClassNotFoundException e) {
        afficherErreur("Erreur de connexion", e.getMessage());
    }
}
private void configurerTableauCommandes() {
    numclient.setCellValueFactory(new PropertyValueFactory<>("numeroclient"));
    numcommande.setCellValueFactory(new PropertyValueFactory<>("numerocommande"));
    date.setCellValueFactory(new PropertyValueFactory<>("datecommande"));
    
    actionsCommande.setCellFactory(param -> new TableCell<>() {
        private final Button btnModifier = new Button();
        private final Button btnSupprimer = new Button();
        private final HBox hbox = new HBox(5, btnModifier, btnSupprimer);
        
        {
            hbox.setAlignment(Pos.CENTER);
            btnModifier.setStyle("-fx-background-color: #2196F3; -fx-padding: 5;");
            btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-padding: 5;");
            
            // Icône de modification (crayon)
            SVGPath editIcon = new SVGPath();
            editIcon.setContent(
                "M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168z" +
                "M11.207 2.5L13.5 4.793 14.793 3.5 12.5 1.207zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293zm-9.761 5.175-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325"
            );
            editIcon.setFill(javafx.scene.paint.Color.WHITE);
            
            // Icône de suppression (poubelle)
            SVGPath trashIcon = new SVGPath();
            trashIcon.setContent(
                "M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z" +
                "M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4L4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3h11V2h-11v1z"
            );
            trashIcon.setFill(javafx.scene.paint.Color.WHITE);
            
            // Conteneurs pour les icônes
            StackPane editContainer = new StackPane(editIcon);
            editContainer.setPrefSize(16, 16);
            
            StackPane trashContainer = new StackPane(trashIcon);
            trashContainer.setPrefSize(16, 16);
            
            btnModifier.setGraphic(editContainer);
            btnSupprimer.setGraphic(trashContainer);
            
            // Tooltips
            btnModifier.setTooltip(new Tooltip("Modifier la commande"));
            btnSupprimer.setTooltip(new Tooltip("Supprimer la commande"));
            
            // Actions des boutons
            btnModifier.setOnAction(e -> {
                Commande cmd = getTableView().getItems().get(getIndex());
                if (cmd != null) {
                    selectionnerCommande(cmd);
                }
            });
            
            btnSupprimer.setOnAction(e -> {
                Commande cmd = getTableView().getItems().get(getIndex());
                if (cmd != null) {
                    supprimerCommande(cmd.getNumerocommande());
                }
            });
        }
        
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : hbox);
        }
    });
}
    private void configurerTableauProduits() {
        nomproduit.setCellValueFactory(cellData -> {
            try (Connection conn = ConnectToDB.getConnection()) {
                Produit produit = produitDAO.getProduitById(conn, cellData.getValue().getNumeroproduit());
                return new SimpleStringProperty(produit != null ? produit.getNomproduit() : "N/A");
            } catch (SQLException | ClassNotFoundException e) {
                return new SimpleStringProperty("N/A");
            }
        });
        
        quantiteprod.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        
        actionsProduit.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button();
            private final Button btnSupprimer = new Button();
            private final HBox hbox = new HBox(5, btnModifier, btnSupprimer);
            
            {
                hbox.setAlignment(Pos.CENTER);
            btnModifier.setStyle("-fx-background-color: #2196F3; -fx-padding: 5;");
            btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-padding: 5;");
            
            // Icône de modification (crayon)
            SVGPath editIcon = new SVGPath();
            editIcon.setContent(
                "M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168z" +
                "M11.207 2.5L13.5 4.793 14.793 3.5 12.5 1.207zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293zm-9.761 5.175-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325"
            );
            editIcon.setFill(javafx.scene.paint.Color.WHITE);
            
            // Icône de suppression (poubelle)
            SVGPath trashIcon = new SVGPath();
            trashIcon.setContent(
                "M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z" +
                "M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4L4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3h11V2h-11v1z"
            );
            trashIcon.setFill(javafx.scene.paint.Color.WHITE);
            
            // Conteneurs pour les icônes
            StackPane editContainer = new StackPane(editIcon);
            editContainer.setPrefSize(16, 16);
            
            StackPane trashContainer = new StackPane(trashIcon);
            trashContainer.setPrefSize(16, 16);
            
            btnModifier.setGraphic(editContainer);
            btnSupprimer.setGraphic(trashContainer);
                btnModifier.setOnAction(event -> {
                    DetailCommande detail = getTableView().getItems().get(getIndex());
                    selectionnerDetail(detail);
                });
                
                btnSupprimer.setOnAction(event -> {
                    DetailCommande detail = getTableView().getItems().get(getIndex());
                    supprimerDetail(detail);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });
    }
    
    private void chargerCommandes() {
        try (Connection conn = ConnectToDB.getConnection()) {
            commandes.setAll(commandeDAO.getAllCommandes(conn));
            tableaucommand.setItems(commandes);
        } catch (SQLException | ClassNotFoundException e) {
            afficherErreur("Erreur de chargement", e.getMessage());
        }
    }
    
    private void chargerDetailsCommande(int numeroCommande) {
        try (Connection conn = ConnectToDB.getConnection()) {
            detailsCommande.setAll(detailCommandeDAO.getDetailsByCommande(conn, numeroCommande));
            tableauproduit.setItems(detailsCommande);
        } catch (SQLException | ClassNotFoundException e) {
            afficherErreur("Erreur de chargement", e.getMessage());
        }
    }
    
    @FXML
    private void chercher() {
        String critere = combobox.getValue();
        String valeur = cherchetext.getText().trim();
        
        if (critere == null || valeur.isEmpty()) {
            afficherAlerte("Recherche", "Veuillez sélectionner un critère et entrer une valeur");
            return;
        }
        
        try (Connection conn = ConnectToDB.getConnection()) {
            switch (critere) {
                case "Numéro Client":
                    commandes.setAll(commandeDAO.getCommandesByClient(conn, Integer.parseInt(valeur)));
                    break;
                case "Numéro Commande":
                    Commande cmd = commandeDAO.getCommandeById(conn, Integer.parseInt(valeur));
                    if (cmd != null) {
                        commandes.setAll(cmd);
                    }
                    break;
                case "Date":
                    // Implémenter la recherche par date si nécessaire
                    break;
                default:
                    chargerCommandes();
            }
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            afficherErreur("Erreur de recherche", e.getMessage());
        }
    }
    
    @FXML
    private void annulerRecherche() {
        cherchetext.clear();
        chargerCommandes();
    }
    
    
    
    private void selectionnerCommande(Commande commande) {
        this.commandeSelectionnee = commande;
        textnumclient.setText(String.valueOf(commande.getNumeroclient()));
        datetext.setValue(commande.getDatecommande());
        chargerDetailsCommande(commande.getNumerocommande());
    }
    
    private void selectionnerDetail(DetailCommande detail) {
        this.detailSelectionne = detail;
        quantitetext.setText(String.valueOf(detail.getQuantite()));
    }
    
    @FXML
    private void modifierCommande() {
        if (commandeSelectionnee == null) {
            afficherAlerte("Modification", "Aucune commande sélectionnée");
            return;
        }
        
        try {
            int nouveauClientId = Integer.parseInt(textnumclient.getText());
            LocalDate nouvelleDate = datetext.getValue();
            
            try (Connection conn = ConnectToDB.getConnection()) {
                commandeSelectionnee.setNumeroclient(nouveauClientId);
                commandeSelectionnee.setDatecommande(nouvelleDate);
                
                commandeDAO.modifierCommande(conn, commandeSelectionnee);
                afficherAlerte("Succès", "Commande modifiée avec succès");
                chargerCommandes();
            }
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            afficherErreur("Erreur de modification", e.getMessage());
        }
    }
    
    @FXML
    private void modifierProduit() {
        if (detailSelectionne == null) {
            afficherAlerte("Modification", "Aucun produit sélectionné");
            return;
        }
        
        try {
            int nouvelleQuantite = Integer.parseInt(quantitetext.getText());
            
            try (Connection conn = ConnectToDB.getConnection()) {
                // Mettre à jour la quantité dans le détail
                detailSelectionne.setQuantite(nouvelleQuantite);
                detailCommandeDAO.modifierDetail(conn, detailSelectionne);
                
                afficherAlerte("Succès", "Produit modifié avec succès");
                chargerDetailsCommande(commandeSelectionnee.getNumerocommande());
            }
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            afficherErreur("Erreur de modification", e.getMessage());
        }
    }
    
    private void supprimerCommande(int numeroCommande) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la commande #" + numeroCommande);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette commande ?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = ConnectToDB.getConnection()) {
                commandeDAO.supprimerCommande(conn, numeroCommande);
                afficherAlerte("Succès", "Commande supprimée avec succès");
                chargerCommandes();
            } catch (SQLException | ClassNotFoundException e) {
                afficherErreur("Erreur de suppression", e.getMessage());
            }
        }
    }
    
    private void supprimerDetail(DetailCommande detail) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le produit de la commande");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce produit de la commande ?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = ConnectToDB.getConnection()) {
                detailCommandeDAO.supprimerDetail(conn, detail.getNumerodetail());
                afficherAlerte("Succès", "Produit supprimé de la commande");
                chargerDetailsCommande(commandeSelectionnee.getNumerocommande());
            } catch (SQLException | ClassNotFoundException e) {
                afficherErreur("Erreur de suppression", e.getMessage());
            }
        }
    }
    
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}