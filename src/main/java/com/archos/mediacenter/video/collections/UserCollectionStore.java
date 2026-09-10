// Copyright 2026
// Licensed under the Apache License, Version 2.0
package com.archos.mediacenter.video.collections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.archos.mediaprovider.video.ScraperStore;

import java.util.ArrayList;
import java.util.List;

/** Small, UI-independent client for persistent user collections in MediaLib. */
public final class UserCollectionStore {
    private UserCollectionStore() { }

    public static final class Collection {
        public final long id;
        public final String name;

        private Collection(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static List<Collection> getVisibleCollections(Context context) {
        List<Collection> collections = new ArrayList<>();
        String[] projection = { ScraperStore.UserCollections.ID, ScraperStore.UserCollections.NAME };
        String selection = ScraperStore.UserCollections.HIDDEN + "=0";
        try (Cursor cursor = context.getContentResolver().query(ScraperStore.UserCollections.URI.BASE,
                projection, selection, null, ScraperStore.UserCollections.POSITION + ", " +
                        ScraperStore.UserCollections.SORT_NAME + " COLLATE LOCALIZED")) {
            if (cursor == null) return collections;
            int idColumn = cursor.getColumnIndexOrThrow(ScraperStore.UserCollections.ID);
            int nameColumn = cursor.getColumnIndexOrThrow(ScraperStore.UserCollections.NAME);
            while (cursor.moveToNext()) {
                collections.add(new Collection(cursor.getLong(idColumn), cursor.getString(nameColumn)));
            }
        }
        return collections;
    }

    public static Uri create(Context context, String name) {
        String trimmedName = name == null ? "" : name.trim();
        if (trimmedName.isEmpty()) throw new IllegalArgumentException("A collection name is required");
        ContentValues values = new ContentValues();
        values.put(ScraperStore.UserCollections.NAME, trimmedName);
        values.put(ScraperStore.UserCollections.SORT_NAME, trimmedName);
        return context.getContentResolver().insert(ScraperStore.UserCollections.URI.BASE, values);
    }

    public static boolean contains(Context context, long collectionId, int mediaType, long mediaId) {
        String selection = ScraperStore.UserCollectionItems.MEDIA_TYPE + "=? AND " +
                ScraperStore.UserCollectionItems.MEDIA_ID + "=?";
        String[] args = { String.valueOf(mediaType), String.valueOf(mediaId) };
        Uri uri = Uri.withAppendedPath(ScraperStore.UserCollectionItems.URI.BY_COLLECTION_ID,
                String.valueOf(collectionId));
        try (Cursor cursor = context.getContentResolver().query(uri,
                new String[] { ScraperStore.UserCollectionItems.ID }, selection, args, null)) {
            return cursor != null && cursor.moveToFirst();
        }
    }

    public static void setMembership(Context context, long collectionId, int mediaType, long mediaId,
                                     boolean included) {
        Uri uri = ScraperStore.UserCollectionItems.URI.BASE;
        String selection = ScraperStore.UserCollectionItems.COLLECTION_ID + "=? AND " +
                ScraperStore.UserCollectionItems.MEDIA_TYPE + "=? AND " +
                ScraperStore.UserCollectionItems.MEDIA_ID + "=?";
        String[] args = { String.valueOf(collectionId), String.valueOf(mediaType), String.valueOf(mediaId) };
        if (!included) {
            context.getContentResolver().delete(uri, selection, args);
            return;
        }
        ContentValues values = new ContentValues();
        values.put(ScraperStore.UserCollectionItems.COLLECTION_ID, collectionId);
        values.put(ScraperStore.UserCollectionItems.MEDIA_TYPE, mediaType);
        values.put(ScraperStore.UserCollectionItems.MEDIA_ID, mediaId);
        context.getContentResolver().insert(uri, values);
    }
}
