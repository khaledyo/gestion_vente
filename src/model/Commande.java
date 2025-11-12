package model;

import java.time.LocalDate;

public class Commande {
    private int numerocommande;
    private LocalDate datecommande;
    private int numeroclient;

    
    public Commande(int numeroclient) {
        this.numeroclient = numeroclient;
    }

    
    public Commande(int numerocommande, LocalDate datecommande, int numeroclient) {
        this.numerocommande = numerocommande;
        this.datecommande = datecommande;
        this.numeroclient = numeroclient;
    }

    public int getNumerocommande() {
        return numerocommande;
    }

    public void setNumerocommande(int numerocommande) {
        this.numerocommande = numerocommande;
    }

    public LocalDate getDatecommande() {
        return datecommande;
    }

    public void setDatecommande(LocalDate datecommande) {
        this.datecommande = datecommande;
    }

    public int getNumeroclient() {
        return numeroclient;
    }

    public void setNumeroclient(int numeroclient) {
        this.numeroclient = numeroclient;
    }
}