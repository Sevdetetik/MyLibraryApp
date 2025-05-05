package com.example.mylibraryapp.model;

public class FavoriteBook {
    public String id;
    public String title;
    public String authors;
    public String thumbnail;

    // Boş constructor (Firebase için gereklidir)
    public FavoriteBook() {}

    public FavoriteBook(String id, String title, String authors, String thumbnail) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.thumbnail = thumbnail;
    }
}
