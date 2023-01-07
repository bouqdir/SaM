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

    /**
     * This method gives us writing access in the database
     */
    public void openWritable() throws SQLException {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
    }

    /**
     * This method gives us reading access in the database
     */
    public void openReadable() throws SQLException {
        sqLiteDatabase = databaseHelper.getReadableDatabase();
    }
    /**
     * This method closes the database connection
     */
    public void close() {
        databaseHelper.close();
    }
    /**
     * This method converts the given cursor to an object
     */
    public abstract T cursorToObject(Cursor cursor);
}
