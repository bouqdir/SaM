package com.dm.sam.model;

import android.content.ContentValues;

import java.io.Serializable;

public class Categorie
{
    private long id_categorie;
    private String nom;
    private String avatar;

    public Categorie() {
    }

    public Categorie(long id_categorie, String nom, String avatar) {
        this.id_categorie = id_categorie;
        this.nom = nom;
        this.avatar = avatar;
    }

    public long getId_categorie() {
        return id_categorie;
    }

    public void setId_categorie(long id_categorie) {
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

    public static Categorie fromContentValues(ContentValues values) {
        final Categorie category = new Categorie();

        if (values.containsKey("id")) category.setId_categorie(values.getAsInteger("id_categorie"));
        if (values.containsKey("id_categorie")) category.setNom(values.getAsString("nom"));
        if (values.containsKey("avatar")) category.setNom(values.getAsString("avatar"));

        return category;
    }
}
