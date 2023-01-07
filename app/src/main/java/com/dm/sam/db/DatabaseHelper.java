package com.dm.sam.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance = null;
    public static final String DATABASE_NAME = "sam.db";
    public static final int DATABASE_VERSION = 1;
    public  static final String TABLE_SITE = "Site";
    public  static final String SITE_ID = "id_site";
    public static final String SITE_NOM = "nom";
    public  static final String SITE_LATITUDE = "latitude";
    public   static final String SITE_LONGITUDE = "longitude";
    public  static final String SITE_CODE_POSTAL = "code_postal";
    public  static final String SITE_CATEGORIE = "categorie";
    public  static final String SITE_RESUME = "resume";

    public   static final String CREATE_SITE_TABLE = "CREATE TABLE " + TABLE_SITE
            + " (" + SITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SITE_NOM + " TEXT NOT NULL, "
            + SITE_LATITUDE + " REAL NOT NULL, "
            + SITE_LONGITUDE + " REAL NOT NULL, "
            + SITE_CODE_POSTAL + " INTEGER, "
            + SITE_CATEGORIE + " INTEGERT, "
            + SITE_RESUME + " TEXT);";

    public  static final String TABLE_CATEGORIE = "Categorie";
    public   static final String CATEGORIE_ID = "id_categorie";
    public  static final String CATEGORIE_NAME = "nom";
    public static final String CATEGORIE_AVATAR = "avatar";

    public  static final String CREATE_CATEGORIE_TABLE = "CREATE TABLE " + TABLE_CATEGORIE
            + " ("+ CATEGORIE_ID + " INTEGER PRIMARY KEY, "
            + CATEGORIE_NAME + " TEXT NOT NULL, "
            + CATEGORIE_AVATAR + " TEXT );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context);

        return instance;
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

}

