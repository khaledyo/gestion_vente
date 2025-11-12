package model;

import javafx.beans.property.*;

public class Produit {
    private final IntegerProperty id;
    private final StringProperty nomproduit;
    private final IntegerProperty quantite;
    private final DoubleProperty prix;
    private final BooleanProperty selectionne;
    private final IntegerProperty quantiteChoisie;

    public Produit(String nomproduit, int quantite, double prix) {
        this.nomproduit = new SimpleStringProperty(nomproduit);
        this.quantite = new SimpleIntegerProperty(quantite);
        this.prix = new SimpleDoubleProperty(prix);
        this.id = new SimpleIntegerProperty();
        this.selectionne = new SimpleBooleanProperty(false);
        this.quantiteChoisie = new SimpleIntegerProperty(0);
    }

    // Getters et setters standards...
    public boolean isSelectionne() { return selectionne.get(); }
    public BooleanProperty selectionneProperty() { return selectionne; }
    public void setSelectionne(boolean selectionne) { this.selectionne.set(selectionne); }

    public int getQuantiteChoisie() { return quantiteChoisie.get(); }
    public IntegerProperty quantiteChoisieProperty() { return quantiteChoisie; }
    public void setQuantiteChoisie(int quantiteChoisie) { this.quantiteChoisie.set(quantiteChoisie); }

    // Getters et Setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNomproduit() {
        return nomproduit.get();
    }

    public void setNomproduit(String nomproduit) {
        this.nomproduit.set(nomproduit);
    }

    public int getQuantite() {
        return quantite.get();
    }

    public void setQuantite(int quantite) {
        this.quantite.set(quantite);
    }

    public double getPrix() {
        return prix.get();
    }

    public void setPrix(double prix) {
        this.prix.set(prix);
    }

    // Propriétés JavaFX pour les TableView
    public StringProperty nomproduitProperty() {
        return nomproduit;
    }

    public IntegerProperty quantiteProperty() {
        return quantite;
    }

    public DoubleProperty prixProperty() {
        return prix;
    }

    public IntegerProperty idProperty() {
        return id;
    }
}
