package com.example.finalproject;

import java.io.Serializable;

/**
 * This class represents a news article with a title, description, publication date, and link.
 * It implements Serializable so that it can be passed between activities.
 */
public class NewsItem implements Serializable {
    private String title;
    private String description;
    private String date;
    private String link;

    /**
     * Default constructor to create an empty NewsItem.
     */
    public NewsItem() {}

    /**
     * Constructor to create a NewsItem with all the details.
     *
     * @param title       The title of the news article.
     * @param description A brief description of the news article.
     * @param date        The date the news article was published.
     * @param link        The link to the full news article.
     */
    public NewsItem(String title, String description, String date, String link) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
    }

    /**
     * Gets the title of the news article.
     *
     * @return The title of the article.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the description of the news article.
     *
     * @return The description of the article.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the publication date of the news article.
     *
     * @return The publication date of the article.
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the link to the full news article.
     *
     * @return The link to the article.
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the title of the news article.
     *
     * @param title The new title of the article.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the description of the news article.
     *
     * @param description The new description of the article.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the publication date of the news article.
     *
     * @param date The new date of the article.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Sets the link to the full news article.
     *
     * @param link The new link to the article.
     */
    public void setLink(String link) {
        this.link = link;
    }
}
