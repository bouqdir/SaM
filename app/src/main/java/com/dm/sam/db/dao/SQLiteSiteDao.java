package com.dm.sam.db.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.dm.sam.db.DatabaseHelper;
import com.dm.sam.db.service.ServiceDAO;
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;
import com.google.android.gms.maps.model.LatLng;


import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SQLiteSiteDao extends SQLiteDao<Site> implements ServiceDAO<Site> {

    @SuppressLint("StaticFieldLeak")
    private static SQLiteSiteDao instance;

    private static final String[] allColumns = { DatabaseHelper.SITE_ID, DatabaseHelper.SITE_NOM,
            DatabaseHelper.SITE_LATITUDE, DatabaseHelper.SITE_LONGITUDE, DatabaseHelper.SITE_CODE_POSTAL,
            DatabaseHelper.SITE_CATEGORIE, DatabaseHelper.SITE_RESUME };

    public SQLiteSiteDao(Context context) {
        super(context);
    }

    public static SQLiteSiteDao getInstance(Context context) {
        if (instance == null)
            instance = new SQLiteSiteDao(context);

        return instance;
    }

    @Override
    public long create(Site site) {
        openWritable();

        ContentValues values = putContentValues(site);

        long lastInsertedId = sqLiteDatabase.insert(DatabaseHelper.TABLE_SITE,
                null,
                values);

        close();

        return lastInsertedId;
    }

    @Override
    public int update(Site site) {
        openWritable();

        ContentValues values = putContentValues(site);

        return sqLiteDatabase.update(DatabaseHelper.TABLE_SITE, values, DatabaseHelper.SITE_ID + " = ?", new String[] { String.valueOf(site.getId_site()) });
    }

    @Override
    public int delete(long id) {
        openWritable();

        int returnedId = sqLiteDatabase.delete(DatabaseHelper.TABLE_SITE,
                DatabaseHelper.SITE_ID + " = ?",
                new String[] { String.valueOf(id) });

        close();

        return returnedId;
    }

    @Override
    public Site findById(long id) {
        openReadable();

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_SITE,
                allColumns,
                DatabaseHelper.SITE_ID + " = ?",
                new String[] { String.valueOf(id) },
                null, null, null, null);

        cursor.moveToFirst();

        Site site = cursorToObject(cursor);

        cursor.close();

        close();

        return site;
    }

    @Override
    public List<Site> findAll() {
        openReadable();

        List<Site> sites = new LinkedList<>();

        String query = "SELECT  * FROM " + DatabaseHelper.TABLE_SITE;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        Site site;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            site = cursorToObject(cursor);
            sites.add(site);

            cursor.moveToNext();
        }
        cursor.close();

        close();

        return sites;
    }

    public List<Site> findByCategory(long idCat) {
        openReadable();

        List<Site> sites = new LinkedList<>();

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_SITE,
                allColumns,
                DatabaseHelper.SITE_CATEGORIE + " = ?" ,
                new String[] { String.valueOf(idCat) },
                null, null, null, null);

        Site site;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            site = cursorToObject(cursor);
            sites.add(site);

            cursor.moveToNext();
        }
        cursor.close();

        close();

        return sites;
    }

    public Site findByLatLng(LatLng latLng) {
        openReadable();

        String where = DatabaseHelper.SITE_LATITUDE+" =?" + " AND " + DatabaseHelper.SITE_LONGITUDE + " = ?" ;
        String[] whereArgs = new String[] { String.valueOf(latLng.latitude),String.valueOf(latLng.longitude)};
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_SITE,
                allColumns ,
                where, whereArgs,null, null, null, null);

        if (cursor.moveToFirst()){
            return  cursorToObject(cursor);
        }

        return null;
    }
    @Override
    public Cursor getWithCursor(long id) {
        return sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_SITE + " WHERE id = ?",
                new String[] { String.valueOf(id) });
    }

    @Override
    public Site cursorToObject(Cursor cursor) {
        return new Site(cursor.getInt(0),cursor.getString(1),
                cursor.getFloat(2), cursor.getFloat(3), cursor.getInt(4),
                cursor.getInt(5), cursor.getString(6));
    }

    private ContentValues putContentValues(Site site) {
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.SITE_NOM, site.getNom());
        values.put(DatabaseHelper.SITE_LATITUDE, site.getLatitude());
        values.put(DatabaseHelper.SITE_LONGITUDE, site.getLongitude());
        values.put(DatabaseHelper.SITE_CODE_POSTAL, site.getCode_postal());
        values.put(DatabaseHelper.SITE_CATEGORIE, site.getCategorie());
        values.put(DatabaseHelper.SITE_RESUME, site.getResume());

        return values;
    }

    /**
     * This method creates default sites for the first installation
     */
    public void createDefaultSitesIfNeed() {
        openWritable();
        int count = this.getSitesCount();
        if (count == 0) {
            Site s1 = new Site("Croquo Pizza", 49.1155f, 6.2094f, 57070, 1, "Ce restaurant sobre propose sur place ou à emporter des pizzas et pâtes mais aussi des couscous et tajines.");
            Site s2 = new Site("Feminae, l’Institut médical de la femme", 49.1329f, 6.2024f, 57070, 2, "l’Institut médical de la femme. Institut Feminae, 97 Rue Claude Bernard, 57070 Metz");
            Site s3 = new Site("Er Radi Abdellah, magasin d'antiquité", 49.1159f, 6.2077f, 57070, 5, "36 Rue Nicolas Untersteller, 57070 Metz");

            this.create(s1);
            this.create(s2);
            this.create(s3);
        }
    }
    public int getSitesCount() {

        String countQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_SITE;
                Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        return count;
    }
}
