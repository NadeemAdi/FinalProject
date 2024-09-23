package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * This class helps manage a local database for storing favorite news articles.
 * It handles creating the database, inserting articles, checking for duplicates, retrieving, and removing favorites.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Constants for database and table names, as well as column names
    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "favorites";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_DATE = "date";
    private static final String COL_LINK = "link";

    /**
     * Constructor to initialize the database helper.
     *
     * @param context The context in which the database is being used (usually the activity).
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method is called the first time the database is created.
     * It sets up the table to store favorite articles.
     *
     * @param db The SQLiteDatabase object.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_TITLE + " TEXT PRIMARY KEY, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_LINK + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    /**
     * This method is called when the database version changes (for example, when upgrading the app).
     * It will drop the old table and recreate a new one.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Adds a news article to the favorites database.
     * Before inserting, it checks if the article is already saved to avoid duplicates.
     *
     * @param title       The title of the article.
     * @param description A brief description of the article.
     * @param date        The date the article was published.
     * @param link        The link to the full article.
     * @return true if the article was added, false if it's already a favorite.
     */
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

    /**
     * Checks if an article is already saved as a favorite.
     *
     * @param title The title of the article.
     * @return true if the article is already in the favorites, false if not.
     */
    public boolean isFavorite(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TITLE + " = ?", new String[]{title});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     * Gets a list of all the articles saved as favorites.
     *
     * @return A list of NewsItem objects representing the favorite articles.
     */
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

    /**
     * Removes a specific article from the favorites list using the article's title.
     *
     * @param title The title of the article to be removed.
     */
    public void removeFavorite(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_TITLE + " = ?", new String[]{title});
    }

    /**
     * Clears all the favorite articles from the database.
     */
    public void clearFavorites() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);  // Deletes all rows from the favorites table
    }
}
