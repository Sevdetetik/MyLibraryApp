package com.example.mylibraryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mylibraryapp.R;

public class AddBookActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        EditText editText = findViewById(R.id.editTextBookName);
        Button buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> {
            String bookName = editText.getText().toString().trim();
            if (!bookName.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("book_name", bookName);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                editText.setError("Kitap adı boş olamaz!");
            }
        });
    }
}
