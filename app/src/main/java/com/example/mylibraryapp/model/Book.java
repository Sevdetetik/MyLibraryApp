// model/Book.java
package com.example.mylibraryapp.model;

public class Book {
    private String title;
    private String authors;
    private String thumbnail;
    private String id; // Firebase için benzersiz ID

    public Book() {
        // Boş constructor gerekli
    }

    public Book(String title, String authors, String thumbnail) {
        this.title = title;
        this.authors = authors;
        this.thumbnail = thumbnail;
        this.id = title + "_" + authors; // Basit bir ID üretimi
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

    public String getId() {
        return id;
    }
}
