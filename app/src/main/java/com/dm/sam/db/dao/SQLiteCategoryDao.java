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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SQLiteCategoryDao extends SQLiteDao<Categorie> implements ServiceDAO<Categorie> {

    @SuppressLint("StaticFieldLeak")
    private static SQLiteCategoryDao instance;

    private static final String[] allColumns = { DatabaseHelper.CATEGORIE_ID,DatabaseHelper.CATEGORIE_NAME, DatabaseHelper.CATEGORIE_AVATAR };

    public SQLiteCategoryDao(Context context) {
        super(context);

    }

    public static SQLiteCategoryDao getInstance(Context context) {
        if (instance == null)
            instance = new SQLiteCategoryDao(context);

        return instance;
    }

    @Override
    public long create(Categorie category) {
        openWritable();

        ContentValues values = putContentValues(category);

        long lastInsertedId = sqLiteDatabase.insert(DatabaseHelper.TABLE_CATEGORIE,
                null,
                values);

        close();

        return lastInsertedId;
    }

    @Override
    public int update(Categorie category) {
        openWritable();

        ContentValues values = putContentValues(category);

        int returnedId = sqLiteDatabase.update(DatabaseHelper.TABLE_CATEGORIE, values, DatabaseHelper.CATEGORIE_ID + " = ?", new String[] { String.valueOf(category.getId_categorie()) });

        return returnedId;
    }

    @Override
    public int delete(long id) {
        openWritable();

        int returnedId = sqLiteDatabase.delete(DatabaseHelper.TABLE_CATEGORIE,
                DatabaseHelper.CATEGORIE_ID + " = ?",
                new String[] { String.valueOf(id) });

        close();

        return returnedId;
    }

    @Override
    public Categorie findById(long id) {
        openReadable();

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_CATEGORIE,
                allColumns,
                DatabaseHelper.CATEGORIE_ID + " = ?",
                new String[] { String.valueOf(id) },
                null, null, null, null);

        cursor.moveToFirst();

        Categorie category = cursorToObject(cursor);

        cursor.close();

        close();

        return category;
    }

    public Categorie findByName(String name) {
        openReadable();
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_CATEGORIE, allColumns,DatabaseHelper.CATEGORIE_NAME + "=?" +"COLLATE NOCASE" ,
                new String[] { name }, null, null, null, null);


       if( cursor.moveToFirst() ){

           Categorie c=  cursorToObject(cursor);

           cursor.close();
           close();

           return c;
       }

        return null;
    }
    @Override
    public List<Categorie> findAll() {
        openReadable();

        List<Categorie> categories = new LinkedList<>();

        String query = "SELECT  * FROM " + DatabaseHelper.TABLE_CATEGORIE;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        Categorie category;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            category = cursorToObject(cursor);
            categories.add(category);

            cursor.moveToNext();
        }
        cursor.close();

        close();

        return categories;
    }

    @Override
    public Cursor getWithCursor(long id) {
        return sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIE + " WHERE id = ?",
                new String[] { String.valueOf(id) });
    }

    @Override
    public Categorie cursorToObject(Cursor cursor) {
        return new Categorie(cursor.getInt(0),cursor.getString(1), cursor.getString(2));
    }

    private ContentValues putContentValues(Categorie categorie) {
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.CATEGORIE_ID, categorie.getId_categorie());
        values.put(DatabaseHelper.CATEGORIE_NAME, categorie.getNom());
        values.put(DatabaseHelper.CATEGORIE_AVATAR, categorie.getAvatar());

        return values;
    }

    public int getCategoriesCount() {
        openReadable();
        String countQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_CATEGORIE;
        Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();
        return count;
    }


    public void setDefaultCategories(){
        openWritable();

        int count = this.getCategoriesCount();
        if(count ==0 ) {
            //Add categories
            Categorie c1 = new Categorie(1, "Café & restaurants", "cat_cafe");
            Categorie c2 = new Categorie(2, "Santé", "cat_sante");
            Categorie c3 = new Categorie(3, "Supermarché", "cat_alimentation");
            Categorie c4 = new Categorie(4, "Monument", "cat_monument");
            Categorie c5 = new Categorie(5, "Shopping", "cat_shopping");
            Categorie c6 = new Categorie(6, "Hotel", "cat_hotel");

            create(c1);
            create(c2);
            create(c3);
            create(c4);
            create(c5);
            create(c6);
        }
    }
}
