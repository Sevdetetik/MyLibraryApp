package com.example.mylibraryapp.model;

import java.io.Serializable;

public class Book implements Serializable {
    private String id;
    private String title;
    private String authors;
    private String thumbnail;
    private String description;
    private boolean favorite; // <-- YENİ EKLENDİ

    public Book() {}

    public Book(String title, String authors, String thumbnail) {
        this.title = title;
        this.authors = authors;
        this.thumbnail = thumbnail;
    }

    public Book(String title, String authors, String thumbnail, String description) {
        this.title = title;
        this.authors = authors;
        this.thumbnail = thumbnail;
        this.description = description;
    }

    public String getId() {
        if (id != null) return id;

        // Geçersiz karakterleri kaldır veya değiştir
        String rawId = (title + "_" + authors)
                .replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "")
                .replace("/", "_");

        return rawId;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getDescription() {
        return description != null ? description : "Açıklama bulunamadı.";
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
