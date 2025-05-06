package com.example.mylibraryapp.fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylibraryapp.R;
import com.example.mylibraryapp.adapter.BookAdapter;
import com.example.mylibraryapp.model.Book;
import com.example.mylibraryapp.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerTrending, recyclerBestsellers;
    private BookAdapter trendingAdapter, bestsellersAdapter;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerTrending = view.findViewById(R.id.recyclerViewTrendings);
        recyclerBestsellers = view.findViewById(R.id.recyclerViewBestsellers);

        recyclerTrending.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerBestsellers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Tıklama dinleyicisi tanımlanıyor
        BookAdapter.OnBookClickListener onBookClickListener = book ->
                Toast.makeText(getContext(), "Tıklanan kitap: " + book.getTitle(), Toast.LENGTH_SHORT).show();

        // Adapterlar oluşturuluyor — 4. parametre: isFavoritesPage = false
        trendingAdapter = new BookAdapter(getContext(), new ArrayList<>(), onBookClickListener, false);
        bestsellersAdapter = new BookAdapter(getContext(), new ArrayList<>(), onBookClickListener, false);

        recyclerTrending.setAdapter(trendingAdapter);
        recyclerBestsellers.setAdapter(bestsellersAdapter);

        loadBooks();

        return view;
    }

    private void loadBooks() {
        // Trend kitaplar
        ApiClient.getBooks(getContext(), "bestsellers", new ApiClient.OnBooksFetchedListener() {
            @Override
            public void onSuccess(List<Book> books) {
                trendingAdapter.updateList(books);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Trend kitaplar yüklenemedi", Toast.LENGTH_SHORT).show();
            }
        });

        // Yeni kitaplar (Bestsellers olarak alınıyor)
        ApiClient.getBooks(getContext(), "new+books", new ApiClient.OnBooksFetchedListener() {
            @Override
            public void onSuccess(List<Book> books) {
                bestsellersAdapter.updateList(books);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Bestsellers yüklenemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
