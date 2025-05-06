package com.example.mylibraryapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mylibraryapp.R;
import com.example.mylibraryapp.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class BookDetailActivity extends AppCompatActivity {

    private ImageView imageViewCover;
    private TextView textViewTitle, textViewAuthor, textViewDescription;  // Yeni textView
    private Button btnAddFavorite;
    private Book book;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        imageViewCover = findViewById(R.id.imageViewDetailCover);
        textViewTitle = findViewById(R.id.textViewDetailTitle);
        textViewAuthor = findViewById(R.id.textViewDetailAuthor);
        textViewDescription = findViewById(R.id.textViewDetailDescription);  // Yeni textView
        btnAddFavorite = findViewById(R.id.btnAddFavorite);

        // Kitap nesnesini intent'ten al
        book = (Book) getIntent().getSerializableExtra("book");

        if (book != null) {
            textViewTitle.setText(book.getTitle());
            textViewAuthor.setText(book.getAuthors());
            textViewDescription.setText(book.getDescription());  // Kitap açıklamasını göster

            Glide.with(this)
                    .load(book.getThumbnail())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(imageViewCover);
        }

        btnAddFavorite.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Lütfen giriş yapın", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference favRef = FirebaseDatabase.getInstance()
                    .getReference("Favorites")
                    .child(user.getUid())
                    .child(book.getId());

            favRef.setValue(book)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Favorilere eklendi", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Favori eklenemedi", Toast.LENGTH_SHORT).show());
        });
    }
}
