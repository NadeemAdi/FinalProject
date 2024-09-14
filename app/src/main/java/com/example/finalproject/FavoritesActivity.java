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
                .setTitle("Remove Favorite")
                .setMessage("Are you sure you want to remove this article from favorites?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    myDb.removeFavorite(article.getTitle());  // Remove from DB
                    favoriteArticles.remove(position);  // Remove from list
                    adapter.notifyDataSetChanged();  // Refresh adapter
                    Toast.makeText(this, "Article removed from favorites", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
