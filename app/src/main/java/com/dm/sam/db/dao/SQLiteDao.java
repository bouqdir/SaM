package com.dm.sam.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.dm.sam.db.DatabaseHelper;

public abstract class SQLiteDao<T> {

    protected SQLiteDatabase sqLiteDatabase;
    protected DatabaseHelper databaseHelper;
    protected Context context;

    public SQLiteDao(Context context) {
        this.context = context;
        this.databaseHelper = DatabaseHelper.getInstance(this.context);
    }

    /*
        Ecrire dans la bdd
     */
    public void openWritable() throws SQLException {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
    }

    /*
        Lire dans la bdd
     */
    public void openReadable() throws SQLException {
        sqLiteDatabase = databaseHelper.getReadableDatabase();
    }

    public void close() {
        databaseHelper.close();
    }

    public abstract T cursorToObject(Cursor cursor);
}
