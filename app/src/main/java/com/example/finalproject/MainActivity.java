package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * MainActivity displays a list of news articles fetched from an RSS feed.
 * Users can view articles, search for specific ones, refresh the feed, and navigate to the article details or favorites.
 */
public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private Button refreshButton;
    private EditText searchEditText;
    private ArrayAdapter<String> adapter;
    private ArrayList<NewsItem> newsList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    /**
     * Called when the activity is created.
     * It sets up the toolbar, list, and various buttons, and starts fetching news from the RSS feed.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the selected theme before setting the content view
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        // Initialize UI components
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        refreshButton = findViewById(R.id.refreshButton);
        searchEditText = findViewById(R.id.searchEditText);

        // Initialize SharedPreferences for saving data like the last viewed article
        sharedPreferences = getSharedPreferences("NewsAppPrefs", MODE_PRIVATE);

        // Set up the ListView with an ArrayAdapter for displaying news titles
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        // Fetch news articles from the RSS feed
        new FetchRSSFeedTask().execute("https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");

        // Handle ListView item clicks to open article details
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, ArticleDetailActivity.class);
            intent.putExtra("newsItem", newsList.get(position));

            // Save the last viewed article in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lastViewedTitle", newsList.get(position).getTitle());
            editor.apply();

            startActivity(intent);
        });

        // Handle refresh button click to fetch the latest news articles
        refreshButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Refreshing news...", Toast.LENGTH_SHORT).show();
            new FetchRSSFeedTask().execute("https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");
        });

        // Set up search functionality to filter news titles based on user input
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    /**
     * Loads the selected theme (dark or light) from the user's preferences.
     * This method is called before setting the content view to apply the theme.
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
     * AsyncTask to fetch news articles from an RSS feed in the background.
     * It parses the feed and updates the ListView with article titles.
     */
    private class FetchRSSFeedTask extends AsyncTask<String, Void, ArrayList<NewsItem>> {

        /**
         * Shows the progress bar before fetching the news articles.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        /**
         * Fetches the RSS feed, parses the XML, and builds a list of news articles.
         *
         * @param urls The URL of the RSS feed to fetch.
         * @return A list of NewsItem objects representing the articles.
         */
        @Override
        protected ArrayList<NewsItem> doInBackground(String... urls) {
            ArrayList<NewsItem> result = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
                InputStream inputStream = url.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(inputStream, null);

                int eventType = parser.getEventType();
                NewsItem currentItem = null;
                boolean insideItem = false;

                // Parse the XML data
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                            currentItem = new NewsItem();
                        } else if (insideItem && parser.getName().equalsIgnoreCase("title")) {
                            currentItem.setTitle(parser.nextText());
                        } else if (insideItem && parser.getName().equalsIgnoreCase("description")) {
                            currentItem.setDescription(parser.nextText());
                        } else if (insideItem && parser.getName().equalsIgnoreCase("pubDate")) {
                            currentItem.setDate(parser.nextText());
                        } else if (insideItem && parser.getName().equalsIgnoreCase("link")) {
                            currentItem.setLink(parser.nextText());
                        }
                    } else if (eventType == XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase("item")) {
                        result.add(currentItem);
                        insideItem = false;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                Log.e("RSSFeed", "Error fetching RSS feed", e);
                runOnUiThread(() -> Snackbar.make(findViewById(R.id.mainLayout), "Failed to load news articles.", Snackbar.LENGTH_LONG).show());
            }
            return result;
        }

        /**
         * Called when the fetching is done.
         * Updates the ListView with the news article titles.
         *
         * @param result The list of news articles fetched from the RSS feed.
         */
        @Override
        protected void onPostExecute(ArrayList<NewsItem> result) {
            progressBar.setVisibility(ProgressBar.GONE);
            newsList.clear();
            newsList.addAll(result);
            adapter.clear();
            for (NewsItem item : result) {
                adapter.add(item.getTitle());
            }
        }
    }

    /**
     * Inflates the menu with options like Help, Favorites, and Settings.
     *
     * @param menu The options menu.
     * @return true if the menu was created successfully.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Handles the actions when menu items are selected.
     *
     * @param item The selected menu item.
     * @return true if the action was handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        } else if (id == R.id.action_favorites) {
            // Open FavoritesActivity
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            // Open SettingsActivity (if you have one)
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows a Help dialog with information about how to use the app.
     */
    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.help))
                .setMessage(getString(R.string.help_message))
                .setPositiveButton(getString(R.string.ok), null)
                .show();
    }
}
