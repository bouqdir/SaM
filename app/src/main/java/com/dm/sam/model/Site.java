package com.dm.sam.model;

import java.io.Serializable;

public class Site implements Serializable {

    private  int id_site;
    private String nom;
    private float latitude;
    private float longitude;
    private int code_postal;
    private int categorie; // id_categorie
    private String resume;

    public Site() {
    }

    public Site(String nom, float latitude, float longitude, int code_postal, int categorie, String resume) {
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.code_postal = code_postal;
        this.categorie = categorie;
        this.resume = resume;

    }

    public Site(int id_site, String nom, float latitude, float longitude, int code_postal, int categorie, String resume) {
        this.id_site = id_site;
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.code_postal = code_postal;
        this.categorie = categorie;
        this.resume = resume;
    }

    public int getId_site() {
        return id_site;
    }

    public void setId_site(int id_site) {
        this.id_site = id_site;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getCode_postal() {
        return code_postal;
    }

    public void setCode_postal(int code_postal) {
        this.code_postal = code_postal;
    }

    public int getCategorie() {
        return categorie;
    }

    public void setCategorie(int categorie) {
        this.categorie = categorie;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }
}
