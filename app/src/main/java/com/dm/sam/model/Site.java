package com.dm.sam.model;

import android.content.ContentValues;
import com.dm.sam.db.DatabaseHelper;

import java.io.Serializable;

public class Site {

    private  long id_site;
    private String nom;
    private float latitude;
    private float longitude;
    private int code_postal;
    private long categorie; // id_categorie
    private String resume;

    public Site() {
    }

    public Site(String nom, float latitude, float longitude, int code_postal, long categorie, String resume) {
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.code_postal = code_postal;
        this.categorie = categorie;
        this.resume = resume;

    }

    public Site(long id_site, String nom, float latitude, float longitude, int code_postal, long categorie, String resume) {
        this.id_site = id_site;
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.code_postal = code_postal;
        this.categorie = categorie;
        this.resume = resume;
    }


    public long getId_site() {
        return id_site;
    }

    public void setId_site(long id_site) {
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

    public long getCategorie() {
        return categorie;
    }

    public void setCategorie(long categorie) {
        this.categorie = categorie;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public static Site fromContentValues(ContentValues values) {
        final Site site = new Site();

        if (values.containsKey("id_site")) site.setId_site(values.getAsLong("id_site"));
        if (values.containsKey("label")) site.setNom(values.getAsString("nom"));
        if (values.containsKey("latitude")) site.setLatitude(values.getAsFloat("latitude"));
        if (values.containsKey("longitude")) site.setLongitude(values.getAsFloat("longitude"));
        if (values.containsKey("code_postal")) site.setCode_postal(values.getAsInteger("code_postal"));
        if (values.containsKey("categorie")) site.setCategorie(values.getAsInteger("categorie"));
        if (values.containsKey("resume")) site.setResume(values.getAsString("summary"));

        return site;
    }

}
