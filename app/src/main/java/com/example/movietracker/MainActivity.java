package com.example.movietracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movietracker.utils.Constants;
import com.example.movietracker.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Terima data dari LoginActivity
        userId = getIntent().getIntExtra(Constants.EXTRA_USER_ID, -1);
        String username = getIntent().getStringExtra(Constants.EXTRA_USERNAME);
        String fullName = getIntent().getStringExtra(Constants.EXTRA_FULL_NAME);

        // Pemeriksaan: jika userId tidak ada, kembali ke Login
        if (userId == -1) {
            SessionManager.redirectToLogin(this);
            return;
        }

        setContentView(R.layout.activity_main);

        // Sapaan memakai kata pertama dari nama lengkap, misalnya "Andi"
        String displayName = username;
        if (fullName != null && !fullName.trim().isEmpty()) {
            displayName = fullName.trim().split("\\s+")[0];
        }
        if (displayName == null) {
            displayName = "";
        }

        TextView tvGreeting = findViewById(R.id.tvGreeting);
        tvGreeting.setText("Halo, " + displayName + "!");

        // ALUR 2: Home -> Genre
        findViewById(R.id.btnPickMovie).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GenreActivity.class);
            intent.putExtra(Constants.EXTRA_USER_ID, userId);
            startActivity(intent);
        });

        // Home -> Favorit
        findViewById(R.id.btnMyFavorites).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
            intent.putExtra(Constants.EXTRA_USER_ID, userId);
            startActivity(intent);
        });

        // Logout: hapus sesi, kembali ke Login, kosongkan tumpukan Activity
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            new SessionManager(MainActivity.this).logout();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
