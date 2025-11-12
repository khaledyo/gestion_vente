package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

public class Client {
    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty prenom;
    private final StringProperty adresse;
    private final StringProperty telephone;

    public Client(String nom, String prenom, String adresse, String telephone) {
        this.id = new SimpleIntegerProperty();
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.adresse = new SimpleStringProperty(adresse);
        this.telephone = new SimpleStringProperty(telephone);
    }

    // Ajouter les méthodes pour l'ID
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // Les autres méthodes restent les mêmes...
    public StringProperty nomProperty() { return nom; }
    public StringProperty prenomProperty() { return prenom; }
    public StringProperty adresseProperty() { return adresse; }
    public StringProperty telephoneProperty() { return telephone; }

    public String getNom() { return nom.get(); }
    public String getPrenom() { return prenom.get(); }
    public String getAdresse() { return adresse.get(); }
    public String getTelephone() { return telephone.get(); }

    public void setNom(String value) { nom.set(value); }
    public void setPrenom(String value) { prenom.set(value); }
    public void setAdresse(String value) { adresse.set(value); }
    public void setTelephone(String value) { telephone.set(value); }
}