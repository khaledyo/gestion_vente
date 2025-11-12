package model;

public class DetailCommande {
    private int numerodetail;
    private int numerocommande;
    private int numeroproduit;
    private int quantite;
    private double prixunitaire;

    public DetailCommande(int numerocommande, int numeroproduit, int quantite, double prixunitaire) {
        this.numerocommande = numerocommande;
        this.numeroproduit = numeroproduit;
        this.quantite = quantite;
        this.prixunitaire = prixunitaire;
    }

    public int getNumerodetail() {
        return numerodetail;
    }

    public void setNumerodetail(int numerodetail) {
        this.numerodetail = numerodetail;
    }

    public int getNumerocommande() {
        return numerocommande;
    }

    public void setNumerocommande(int numerocommande) {
        this.numerocommande = numerocommande;
    }

    public int getNumeroproduit() {
        return numeroproduit;
    }

    public void setNumeroproduit(int numeroproduit) {
        this.numeroproduit = numeroproduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixunitaire() {
        return prixunitaire;
    }

    public void setPrixunitaire(double prixunitaire) {
        this.prixunitaire = prixunitaire;
    }

    // Getters et setters...
}