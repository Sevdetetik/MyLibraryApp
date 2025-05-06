package com.example.mylibraryapp.network;

import com.example.mylibraryapp.model.BookResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleBooksAPI {
    @GET("volumes")
    Call<BookResponse> getBooks(
            @Query("q") String query,
            @Query("maxResults") int maxResults
    );
}
