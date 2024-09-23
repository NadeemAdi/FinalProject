package com.example.finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/**
 * This activity displays a list of favorite news articles saved by the user.
 * It allows the user to remove individual articles or clear all favorites.
 */
public class FavoritesActivity extends AppCompatActivity {

    private ArrayList<NewsItem> favoriteArticles;
    private FavoriteAdapter adapter;
    private DatabaseHelper myDb;

    /**
     * Called when the activity is created.
     * Loads the theme, sets up the list of favorite articles, and handles the clear button.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the selected theme before setting the content view
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Set up the "Clear All" button and its click listener
        Button clearButton = findViewById(R.id.clearFavoritesButton);
        clearButton.setOnClickListener(v -> showDeleteAllConfirmationDialog());

        // Initialize the database and fetch the list of favorite articles
        myDb = new DatabaseHelper(this);
        favoriteArticles = myDb.getFavoriteArticles();

        // Set up the ListView to display the favorite articles
        ListView favoritesListView = findViewById(R.id.favoritesListView);
        adapter = new FavoriteAdapter(this, favoriteArticles);
        favoritesListView.setAdapter(adapter);
    }

    /**
     * Loads the selected theme (dark or light) based on user preferences stored in SharedPreferences.
     */
    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        if (isDarkTheme) {
            setTheme(R.style.Theme_FinalProject_Dark); // Use dark theme
        } else {
            setTheme(R.style.Theme_FinalProject); // Use light theme
        }
    }

    /**
     * Shows a confirmation dialog to ask the user if they want to remove a specific favorite article.
     * If the user confirms, the article is deleted from both the database and the list.
     *
     * @param article  The NewsItem to be removed.
     * @param position The position of the article in the list.
     */
    public void showDeleteConfirmationDialog(NewsItem article, int position) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.remove_favorite_title))  // Title of the dialog
                .setMessage(getString(R.string.remove_favorite_confirmation))  // Confirmation message
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    myDb.removeFavorite(article.getTitle());  // Remove the article from the database
                    favoriteArticles.remove(position);  // Remove the article from the list
                    adapter.notifyDataSetChanged();  // Refresh the ListView
                    Toast.makeText(this, getString(R.string.article_removed_message), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.no), null)  // Do nothing if "No" is selected
                .show();
    }

    /**
     * Shows a confirmation dialog to ask the user if they want to clear all favorite articles.
     * If the user confirms, all favorites are deleted from the database and the list is cleared.
     */
    public void showDeleteAllConfirmationDialog() {
        if (favoriteArticles.isEmpty()) {
            // If no favorites are saved, show a message to the user
            Toast.makeText(this, getString(R.string.no_favorites_message), Toast.LENGTH_SHORT).show();
        } else {
            // Show a confirmation dialog if there are favorites to delete
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.clear_favorites_title))  // Title of the dialog
                    .setMessage(getString(R.string.clear_favorites_confirmation))  // Confirmation message
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        myDb.clearFavorites();  // Clear all favorites from the database
                        favoriteArticles.clear();  // Clear the list of favorite articles in memory
                        adapter.notifyDataSetChanged();  // Refresh the ListView
                    })
                    .setNegativeButton(getString(R.string.no), null)  // Do nothing if "No" is selected
                    .show();
        }
    }
}
