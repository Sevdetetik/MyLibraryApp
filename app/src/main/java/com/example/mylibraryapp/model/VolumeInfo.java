package com.example.mylibraryapp.model;

import java.util.List;

public class VolumeInfo {
    private String title;
    private String subtitle;
    private List<String> authors;
    private String description;
    private ImageLinks imageLinks;

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public ImageLinks getImageLinks() {
        return imageLinks;
    }
}
