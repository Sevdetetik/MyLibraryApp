package com.example.mylibraryapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
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
    private final String GOOGLE_BOOKS_API = "https://www.googleapis.com/books/v1/volumes?q=";
    private Handler handler = new Handler();
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        editTextSearch = view.findViewById(R.id.editTextSearch);

        bookList = new ArrayList<>();
        adapter = new BookAdapter(requireContext(), bookList, book -> {
            Toast.makeText(getContext(), "Seçilen Kitap: " + book.getTitle(), Toast.LENGTH_SHORT).show();
        }, false); // Bu sayfa favoriler sayfası değil

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Arama kutusuna yazıldıkça kitapları API'den çek
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    String query = charSequence.toString().trim();
                    if (!query.isEmpty()) {
                        fetchBooks(query);
                    }
                };
                handler.postDelayed(searchRunnable, 500); // 500ms bekleme
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return view;
    }

    private void fetchBooks(String query) {
        String url = GOOGLE_BOOKS_API + query.replace(" ", "+");
        bookList.clear();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray items = response.getJSONArray("items");

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                            String title = volumeInfo.optString("title", "Başlık Yok");

                            JSONArray authorsArray = volumeInfo.optJSONArray("authors");
                            String authors = (authorsArray != null) ?
                                    authorsArray.join(", ").replace("\"", "") : "Yazar Bilgisi Yok";

                            String thumbnail = "";
                            if (volumeInfo.has("imageLinks")) {
                                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                                thumbnail = imageLinks.optString("thumbnail", "");
                                if (thumbnail.startsWith("http:")) {
                                    thumbnail = thumbnail.replace("http:", "https:");
                                }
                            }

                            String description = volumeInfo.optString("description", "Açıklama bulunamadı.");
                            Book book = new Book(title, authors, thumbnail, description);
                            bookList.add(book);
                        }

                        adapter.updateList(bookList);

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Veri ayrıştırılırken hata oluştu", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "API hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
