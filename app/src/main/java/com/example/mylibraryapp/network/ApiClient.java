package com.example.mylibraryapp.network;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.mylibraryapp.model.Book;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/";

    public static void getBooks(Context context, String query, final OnBooksFetchedListener listener) {
        String url = BASE_URL + "volumes?q=" + query;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray items = response.getJSONArray("items");
                        List<Book> books = new ArrayList<>();

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject volume = items.getJSONObject(i);
                            JSONObject volumeInfo = volume.getJSONObject("volumeInfo");

                            String title = volumeInfo.optString("title", "No Title");
                            String authors = volumeInfo.has("authors")
                                    ? volumeInfo.getJSONArray("authors").join(", ").replace("\"", "")
                                    : "Unknown Author";
                            String thumbnail = volumeInfo.has("imageLinks")
                                    ? volumeInfo.getJSONObject("imageLinks").optString("thumbnail", "")
                                    : "";
                            String description = volumeInfo.optString("description", "Açıklama yok");

                            Book book = new Book(title, authors, thumbnail);
                            book.setId(volume.optString("id"));
                            book.setDescription(description);

                            books.add(book);
                        }

                        listener.onSuccess(books);
                    } catch (Exception e) {
                        listener.onFailure(e);
                    }
                },
                error -> listener.onFailure(error)
        );

        queue.add(request);
    }

    public interface OnBooksFetchedListener {
        void onSuccess(List<Book> books);
        void onFailure(Exception e);
    }
}
