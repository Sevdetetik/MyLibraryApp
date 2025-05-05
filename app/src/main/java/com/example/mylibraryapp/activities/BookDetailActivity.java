package com.example.mylibraryapp.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mylibraryapp.R;

public class BookDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        TextView textViewBookName = findViewById(R.id.textViewBookName);
        String bookName = getIntent().getStringExtra("book_name");
        textViewBookName.setText(bookName);
    }
}

