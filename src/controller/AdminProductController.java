package controller;

import dao.ProduitDAO;
import dao.ProduitDAOImpl;
import application.ConnectToDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.Produit;

import java.sql.Connection;
import java.sql.SQLException;

public class AdminProductController {

    @FXML private TextField searchField;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TableView<Produit> productTable;
    @FXML private TableColumn<Produit, Integer> idColumn;
    @FXML private TableColumn<Produit, String> nameColumn;
    @FXML private TableColumn<Produit, Double> priceColumn;
    @FXML private TableColumn<Produit, Integer> quantityColumn;

    private ObservableList<Produit> productList = FXCollections.observableArrayList();
    private ProduitDAO productDAO;
    private MainController mainController;
    private Produit selectedProduct;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @SuppressWarnings("deprecation")
    @FXML
    public void initialize() {
        try {
            Connection conn = ConnectToDB.getConnection();
            productDAO = new ProduitDAOImpl(conn);

            idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
            nameColumn.setCellValueFactory(cellData -> cellData.getValue().nomproduitProperty());
            priceColumn.setCellValueFactory(cellData -> cellData.getValue().prixProperty().asObject());
            quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantiteProperty().asObject());

            idColumn.setPrefWidth(50);
            nameColumn.setPrefWidth(150);
            priceColumn.setPrefWidth(100);
            quantityColumn.setPrefWidth(100);

            productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            loadProducts();

            searchField.textProperty().addListener((observable, oldValue, newValue) -> filterProducts(newValue));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur de connexion à la base de données.");
        }
    }

    private void loadProducts() throws SQLException, ClassNotFoundException {
        productList.clear();
        Connection conn = ConnectToDB.getConnection();
        productList.addAll(productDAO.getAllProducts(conn)); 
        productTable.setItems(productList);
    }

    private void filterProducts(String searchText) {
        ObservableList<Produit> filteredList = FXCollections.observableArrayList();
        for (Produit product : productList) {
            if (product.getNomproduit().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(product);
            }
        }
        productTable.setItems(filteredList);
    }

    @FXML
    public void addProduct() throws ClassNotFoundException, SQLException {
        String name = nameField.getText().trim();
        if (name.isEmpty() || priceField.getText().isEmpty() || quantityField.getText().isEmpty()) {
            showAlert("Tous les champs sont obligatoires.");
            return;
        }

        if (productDAO.existeNomProduit(name)) {
            showAlert("Le nom du produit existe déjà. Veuillez choisir un nom différent.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());

            Produit newProduct = new Produit(name, quantity, price);
            productDAO.ajouterProduit(newProduct);
            clearFields();
            loadProducts();

            if (mainController != null) {
                mainController.mettreAJourNombreProduits();
                mainController.mettreAJourProduitsFaiblesQuantites();
            }

        } catch (NumberFormatException e) {
            showAlert("Veuillez entrer un nombre valide pour la quantité et le prix.");
        }
    }

    @FXML
    public void updateProduct() {
        try {
            selectedProduct = productTable.getSelectionModel().getSelectedItem();
            if (selectedProduct == null) {
                showAlert("Sélectionnez un produit à modifier.");
                return;
            }

            String newName = nameField.getText().trim();
            if (!newName.equals(selectedProduct.getNomproduit()) && productDAO.existeNomProduit(newName)) {
                showAlert("Un produit avec ce nom existe déjà. Choisissez un nom différent.");
                return;
            }

            selectedProduct.setNomproduit(newName);
            selectedProduct.setQuantite(Integer.parseInt(quantityField.getText()));
            selectedProduct.setPrix(Double.parseDouble(priceField.getText()));

            productDAO.modifierProduit(selectedProduct);
            clearFields();
            loadProducts();

            if (mainController != null) {
                mainController.mettreAJourProduitsFaiblesQuantites();
            }

        } catch (NumberFormatException e) {
            showAlert("Veuillez entrer des valeurs numériques valides.");
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteSelectedProduct() {
        try {
            selectedProduct = productTable.getSelectionModel().getSelectedItem();
            if (selectedProduct == null) {
                showAlert("Sélectionnez un produit à supprimer.");
                return;
            }

            productDAO.supprimerProduit(selectedProduct.getId());
            clearFields();
            loadProducts();

            if (mainController != null) {
                mainController.mettreAJourNombreProduits();
                mainController.mettreAJourProduitsFaiblesQuantites();
            }

        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.clear();
        priceField.clear();
        quantityField.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleTableClick(MouseEvent event) {
        selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            nameField.setText(selectedProduct.getNomproduit());
            priceField.setText(String.valueOf(selectedProduct.getPrix()));
            quantityField.setText(String.valueOf(selectedProduct.getQuantite()));
        }
    }
}