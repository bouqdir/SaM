package com.dm.sam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "SAM_DB";
    static final int DATABASE_VERSION = 3;
    static final String TABLE_SITE = "Site";
    static final String SITE_ID = "id_site";
    public static final String SITE_NOM = "nom";
    static final String SITE_LATITUDE = "latitude";
    static final String SITE_LONGITUDE = "longitude";
    static final String SITE_CODE_POSTAL = "code_postal";
    static final String SITE_CATEGORIE = "categorie";
    static final String SITE_RESUME = "resume";

    static final String CREATE_SITE_TABLE = "CREATE TABLE " + TABLE_SITE
            + " (" + SITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SITE_NOM + " TEXT NOT NULL, "
            + SITE_LATITUDE + " REAL NOT NULL, "
            + SITE_LONGITUDE + " REAL NOT NULL, "
            + SITE_CODE_POSTAL + " INTEGER, "
            + SITE_CATEGORIE + " INTEGERT, "
            + SITE_RESUME + " TEXT);";

    static final String TABLE_CATEGORIE = "Categorie";
    static final String CATEGORIE_ID = "id_categorie";
    static final String CATEGORIE_NAME = "nom";
    static final String CATEGORIE_AVATAR = "avatar";

    static final String CREATE_CATEGORIE_TABLE = "CREATE TABLE " + TABLE_CATEGORIE
            + " ("+ CATEGORIE_ID + " INTEGER PRIMARY KEY, "
            + CATEGORIE_NAME + " TEXT NOT NULL, "
            + CATEGORIE_AVATAR + " TEXT );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SITE_TABLE);
        db.execSQL(CREATE_CATEGORIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_SITE);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIE);
      onCreate(db);
    }

    public long addSite(Site site) {
        long id;

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SITE_LATITUDE, site.getLatitude());
        values.put(DatabaseHelper.SITE_RESUME, site.getResume());
        values.put(DatabaseHelper.SITE_CODE_POSTAL,site.getCode_postal());
        values.put(DatabaseHelper.SITE_NOM, site.getNom());
        values.put(DatabaseHelper.SITE_LONGITUDE,site.getLongitude());
        values.put(DatabaseHelper.SITE_CATEGORIE, site.getCategorie());

        this.db = this.getWritableDatabase();

        // Inserting Row
        id=db.insert(DatabaseHelper.TABLE_SITE, null, values);

        return id;
    }

    public void createDefaultSitesIfNeed()  {
        int count = this.getSitesCount();
        if(count ==0 ) {
            Site s1 = new Site("Croquo Pizza",49.1155f,6.2094f,57070,1,"Ce restaurant sobre propose sur place ou à emporter des pizzas et pâtes mais aussi des couscous et tajines.");
            Site s2 = new Site("Feminae, l’Institut médical de la femme",49.1329f,6.2024f,57070,2,"l’Institut médical de la femme. Institut Feminae, 97 Rue Claude Bernard, 57070 Metz");
            Site s3 = new Site("Er Radi Abdellah, magasin d'antiquité",49.1159f,6.2077f,57070,5,"36 Rue Nicolas Untersteller, 57070 Metz");

            this.addSite(s1);
            this.addSite(s2);
            this.addSite(s3);
        }
    }
    public int getSitesCount() {
        String countQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_SITE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        return count;
    }

    public Site getSite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SITE, new String[] { SITE_ID,
                        SITE_NOM, SITE_LATITUDE, SITE_LONGITUDE, SITE_CODE_POSTAL,SITE_CATEGORIE, SITE_RESUME }, SITE_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Site site = new Site(cursor.getInt(0),cursor.getString(1),
                cursor.getFloat(2), cursor.getFloat(3), cursor.getInt(4),
                cursor.getInt(5), cursor.getString(6));

        return site;
    }

    public Site getSiteByLatLng(LatLng latLng) {
        Log.i(TAG, "MyDatabaseHelper.getSiteByLatLng ... " );

        SQLiteDatabase db = this.getReadableDatabase();

        String[] FROM = {
                SITE_ID,
                SITE_NOM, SITE_LATITUDE, SITE_LONGITUDE, SITE_CODE_POSTAL,SITE_CATEGORIE, SITE_RESUME
        };
        String where = SITE_LATITUDE+" =?" + " AND " + SITE_LONGITUDE + " = ?" ;
        String[] whereArgs = new String[] { String.valueOf(latLng.latitude),String.valueOf(latLng.longitude)};
        Cursor cursor = db.query(TABLE_SITE, FROM , where, whereArgs,null, null, null, null);

        if (cursor.moveToFirst()){
            return new Site(Integer.parseInt(cursor.getString(0)),cursor.getString(1),
                    cursor.getFloat(2), cursor.getFloat(3), cursor.getInt(4),
                    cursor.getInt(5), cursor.getString(6));

        }

        return null;
    }
    public List<Site> getAllSites() {
        Log.i(TAG, "MyDatabaseHelper.getAllSites ... " );


        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SITE;
        List<Site> siteList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Site site = new Site(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        Float.parseFloat(cursor.getString(2)),
                        Float.parseFloat(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)),
                        Integer.parseInt(cursor.getString(5)),
                        cursor.getString(6));
                // Adding site to list
                siteList.add(site);
            } while (cursor.moveToNext());
        }
        return siteList;
    }

    public List<Site> getSitesByCategorie(int idCat) {
        Log.i(TAG, "MyDatabaseHelper.getSitesByCategorie ... " );

        String[] FROM = {
                SITE_ID,
                SITE_NOM, SITE_LATITUDE, SITE_LONGITUDE, SITE_CODE_POSTAL,SITE_CATEGORIE, SITE_RESUME
        };
        String where = SITE_CATEGORIE+" =?";
        String[] whereArgs = new String[] { String.valueOf(idCat)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SITE, FROM , where, whereArgs,null, null, null, null);

        List<Site> siteList = new ArrayList<>();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Site site = new Site(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        Float.parseFloat(cursor.getString(2)),
                        Float.parseFloat(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)),
                        Integer.parseInt(cursor.getString(5)),
                        cursor.getString(6));
                // Adding site to list
                siteList.add(site);
            } while (cursor.moveToNext());
        }
        return siteList;
    }

    public void updateSite(Site site){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SITE_LATITUDE, site.getLatitude());
        values.put(DatabaseHelper.SITE_RESUME, site.getResume());
        values.put(DatabaseHelper.SITE_CODE_POSTAL,site.getCode_postal());
        values.put(DatabaseHelper.SITE_NOM, site.getNom());
        values.put(DatabaseHelper.SITE_LONGITUDE,site.getLongitude());
        values.put(DatabaseHelper.SITE_CATEGORIE, site.getCategorie());

        this.db = this.getWritableDatabase();
        this.db.update(TABLE_SITE,values,SITE_ID + " = ?" , new String[]
                {String.valueOf(site.getId_site())});
    }

    public void deleteSite(int id) {
        this.db = this.getWritableDatabase();
        this.db.delete(TABLE_SITE, SITE_ID + " = ? ", new String[]
                {String.valueOf(id)});

    }
    ///////////////////// CRUD CATEGORIE
    public int getCategoriesCount() {
            Log.i(TAG, "MyDatabaseHelper.getCategoriesCount ... " );

            String countQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_CATEGORIE;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);

            int count = cursor.getCount();

            cursor.close();
            return count;
        }

    public long addCategorie(Categorie categorie) {
        long id;
        Log.i(TAG, "MyDatabaseHelper.addCategorie ... " + categorie.getNom());

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORIE_ID, categorie.getId_categorie());
        values.put(DatabaseHelper.CATEGORIE_NAME, categorie.getNom());
        values.put(DatabaseHelper.CATEGORIE_AVATAR, categorie.getAvatar());

        SQLiteDatabase db = this.getWritableDatabase();

        // Inserting Row
        id=db.insert(DatabaseHelper.TABLE_CATEGORIE, null, values);
        // Closing database connection
        db.close();
        return id;
    }
    public Categorie getCategorie(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIE, new String[] { CATEGORIE_ID,
                         CATEGORIE_NAME, CATEGORIE_AVATAR}, CATEGORIE_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Categorie categorie = new Categorie(cursor.getInt(0),cursor.getString(1), cursor.getString(2));
        return categorie;
    }

    public Categorie getCategorieByName(String name) {
        Log.i(TAG, "MyDatabaseHelper.getCategorieByName ... " + name);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIE, new String[] { CATEGORIE_ID,
                        CATEGORIE_NAME, CATEGORIE_AVATAR}, CATEGORIE_NAME + "=?",
                new String[] { name }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Categorie categorie = new Categorie(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2));
        return categorie;
    }
    public List<Categorie> getAllCategories(){
        Log.i(TAG, "MyDatabaseHelper.getAllCategories ... " );

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIE;
        List<Categorie> categorieList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Categorie categorie = new Categorie(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2));
                // Adding site to list
                categorieList.add(categorie);
            } while (cursor.moveToNext());
        }
        return categorieList;
    }


    public void updateCategorie(Categorie categorie){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORIE_NAME, categorie.getNom());
        values.put(DatabaseHelper.CATEGORIE_AVATAR, categorie.getAvatar());

        this.db = this.getWritableDatabase();
        this.db.update(TABLE_CATEGORIE,values,CATEGORIE_ID + " = ?" , new String[]
                {String.valueOf(categorie.getId_categorie())});
    }

    public void deleteCategorie(int id) {
        this.db = this.getWritableDatabase();
        this.db.delete(TABLE_CATEGORIE, CATEGORIE_ID + " = ? ", new String[]
                {String.valueOf(id)});
    }
    public void setDefaultCategories(){

        int count = this.getCategoriesCount();
        if(count ==0 ) {
            //Add categories
            Categorie c1 = new Categorie(1, "Café & restaurants", "cat_cafe");
            Categorie c2 = new Categorie(2, "Santé", "cat_sante");
            Categorie c3 = new Categorie(3, "Supermarché", "cat_alimentation");
            Categorie c4 = new Categorie(4, "Monument", "cat_monument");
            Categorie c5 = new Categorie(5, "Shopping", "cat_shopping");
            Categorie c6 = new Categorie(6, "Hotel", "cat_hotel");

            addCategorie(c1);
            addCategorie(c2);
            addCategorie(c3);
            addCategorie(c4);
            addCategorie(c5);
            addCategorie(c6);
        }
    }
}

