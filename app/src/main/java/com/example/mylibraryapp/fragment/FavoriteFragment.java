package com.example.mylibraryapp.fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.mylibraryapp.R;
import com.example.mylibraryapp.adapter.BookAdapter;
import com.example.mylibraryapp.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> favoriteBooks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteBooks = new ArrayList<>();

        // Kitap tıklama olayını ekleyerek adapter'ı oluştur
        bookAdapter = new BookAdapter(getContext(), favoriteBooks, book -> {
            // Kitap tıklandığında yapılacak işlem
            Toast.makeText(getContext(), "Favori Kitap: " + book.getTitle(), Toast.LENGTH_SHORT).show();
        }, true); // Burada 'true' parametresi FAVORİLER SAYFASINDAN geldiğimizi belirtir

        recyclerView.setAdapter(bookAdapter);

        loadFavoriteBooks();

        return view;
    }

    private void loadFavoriteBooks() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Giriş yapılmamış", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference favRef = FirebaseDatabase.getInstance()
                .getReference("Favorites")
                .child(user.getUid());

        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteBooks.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Book book = dataSnapshot.getValue(Book.class);
                    if (book != null) {
                        book.setFavorite(true); // Favori olduğunu belirt
                        favoriteBooks.add(book);
                    }
                }
                bookAdapter.updateList(favoriteBooks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Favoriler yüklenemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
