package controller;

import dao.LivraisonDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Livraison;

import java.sql.SQLException;
import java.time.LocalDate;

import java.util.Optional;

public class LivraisonController {

 
    @FXML private TextField numeroCommandeField;
    @FXML private DatePicker dateLivraisonPicker;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private TextField adresseLivraisonField;
    @FXML private TextField transporteurField;
    
   
    @FXML private Button ajouterBtn;
    @FXML private Button modifierBtn;
    @FXML private Button supprimerBtn;
  
    @FXML private TableView<Livraison> livraisonTableView;
    @FXML private TableColumn<Livraison, Integer> numeroLivraisonCol;
    @FXML private TableColumn<Livraison, Integer> numeroCommandeCol;
    @FXML private TableColumn<Livraison, LocalDate> dateLivraisonCol;
    @FXML private TableColumn<Livraison, String> statutCol;
    @FXML private TableColumn<Livraison, String> transporteurCol;
    @FXML private TableColumn<Livraison, String> adresseCol;
    

    @FXML private Label statusLabel;
    
    private LivraisonDAO livraisonDAO;
    private ObservableList<Livraison> livraisonList;
    private Livraison livraisonSelectionnee;
    private MainController mainController;

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
    @FXML
    public void initialize() {
        try {
            livraisonDAO = new LivraisonDAO();
            initComboBox();
            initTableView();
            chargerDonnees();
            setupListeners();
            
            modifierBtn.setDisable(true);
            supprimerBtn.setDisable(true);
            
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Erreur", "Erreur de connexion à la base de données");
        }
    }
    
    private void initComboBox() {
        statutComboBox.setItems(FXCollections.observableArrayList(
            "En préparation", 
            "Livrée",  "Annulée"
        ));
    }
    
    private void initTableView() {
        numeroLivraisonCol.setCellValueFactory(new PropertyValueFactory<>("numeroLivraison"));
        numeroCommandeCol.setCellValueFactory(new PropertyValueFactory<>("numeroCommande"));
        dateLivraisonCol.setCellValueFactory(new PropertyValueFactory<>("dateLivraison"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        transporteurCol.setCellValueFactory(new PropertyValueFactory<>("transporteur"));
        adresseCol.setCellValueFactory(new PropertyValueFactory<>("adresseLivraison"));
        
        livraisonList = FXCollections.observableArrayList();
        livraisonTableView.setItems(livraisonList);
    }
    
    private void chargerDonnees() {
        try {
            livraisonList.setAll(livraisonDAO.getAllLivraisons());
            statusLabel.setText(livraisonList.size() + " livraison(s) chargée(s)");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des données");
        }
    }
    
    private void setupListeners() {
        livraisonTableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    livraisonSelectionnee = newSelection;
                    fillFormWithSelectedItem();
                    ajouterBtn.setDisable(true);
                    modifierBtn.setDisable(false);
                    supprimerBtn.setDisable(false);
                }
            });
    }
    
    private void fillFormWithSelectedItem() {
        numeroCommandeField.setText(String.valueOf(livraisonSelectionnee.getNumeroCommande()));
        dateLivraisonPicker.setValue(livraisonSelectionnee.getDateLivraison());
        statutComboBox.setValue(livraisonSelectionnee.getStatut());
        adresseLivraisonField.setText(livraisonSelectionnee.getAdresseLivraison());
        transporteurField.setText(livraisonSelectionnee.getTransporteur());
    }
    
    @FXML
    private void handleAjouterLivraison() {
        if (validateForm()) {
            try {
                Livraison livraison = createLivraisonFromForm();
                livraisonDAO.ajouterLivraison(livraison);
                if (mainController != null) {
                mainController.mettreAJourStatLivraison();
            }
                clearForm();
                chargerDonnees();
                statusLabel.setText("Livraison ajoutée avec succès");
            } catch (SQLException | NumberFormatException e) {
                showAlert("Erreur", "Erreur lors de l'ajout");
            }
        }
    }
    
    @FXML
    private void handleModifierLivraison() {
        if (livraisonSelectionnee != null && validateForm()) {
            try {
                updateLivraisonFromForm();
                livraisonDAO.updateLivraison(livraisonSelectionnee);
                if (mainController != null) {
                
                mainController.mettreAJourStatLivraison();
    }
                clearForm();
                chargerDonnees();
                statusLabel.setText("Livraison modifiée avec succès");
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la modification");
            }
        }
    }
    
    @FXML
    private void handleSupprimerLivraison() {
        if (livraisonSelectionnee != null) {
            Optional<ButtonType> result = showConfirmation("Confirmation", 
                "Voulez-vous vraiment supprimer cette livraison ?");
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    livraisonDAO.deleteLivraison(livraisonSelectionnee.getNumeroLivraison());
                    if (mainController != null) {
                        mainController.mettreAJourStatLivraison();
                    }
                    clearForm();
                    chargerDonnees();
                    statusLabel.setText("Livraison supprimée avec succès");
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la suppression");
                }
            }
        }
    }
    
    @FXML
    private void handleAnnuler() {
        clearForm();
        statusLabel.setText("Opération annulée");
    }
    
    private boolean validateForm() {
        if (numeroCommandeField.getText().isEmpty() || 
            dateLivraisonPicker.getValue() == null ||
            statutComboBox.getValue() == null ||
            adresseLivraisonField.getText().isEmpty() ||
            transporteurField.getText().isEmpty()) {
            
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return false;
        }
        
        try {
            Integer.parseInt(numeroCommandeField.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Numéro de commande invalide");
            return false;
        }
        
        return true;
    }
    
    private Livraison createLivraisonFromForm() {
        return new Livraison(
            Integer.parseInt(numeroCommandeField.getText()),
            dateLivraisonPicker.getValue(),
            statutComboBox.getValue(),
            adresseLivraisonField.getText(),
            transporteurField.getText()
        );
       
    }
    
    private void updateLivraisonFromForm() {
        livraisonSelectionnee.setNumeroCommande(Integer.parseInt(numeroCommandeField.getText()));
        livraisonSelectionnee.setDateLivraison(dateLivraisonPicker.getValue());
        livraisonSelectionnee.setStatut(statutComboBox.getValue());
        livraisonSelectionnee.setAdresseLivraison(adresseLivraisonField.getText());
        livraisonSelectionnee.setTransporteur(transporteurField.getText());
        
    }
    
    private void clearForm() {
        numeroCommandeField.clear();
        dateLivraisonPicker.setValue(null);
        statutComboBox.setValue(null);
        adresseLivraisonField.clear();
        transporteurField.clear();
        
        livraisonSelectionnee = null;
        livraisonTableView.getSelectionModel().clearSelection();
        
        ajouterBtn.setDisable(false);
        modifierBtn.setDisable(true);
        supprimerBtn.setDisable(true);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}