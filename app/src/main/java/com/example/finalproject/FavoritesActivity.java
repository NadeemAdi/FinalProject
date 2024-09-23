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

public class FavoritesActivity extends AppCompatActivity {

    private ArrayList<NewsItem> favoriteArticles;
    private FavoriteAdapter adapter;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the selected theme before setting the content view
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Button clearButton = findViewById(R.id.clearFavoritesButton);
        clearButton.setOnClickListener(v ->showDeleteAllConfirmationDialog());

        // Initialize database and fetch favorites
        myDb = new DatabaseHelper(this);
        favoriteArticles = myDb.getFavoriteArticles();

        // Set up the ListView and adapter
        ListView favoritesListView = findViewById(R.id.favoritesListView);
        adapter = new FavoriteAdapter(this, favoriteArticles);
        favoritesListView.setAdapter(adapter);
    }

    // Load the selected theme
    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        if (isDarkTheme) {
            setTheme(R.style.Theme_FinalProject_Dark); // Use dark theme
        } else {
            setTheme(R.style.Theme_FinalProject); // Use light theme
        }
    }

    // Add showDeleteConfirmationDialog method here
    public void showDeleteConfirmationDialog(NewsItem article, int position) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.remove_favorite_title))
                .setMessage(getString(R.string.remove_favorite_confirmation))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    myDb.removeFavorite(article.getTitle());  // Remove from DB
                    favoriteArticles.remove(position);  // Remove from list
                    adapter.notifyDataSetChanged();  // Refresh adapter
                    Toast.makeText(this, getString(R.string.article_removed_message), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    public void showDeleteAllConfirmationDialog() {
        if (favoriteArticles.isEmpty()) {
            // Show a toast if there are no favorites
            Toast.makeText(this, getString(R.string.no_favorites_message), Toast.LENGTH_SHORT).show();
        } else {
            // Show confirmation dialog if there are favorites
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.clear_favorites_title))
                    .setMessage(getString(R.string.clear_favorites_confirmation))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        myDb.clearFavorites();  // Remove from DB
                        favoriteArticles.clear(); // Clear the list in memory
                        adapter.notifyDataSetChanged(); // Refresh the ListView
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
    }

}
