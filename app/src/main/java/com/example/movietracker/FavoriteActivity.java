package com.example.movietracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movietracker.adapter.MovieAdapter;
import com.example.movietracker.database.DatabaseHelper;
import com.example.movietracker.model.Movie;
import com.example.movietracker.utils.Constants;
import com.example.movietracker.utils.SessionManager;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private int userId;
    private DatabaseHelper databaseHelper;
    private MovieAdapter adapter;
    private ListView lvFavorites;
    private TextView tvEmptyFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = getIntent().getIntExtra(Constants.EXTRA_USER_ID, -1);
        if (userId == -1) {
            SessionManager.redirectToLogin(this);
            return;
        }

        setContentView(R.layout.activity_favorite);

        databaseHelper = new DatabaseHelper(this);

        lvFavorites = findViewById(R.id.lvFavorites);
        tvEmptyFavorites = findViewById(R.id.tvEmptyFavorites);

        findViewById(R.id.ibBack).setOnClickListener(v -> finish());

        // Favorit -> Detail Film (userId ikut dikirim)
        lvFavorites.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = adapter.getItem(position);

            Intent intent = new Intent(FavoriteActivity.this, MovieDetailActivity.class);
            intent.putExtra(Constants.EXTRA_MOVIE_ID, movie.getId());
            intent.putExtra(Constants.EXTRA_MOVIE_TITLE, movie.getTitle());
            intent.putExtra(Constants.EXTRA_MOVIE_GENRE, movie.getGenre());
            intent.putExtra(Constants.EXTRA_MOVIE_YEAR, movie.getYear());
            intent.putExtra(Constants.EXTRA_USER_ID, userId);
            startActivity(intent);
        });
    }

    // Dimuat di onResume: setelah pengguna menghapus favorit di halaman detail
    // lalu menekan Back, daftar ini otomatis diperbarui.
    @Override
    protected void onResume() {
        super.onResume();
        if (databaseHelper != null) {
            loadFavorites();
        }
    }

    private void loadFavorites() {
        List<Movie> favorites = databaseHelper.getFavoriteMovies(userId);

        if (adapter == null) {
            adapter = new MovieAdapter(this, favorites);
            lvFavorites.setAdapter(adapter);
        } else {
            adapter.updateData(favorites);
        }

        tvEmptyFavorites.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
