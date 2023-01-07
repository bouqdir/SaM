package com.dm.sam.db.provider;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.dm.sam.db.service.CategorieService;
import com.dm.sam.model.Categorie;


public class CategoryProvider extends ContentProvider {

    // FOR DATA
    public static final String AUTHORITY = "com.dm.sam.db.provider";
    public static final String TABLE_NAME = Categorie.class.getSimpleName();

    // The site service
    private CategorieService categorieService;

    @Override
    public boolean onCreate() {
        this.categorieService = CategorieService.getInstance(getContext());

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (getContext() != null) {
            long id = ContentUris.parseId(uri);

            final Cursor cursor = this.categorieService.getWithCursor(id);
            cursor.setNotificationUri(requireContext().getContentResolver(), uri);

            return cursor;
        }

        throw new IllegalArgumentException("Failed to query row for uri " + uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "vnd.android.cursor.category/" + AUTHORITY + "." + TABLE_NAME;
    }

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final long id;

        if (contentValues != null) {
            id = this.categorieService.create(Categorie.fromContentValues(contentValues));

            if (id != 0) {
                requireContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            }
        }

        throw new IllegalArgumentException("Failed to insert row into " + uri);
    }

    @SuppressLint("NewApi")
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int count = this.categorieService.delete(ContentUris.parseId(uri));

        requireContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @SuppressLint("NewApi")
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (contentValues != null) {
            final int count = this.categorieService.update(Categorie.fromContentValues(contentValues));

            requireContext().getContentResolver().notifyChange(uri, null);

            return count;
        }

        throw new IllegalArgumentException("Failed to update row into " + uri);
    }
}
