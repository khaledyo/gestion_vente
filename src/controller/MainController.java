package controller;

import application.ConnectToDB;
import dao.ClientDAOImpl;
import dao.CommandeDAOImpl;
import dao.LivraisonDAO;
import dao.ProduitDAOImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Produit;
import javafx.application.Platform;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class MainController {

    @FXML private AnchorPane choices, choices2, choices3, choices4;
    @FXML private Label choix1_1, choix1_2, choix3_1, choix3_2, choix4_1, choix4_2, choix5_1, choix5_2;
    @FXML private Label nbrclientlabel;
    @FXML private Label nbrprdlabel;
    @FXML private Label nbrcommandelabel; 
     @FXML private Label nbrlivraisonlabel;
    private void toggleChoices(AnchorPane choicePane, Label label1, Label label2) {
        boolean isVisible = label1.isVisible();
        label1.setVisible(!isVisible);
        label2.setVisible(!isVisible);
    }

    @FXML
    void closingWindow(MouseEvent event) {
        Platform.exit();
    }

    @FXML
    void show(MouseEvent event) {
        toggleChoices(choices, choix1_1, choix1_2);
    }

    @FXML
    void showCommande(MouseEvent event) {
        toggleChoices(choices2, choix3_1, choix3_2);
    }

    @FXML
    void showFacture(MouseEvent event) {
        toggleChoices(choices3, choix4_1, choix4_2);
    }

    @FXML
    void showLivraison(MouseEvent event) {
        toggleChoices(choices4, choix5_1, choix5_2);
    }

    @FXML
    public void initialize() {
        mettreAJourNombreLivraisons();
        mettreAJourNombreClients();
        mettreAJourNombreProduits();
        mettreAJourNombreCommandes(); 
        mettreAJourProduitsFaiblesQuantites();
        mettreAJourStatLivraison();
    }
    

    public void mettreAJourNombreProduits() {
        try (Connection conn = ConnectToDB.getConnection()) {
            int count = new dao.ProduitDAOImpl(conn).getNombreProduits(conn);
            nbrprdlabel.setText(String.valueOf(count));
        } catch (Exception e) {
            e.printStackTrace();
            nbrprdlabel.setText("Erreur");
        }
    }
    
    public void mettreAJourNombreClients() {
        try (Connection conn = ConnectToDB.getConnection()) {
            int count = new ClientDAOImpl().getNombreClients(conn);
            nbrclientlabel.setText(String.valueOf(count));
        } catch (Exception e) {
            e.printStackTrace();
            nbrclientlabel.setText("Erreur");
        }
    }

    public void mettreAJourNombreCommandes() {
        try (Connection conn = ConnectToDB.getConnection()) {
            int count = new CommandeDAOImpl().getNombreCommandes(conn);
            nbrcommandelabel.setText(String.valueOf(count));
        } catch (Exception e) {
            e.printStackTrace();
            nbrcommandelabel.setText("Erreur");
        }
    }
    public void mettreAJourNombreLivraisons() {
        try (Connection conn = ConnectToDB.getConnection()) {
            LivraisonDAO livraisonDAO = new LivraisonDAO();
            int count = livraisonDAO.getNombreLivraisons();
            nbrlivraisonlabel.setText(String.valueOf(count));
        } catch (Exception e) {
            e.printStackTrace();
            nbrlivraisonlabel.setText("Erreur");
        }
    }

    @FXML
    private void ajouterClient() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/clientAjoue.fxml"));
            AnchorPane root = fxmlLoader.load();

            ClientController controller = fxmlLoader.getController();
            controller.setMainController(this);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Ajout d'un Client");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void Client(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ClientsListAndModify.fxml"));
            AnchorPane root = fxmlLoader.load();

            ClientSuppModifController controller = fxmlLoader.getController();
            controller.setMainController(this);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Supprimer ou Modifier un Client");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showProduit() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/GestionProduits.fxml"));
            VBox root = fxmlLoader.load();
            AdminProductController controller = fxmlLoader.getController();
            controller.setMainController(this);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Produits");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ajoutez cette méthode pour gérer l'ouverture de la fenêtre d'ajout de commande
    @FXML
    private void ajouterCommande() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AjouterCommande.fxml"));
            AnchorPane root = fxmlLoader.load();

            AjouterCommandeController controller = fxmlLoader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter une Commande");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void GestionCommande() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/GestionCommandes.fxml"));
            AnchorPane root = fxmlLoader.load();

            GestionCommandesController controller = fxmlLoader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Gestion des Commandes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   @FXML
    private void GestionLivraison() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LivraisonView.fxml"));
            BorderPane root = fxmlLoader.load();

            LivraisonController controller = fxmlLoader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Gestion des Livraisons");
            stage.setScene(new Scene(root));
            
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }


@FXML private Label produit1Quantite, produit1Nom;
@FXML private Label produit2Quantite, produit2Nom;
@FXML private Label produit3Quantite, produit3Nom;

// Ajoutez cette méthode
public void mettreAJourProduitsFaiblesQuantites() {
    try (Connection conn = ConnectToDB.getConnection()) {
        List<Produit> produits = new ProduitDAOImpl(conn).getProduitsFaiblesQuantites(3);
        
        // Réinitialiser tous les labels
        produit1Nom.setText("");
        produit1Quantite.setText("");
        produit2Nom.setText("");
        produit2Quantite.setText("");
        produit3Nom.setText("");
        produit3Quantite.setText("");
        
        // Mettre à jour seulement les produits disponibles
        if (produits.size() > 0) {
            produit1Quantite.setText(String.valueOf(produits.get(0).getQuantite()));
            produit1Nom.setText(produits.get(0).getNomproduit());
        }
        if (produits.size() > 1) {
            produit2Quantite.setText(String.valueOf(produits.get(1).getQuantite()));
            produit2Nom.setText(produits.get(1).getNomproduit());
        }
        if (produits.size() > 2) {
            produit3Quantite.setText(String.valueOf(produits.get(2).getQuantite()));
            produit3Nom.setText(produits.get(2).getNomproduit());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
@FXML private Label affeclivrnbr;
@FXML private Label nonaffeclivrnbr;

// Ajoutez cette méthode pour mettre à jour les statistiques
public void mettreAJourStatLivraison() {
    try (Connection conn = ConnectToDB.getConnection()) {
        LivraisonDAO livraisonDAO = new LivraisonDAO();
        
        // Compter les livraisons effectuées
        int effectuees = livraisonDAO.getNombreLivraisonsParStatut("Livrée");
        affeclivrnbr.setText(String.valueOf(effectuees));
        
        // Compter les livraisons non effectuées (En préparation + Annulée)
        int enPreparation = livraisonDAO.getNombreLivraisonsParStatut("En préparation");
        int annulees = livraisonDAO.getNombreLivraisonsParStatut("Annulée");
        nonaffeclivrnbr.setText(String.valueOf(enPreparation + annulees));
        
    } catch (Exception e) {
        e.printStackTrace();
        affeclivrnbr.setText("Err");
        nonaffeclivrnbr.setText("Err");
    }
}
}