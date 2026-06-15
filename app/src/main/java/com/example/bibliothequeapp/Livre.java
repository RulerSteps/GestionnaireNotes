package com.example.bibliothequeapp;
public class Livre {

    private int id;
    private String titre;
    private String auteur;
    private String isbn;
    private boolean disponible;

    public Livre(int id, String titre, String auteur,
                 String isbn, boolean disponible) {

        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.disponible = disponible;
    }

    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getIsbn() {
        return isbn;
    }

    public boolean isDisponible() {
        return disponible;
    }
}