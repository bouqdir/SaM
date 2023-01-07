package com.dm.sam.db.provider;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.dm.sam.db.service.SiteService;
import com.dm.sam.model.Site;

public class SiteProvider extends ContentProvider {

    // FOR DATA
    public static final String AUTHORITY = "com.dm.sam.db.provider";
    public static final String TABLE_NAME = Site.class.getSimpleName();

    // The site service
    private SiteService siteService;

    @Override
    public boolean onCreate() {
        this.siteService = SiteService.getInstance(getContext());

        return true;
    }

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (getContext() != null) {
            long id = ContentUris.parseId(uri);

            final Cursor cursor = this.siteService.getWithCursor(id);
            cursor.setNotificationUri(requireContext().getContentResolver(), uri);

            return cursor;
        }

        throw new IllegalArgumentException("Failed to query row for uri " + uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "vnd.android.cursor.site/" + AUTHORITY + "." + TABLE_NAME;
    }

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final long id;

        if (contentValues != null) {
            id = this.siteService.create(Site.fromContentValues(contentValues));

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
        final int count = this.siteService.delete(ContentUris.parseId(uri));

        requireContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @SuppressLint("NewApi")
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (contentValues != null) {
            final int count = this.siteService.update(Site.fromContentValues(contentValues));

            requireContext().getContentResolver().notifyChange(uri, null);

            return count;
        }
        throw new IllegalArgumentException("Failed to update row into " + uri);
    }
}
