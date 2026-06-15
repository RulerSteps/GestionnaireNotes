package com.example.gestionnairenotes.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String titre;
    private String contenu;
    private String couleur;   // ex: "#219653"
    private boolean favori;
    private String date;      // ex: "12 Juin 2026"

    // Constructeur
    public Note(String titre, String contenu, String couleur, boolean favori, String date) {
        this.titre   = titre;
        this.contenu = contenu;
        this.couleur = couleur;
        this.favori  = favori;
        this.date    = date;
    }

    // Getters
    public int     getId()      { return id; }
    public String  getTitre()   { return titre; }
    public String  getContenu() { return contenu; }
    public String  getCouleur() { return couleur; }
    public boolean isFavori()   { return favori; }
    public String  getDate()    { return date; }

    // Setters
    public void setId(int id)           { this.id = id; }
    public void setTitre(String t)      { this.titre = t; }
    public void setContenu(String c)    { this.contenu = c; }
    public void setCouleur(String c)    { this.couleur = c; }
    public void setFavori(boolean f)    { this.favori = f; }
    public void setDate(String d)       { this.date = d; }
}