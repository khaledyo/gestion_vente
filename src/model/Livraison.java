package model;

import java.time.LocalDate;

public class Livraison {
    private int numeroLivraison;
    private int numeroCommande;
    private LocalDate dateLivraison;
    private String statut;
    private String adresseLivraison;
    private String transporteur;

    // Constructeur par défaut
    public Livraison() {
    }

    // Constructeur avec paramètres (sans numéroLivraison)
    public Livraison(int numeroCommande, LocalDate dateLivraison, String statut,
                   String adresseLivraison, String transporteur) {
        this.numeroCommande = numeroCommande;
        this.dateLivraison = dateLivraison;
        this.statut = statut;
        this.adresseLivraison = adresseLivraison;
        this.transporteur = transporteur;
    }

    // Constructeur complet avec tous les champs
    public Livraison(int numeroLivraison, int numeroCommande, LocalDate dateLivraison,
                   String statut, String adresseLivraison, String transporteur) {
        this.numeroLivraison = numeroLivraison;
        this.numeroCommande = numeroCommande;
        this.dateLivraison = dateLivraison;
        this.statut = statut;
        this.adresseLivraison = adresseLivraison;
        this.transporteur = transporteur;
    }

    // Getters et Setters
    public int getNumeroLivraison() {
        return numeroLivraison;
    }

    public void setNumeroLivraison(int numeroLivraison) {
        this.numeroLivraison = numeroLivraison;
    }

    public int getNumeroCommande() {
        return numeroCommande;
    }

    public void setNumeroCommande(int numeroCommande) {
        this.numeroCommande = numeroCommande;
    }

    public LocalDate getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(LocalDate dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public String getTransporteur() {
        return transporteur;
    }

    public void setTransporteur(String transporteur) {
        this.transporteur = transporteur;
    }

    // Méthode toString() pour l'affichage
    @Override
    public String toString() {
        return "Livraison{" +
                "numeroLivraison=" + numeroLivraison +
                ", numeroCommande=" + numeroCommande +
                ", dateLivraison=" + dateLivraison +
                ", statut='" + statut + '\'' +
                ", adresseLivraison='" + adresseLivraison + '\'' +
                ", transporteur='" + transporteur + '\'' +
                '}';
    }

    // Méthode equals() pour la comparaison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Livraison livraison = (Livraison) o;
        return numeroLivraison == livraison.numeroLivraison &&
               numeroCommande == livraison.numeroCommande &&
               dateLivraison.equals(livraison.dateLivraison) &&
               statut.equals(livraison.statut) &&
               adresseLivraison.equals(livraison.adresseLivraison) &&
               transporteur.equals(livraison.transporteur);
    }

    // Méthode hashCode()
    @Override
    public int hashCode() {
        int result = numeroLivraison;
        result = 31 * result + numeroCommande;
        result = 31 * result + dateLivraison.hashCode();
        result = 31 * result + statut.hashCode();
        result = 31 * result + adresseLivraison.hashCode();
        result = 31 * result + transporteur.hashCode();
        return result;
    }

    // Méthode de copie
    public Livraison copy() {
        return new Livraison(
                this.numeroLivraison,
                this.numeroCommande,
                this.dateLivraison,
                this.statut,
                this.adresseLivraison,
                this.transporteur
        );
    }
}