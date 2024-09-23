package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * This class is a custom adapter to display a list of favorite news articles.
 * It helps show each article in the list along with a button to delete the article from the favorites.
 */
public class FavoriteAdapter extends ArrayAdapter<NewsItem> {

    private ArrayList<NewsItem> favoriteArticles;
    private Context context;

    /**
     * Constructor to initialize the adapter with the list of favorite articles and the current context (e.g., activity).
     *
     * @param context  The activity or fragment where the adapter is used.
     * @param articles The list of favorite news articles to display.
     */
    public FavoriteAdapter(Context context, ArrayList<NewsItem> articles) {
        super(context, 0, articles);
        this.context = context;
        this.favoriteArticles = articles;
    }

    /**
     * Creates the view for each favorite article item in the list.
     * This method is called for each item in the list to display its content and handle button clicks.
     *
     * @param position    The position of the article in the list.
     * @param convertView The old view that can be reused (if not null), otherwise a new view is created.
     * @param parent      The parent view that this view will be attached to (the ListView).
     * @return The view for the current article in the list.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // If there is no existing view, create a new one by inflating the favorite_item layout
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.favorite_item, parent, false);
        }

        // Get the current article from the list based on its position
        NewsItem article = getItem(position);

        // Get the TextView and delete button from the layout
        TextView articleTitle = convertView.findViewById(R.id.articleTitle);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        // Set the article title in the TextView if the article is not null
        if (article != null) {
            articleTitle.setText(article.getTitle());
        }

        // Set up a click listener on the view to open the article details when clicked
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra("newsItem", article);  // Pass the selected article to the details page
            context.startActivity(intent);
        });

        // Set up the delete button to remove the article from favorites
        deleteButton.setOnClickListener(v -> {
            if (context instanceof FavoritesActivity) {
                // Show a confirmation dialog before deleting the article
                ((FavoritesActivity) context).showDeleteConfirmationDialog(article, position);
            }
        });

        return convertView;
    }
}
