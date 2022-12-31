package com.dm.sam.model;

import java.io.Serializable;

public class Categorie implements Serializable
{
    private int id_categorie;
    private String nom;
    private String avatar;

    public Categorie() {
    }

    public Categorie(int id_categorie, String nom, String avatar) {
        this.id_categorie = id_categorie;
        this.nom = nom;
        this.avatar = avatar;
    }

    public int getId_categorie() {
        return id_categorie;
    }

    public void setId_categorie(int id_categorie) {
        this.id_categorie = id_categorie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
