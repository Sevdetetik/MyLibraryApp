package com.example.mylibraryapp.network;

import com.example.mylibraryapp.model.BookResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleBooksAPI {
    @GET("volumes")
    Call<BookResponse> searchBooks(@Query("q") String query);
}
