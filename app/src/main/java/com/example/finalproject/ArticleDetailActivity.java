package com.example.finalproject;

import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Activity to display details of a selected news article.
 * Users can view the article title, description, publication date, and link to the original article.
 * They can also add the article to their favorites.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private TextView titleTextView, descriptionTextView, dateTextView, linkTextView;
    private Button favoriteButton;
    private NewsItem newsItem;
    private DatabaseHelper myDb;

    /**
     * Called when the activity is first created.
     * Initializes the UI components, loads the selected theme, and sets up event listeners for the UI.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the selected theme before setting the content view
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // Initialize UI components
        titleTextView = findViewById(R.id.articleTitle);
        descriptionTextView = findViewById(R.id.articleDescription);
        dateTextView = findViewById(R.id.articleDate);
        linkTextView = findViewById(R.id.articleLink);
        favoriteButton = findViewById(R.id.favoriteButton);

        // Get the article data passed from MainActivity
        newsItem = (NewsItem) getIntent().getSerializableExtra("newsItem");
        myDb = new DatabaseHelper(this);

        // Populate UI with article details if available
        if (newsItem != null) {
            titleTextView.setText(newsItem.getTitle());
            descriptionTextView.setText(newsItem.getDescription());

            // Convert the date from the newsItem to EST and 12-hour format
            String formattedDate = formatDateToEST(newsItem.getDate());
            dateTextView.setText(formattedDate);  // Set formatted date

            // Set the article link
            linkTextView.setText(newsItem.getLink());

            // Make the link clickable and open in the browser
            linkTextView.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink()));
                startActivity(browserIntent);
            });
        }

        // Set up Favorite button click listener to add the article to the favorites database
        favoriteButton.setOnClickListener(v -> {
            boolean isFavorite = myDb.insertData(
                    newsItem.getTitle(),
                    newsItem.getDescription(),
                    newsItem.getDate(),
                    newsItem.getLink()
            );
            if (isFavorite) {
                Toast.makeText(ArticleDetailActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ArticleDetailActivity.this, "Already in Favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Loads the user's selected theme (either dark or light) from shared preferences.
     * This method is called before setting the content view to apply the selected theme.
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
     * Converts the date from its original GMT format to Eastern Standard Time (EST) and
     * formats it to a 12-hour format with AM/PM.
     *
     * @param dateString The original date string in GMT.
     * @return The formatted date string in EST and 12-hour format, or the original string if parsing fails.
     */
    private String formatDateToEST(String dateString) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        originalFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat targetFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.ENGLISH);
        targetFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        try {
            Date date = originalFormat.parse(dateString);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
}
