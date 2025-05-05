package com.example.mylibraryapp.fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.mylibraryapp.R;
import com.example.mylibraryapp.adapter.BookAdapter;
import com.example.mylibraryapp.model.Book;

import org.json.*;

import java.util.*;

public class BookListFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;
    private EditText editTextSearch;
    private ImageButton btnSearch;

    private final String GOOGLE_BOOKS_API = "https://www.googleapis.com/books/v1/volumes?q=";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        btnSearch = view.findViewById(R.id.btnSearch);

        bookList = new ArrayList<>();
        adapter = new BookAdapter(requireContext(), bookList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                fetchBooks(query);
            } else {
                Toast.makeText(getContext(), "Lütfen bir anahtar kelime girin", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void fetchBooks(String query) {
        String url = GOOGLE_BOOKS_API + query.replace(" ", "+");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray items = response.getJSONArray("items");
                        bookList.clear();

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                            String title = volumeInfo.optString("title", "Bilinmiyor");
                            JSONArray authorsArray = volumeInfo.optJSONArray("authors");
                            String authors = (authorsArray != null) ? authorsArray.join(", ").replace("\"", "") : "Yazar Bilinmiyor";

                            String thumbnail = "";
                            if (volumeInfo.has("imageLinks")) {
                                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                                thumbnail = imageLinks.optString("thumbnail", "");
                            }

                            bookList.add(new Book(title, authors, thumbnail));
                        }

                        adapter.updateList(bookList);

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Veri ayrıştırılırken hata oluştu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "API hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
