package com.example.movietracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movietracker.adapter.MovieAdapter;
import com.example.movietracker.database.DatabaseHelper;
import com.example.movietracker.model.Movie;
import com.example.movietracker.utils.Constants;
import com.example.movietracker.utils.SessionManager;

import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    private int userId;
    private String genre;
    private DatabaseHelper databaseHelper;
    private MovieAdapter adapter;
    private ListView lvMovies;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Terima data dari GenreActivity
        userId = getIntent().getIntExtra(Constants.EXTRA_USER_ID, -1);
        genre = getIntent().getStringExtra(Constants.EXTRA_GENRE);

        if (userId == -1) {
            SessionManager.redirectToLogin(this);
            return;
        }
        if (genre == null || genre.trim().isEmpty()) {
            Toast.makeText(this, "Genre tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_movie_list);

        databaseHelper = new DatabaseHelper(this);

        TextView tvScreenTitle = findViewById(R.id.tvScreenTitle);
        tvScreenTitle.setText("Film " + genre);

        lvMovies = findViewById(R.id.lvMovies);
        tvEmpty = findViewById(R.id.tvEmpty);

        findViewById(R.id.ibBack).setOnClickListener(v -> finish());

        // ALUR 4: Daftar Film -> Detail Film
        lvMovies.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = adapter.getItem(position);

            Intent intent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
            intent.putExtra(Constants.EXTRA_MOVIE_ID, movie.getId());
            intent.putExtra(Constants.EXTRA_MOVIE_TITLE, movie.getTitle());
            intent.putExtra(Constants.EXTRA_MOVIE_GENRE, movie.getGenre());
            intent.putExtra(Constants.EXTRA_MOVIE_YEAR, movie.getYear());
            intent.putExtra(Constants.EXTRA_USER_ID, userId);
            startActivity(intent);
        });
    }

    // Dimuat di onResume agar rata-rata rating ikut diperbarui
    // setelah pengguna memberi rating di halaman detail lalu kembali.
    @Override
    protected void onResume() {
        super.onResume();
        if (databaseHelper != null) {
            loadMovies();
        }
    }

    private void loadMovies() {
        List<Movie> movies = databaseHelper.getMoviesByGenre(genre);

        if (adapter == null) {
            adapter = new MovieAdapter(this, movies);
            lvMovies.setAdapter(adapter);
        } else {
            adapter.updateData(movies);
        }

        tvEmpty.setVisibility(movies.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
