package com.example.mylibraryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mylibraryapp.R;
import com.example.mylibraryapp.fragment.HomeFragment;
import com.example.mylibraryapp.fragment.FavoriteFragment;
import com.example.mylibraryapp.fragment.SearchFragment; // ✅ ekledik
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Oturum kontrolü
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = getSelectedFragment(item.getItemId());
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, selectedFragment)
                        .commit();
            }
            return true;
        });

        // İlk açılışta HomeFragment göster
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, new HomeFragment())
                    .commit();
        }
    }

    private Fragment getSelectedFragment(int itemId) {
        if (itemId == R.id.menu_home) {
            return new HomeFragment();
        } else if (itemId == R.id.menu_search) { // ✅ ara menüsünü kontrol ediyoruz
            return new SearchFragment();
        } else if (itemId == R.id.menu_favorites) {
            return new FavoriteFragment();
        }
        return null;
    }
}
