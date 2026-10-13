package com.example.movietracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movietracker.database.DatabaseHelper;
import com.example.movietracker.model.Movie;
import com.example.movietracker.utils.Constants;
import com.example.movietracker.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class MovieDetailActivity extends AppCompatActivity {

    private int userId;
    private int movieId;
    private DatabaseHelper databaseHelper;

    private ImageView ivMoviePoster;
    private TextView tvMovieTitle, tvMovieGenre, tvMovieYear, tvAverageRating;
    private TextView tvMovieSynopsis, tvUserRating;
    private MaterialButton btnFavorite, btnSubmitRating, btnViewFavorites;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Terima data dari MovieListActivity / FavoriteActivity
        userId = getIntent().getIntExtra(Constants.EXTRA_USER_ID, -1);
        movieId = getIntent().getIntExtra(Constants.EXTRA_MOVIE_ID, -1);
        String titleExtra = getIntent().getStringExtra(Constants.EXTRA_MOVIE_TITLE);
        String genreExtra = getIntent().getStringExtra(Constants.EXTRA_MOVIE_GENRE);
        int yearExtra = getIntent().getIntExtra(Constants.EXTRA_MOVIE_YEAR, 0);

        if (userId == -1) {
            SessionManager.redirectToLogin(this);
            return;
        }
        if (movieId == -1) {
            Toast.makeText(this, "Data film tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_movie_detail);

        databaseHelper = new DatabaseHelper(this);

        ivMoviePoster = findViewById(R.id.ivMoviePoster);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvMovieGenre = findViewById(R.id.tvMovieGenre);
        tvMovieYear = findViewById(R.id.tvMovieYear);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvMovieSynopsis = findViewById(R.id.tvMovieSynopsis);
        tvUserRating = findViewById(R.id.tvUserRating);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnSubmitRating = findViewById(R.id.btnSubmitRating);
        btnViewFavorites = findViewById(R.id.btnViewFavorites);
        ratingBar = findViewById(R.id.ratingBar);

        // Tampilkan dulu data yang dikirim lewat Intent
        if (titleExtra != null) {
            tvMovieTitle.setText(titleExtra);
        }
        if (genreExtra != null) {
            tvMovieGenre.setText(genreExtra);
        }
        if (yearExtra != 0) {
            tvMovieYear.setText(String.valueOf(yearExtra));
        }

        // Ambil data lengkap (sinopsis, poster, rating) dari SQLite berdasarkan movieId
        Movie movie = databaseHelper.getMovieById(movieId);
        if (movie == null) {
            Toast.makeText(this, "Film tidak ditemukan di database",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        showMovie(movie);
        refreshFavoriteButton();
        refreshUserRating();

        findViewById(R.id.ibBack).setOnClickListener(v -> finish());
        btnFavorite.setOnClickListener(v -> toggleFavorite());
        btnSubmitRating.setOnClickListener(v -> submitRating());

        // ALUR 5: Detail -> Favorit
        btnViewFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MovieDetailActivity.this, FavoriteActivity.class);
            intent.putExtra(Constants.EXTRA_USER_ID, userId);
            startActivity(intent);
        });
    }

    private void showMovie(Movie movie) {
        tvMovieTitle.setText(movie.getTitle());
        tvMovieGenre.setText(movie.getGenre());
        tvMovieYear.setText(String.valueOf(movie.getYear()));
        tvMovieSynopsis.setText(movie.getSynopsis());

        int resId = getResources()
                .getIdentifier(movie.getPoster(), "drawable", getPackageName());
        if (resId == 0) {
            resId = R.drawable.poster_action;
        }
        ivMoviePoster.setImageResource(resId);

        showAverageRating(movie);
    }

    private void showAverageRating(Movie movie) {
        if (movie.getRatingCount() > 0) {
            tvAverageRating.setText(String.format(Locale.getDefault(),
                    "★ %.1f (%d rating)", movie.getAverageRating(), movie.getRatingCount()));
        } else {
            tvAverageRating.setText("Belum ada rating dari pengguna");
        }
    }

    // ----- Favorit -----

    private void toggleFavorite() {
        boolean currentlyFavorite = databaseHelper.isFavorite(userId, movieId);
        boolean success;

        if (currentlyFavorite) {
            success = databaseHelper.removeFavorite(userId, movieId);
            if (success) {
                Toast.makeText(this, "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
            }
        } else {
            success = databaseHelper.addFavorite(userId, movieId);
            if (success) {
                Toast.makeText(this, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show();
            }
        }

        if (!success) {
            Toast.makeText(this, "Gagal mengubah favorit. Coba lagi.",
                    Toast.LENGTH_LONG).show();
        }

        // Tampilan tombol selalu mengikuti kondisi terbaru di database
        refreshFavoriteButton();
    }

    private void refreshFavoriteButton() {
        if (databaseHelper.isFavorite(userId, movieId)) {
            btnFavorite.setText("Remove from Favorites");
        } else {
            btnFavorite.setText("Add to Favorites");
        }
    }

    // ----- Rating -----

    private void submitRating() {
        int score = (int) ratingBar.getRating();

        if (score < 1) {
            Toast.makeText(this, "Pilih minimal 1 bintang dulu",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = databaseHelper.saveRating(userId, movieId, score);

        if (success) {
            Toast.makeText(this, "Rating berhasil disimpan!", Toast.LENGTH_SHORT).show();
            refreshUserRating();

            // Hitung ulang rata-rata dari semua pengguna
            Movie updated = databaseHelper.getMovieById(movieId);
            if (updated != null) {
                showAverageRating(updated);
            }
        } else {
            Toast.makeText(this, "Gagal menyimpan rating. Coba lagi.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void refreshUserRating() {
        int myRating = databaseHelper.getUserRating(userId, movieId);

        if (myRating > 0) {
            ratingBar.setRating(myRating);
            tvUserRating.setText("Rating kamu: " + myRating + " / 5");
        } else {
            ratingBar.setRating(0);
            tvUserRating.setText("Kamu belum memberi rating");
        }
    }
}
