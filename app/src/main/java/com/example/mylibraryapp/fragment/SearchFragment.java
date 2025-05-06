package com.example.mylibraryapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylibraryapp.R;
import com.example.mylibraryapp.activities.BookDetailActivity;
import com.example.mylibraryapp.adapter.BookAdapter;
import com.example.mylibraryapp.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import org.json.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchFragment extends Fragment {

    private EditText editTextSearch;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private BookAdapter adapter;
    private final List<Book> bookList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        editTextSearch = view.findViewById(R.id.editTextSearch);
        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        progressBar = view.findViewById(R.id.progressBar);

        adapter = new BookAdapter(requireContext(), bookList, book -> {
            Intent intent = new Intent(getContext(), BookDetailActivity.class);
            intent.putExtra("book", book);
            startActivity(intent);
        }, false); // 🔍 Favori sayfası değil

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (!keyword.isEmpty()) {
                    fetchBooks(keyword);
                } else {
                    bookList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    private void fetchBooks(String keyword) {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + URLEncoder.encode(keyword, "UTF-8"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                reader.close();
                connection.disconnect();

                JSONObject root = new JSONObject(jsonBuilder.toString());
                JSONArray items = root.optJSONArray("items");
                if (items == null) return;

                List<Book> tempList = new ArrayList<>();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject volume = items.getJSONObject(i);
                    JSONObject volumeInfo = volume.getJSONObject("volumeInfo");

                    String title = volumeInfo.optString("title", "Başlıksız");
                    String authors = "";
                    if (volumeInfo.has("authors")) {
                        JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                        authors = authorsArray.join(", ").replace("\"", "");
                    }
                    String description = volumeInfo.optString("description", "Açıklama yok");
                    String thumbnail = "";
                    if (volumeInfo.has("imageLinks")) {
                        thumbnail = volumeInfo.getJSONObject("imageLinks").optString("thumbnail", "");
                    }

                    Book book = new Book(title, authors, thumbnail, description);
                    tempList.add(book);
                }

                // 🔥 Favori kontrolü
                FirebaseDatabase.getInstance()
                        .getReference("Favorites")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (Book book : tempList) {
                                    if (snapshot.hasChild(book.getId())) {
                                        book.setFavorite(true);
                                    }
                                }

                                requireActivity().runOnUiThread(() -> {
                                    bookList.clear();
                                    bookList.addAll(tempList);
                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Favoriler alınamadı", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                });
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }
        });
    }
}
