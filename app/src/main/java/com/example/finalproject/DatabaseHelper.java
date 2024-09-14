package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "favorites";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_DATE = "date";
    private static final String COL_LINK = "link";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_TITLE + " TEXT PRIMARY KEY, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_LINK + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a favorite article into the database, avoiding duplicates
    public boolean insertData(String title, String description, String date, String link) {
        if (!isFavorite(title)) {  // Check if article is already a favorite
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_TITLE, title);
            contentValues.put(COL_DESCRIPTION, description);
            contentValues.put(COL_DATE, date);
            contentValues.put(COL_LINK, link);
            long result = db.insert(TABLE_NAME, null, contentValues);
            return result != -1;  // returns false if insert fails
        } else {
            return false;  // Article is already in favorites
        }
    }

    // Check if an article is already marked as a favorite
    public boolean isFavorite(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TITLE + " = ?", new String[]{title});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Get all favorite articles
    public ArrayList<NewsItem> getFavoriteArticles() {
        ArrayList<NewsItem> favoritesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                String link = cursor.getString(cursor.getColumnIndexOrThrow(COL_LINK));

                favoritesList.add(new NewsItem(title, description, date, link));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoritesList;
    }

    // Remove a favorite article from the database
    public void removeFavorite(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_TITLE + " = ?", new String[]{title});
    }

    // Clear all favorite articles from the database
    public void clearFavorites() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);  // Deletes all rows from the favorites table
    }
}
