package com.example.mylibraryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase'den mevcut kullanıcıyı kontrol et
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Kullanıcı giriş yaptıysa MainActivity'ye yönlendir
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Giriş yapılmamışsa LoginActivity'ye yönlendir
            startActivity(new Intent(this, LoginActivity.class));
        }

        // LauncherActivity'yi sonlandır
        finish();
    }
}
