package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class FavoriteAdapter extends ArrayAdapter<NewsItem> {

    private ArrayList<NewsItem> favoriteArticles;
    private Context context;

    public FavoriteAdapter(Context context, ArrayList<NewsItem> articles) {
        super(context, 0, articles);
        this.context = context;
        this.favoriteArticles = articles;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.favorite_item, parent, false);
        }

        NewsItem article = getItem(position);
        TextView articleTitle = convertView.findViewById(R.id.articleTitle);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        // Set article title
        articleTitle.setText(article.getTitle());

        // Set up click listener to open the article details
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra("newsItem", article);  // Pass the selected article
            context.startActivity(intent);
        });

        // Handle delete button click
        deleteButton.setOnClickListener(v -> {
            // Cast the context to FavoritesActivity to access the delete method
            if (context instanceof FavoritesActivity) {
                ((FavoritesActivity) context).showDeleteConfirmationDialog(article, position);
            }
        });

        return convertView;
    }
}
