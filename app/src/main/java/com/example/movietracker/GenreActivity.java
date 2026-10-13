package com.example.movietracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movietracker.utils.Constants;
import com.example.movietracker.utils.SessionManager;

public class GenreActivity extends AppCompatActivity {

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = getIntent().getIntExtra(Constants.EXTRA_USER_ID, -1);
        if (userId == -1) {
            SessionManager.redirectToLogin(this);
            return;
        }

        setContentView(R.layout.activity_genre);

        findViewById(R.id.ibBack).setOnClickListener(v -> finish());

        findViewById(R.id.cardAction).setOnClickListener(v ->
                openMovieList(Constants.GENRE_ACTION));
        findViewById(R.id.cardComedy).setOnClickListener(v ->
                openMovieList(Constants.GENRE_COMEDY));
        findViewById(R.id.cardHorror).setOnClickListener(v ->
                openMovieList(Constants.GENRE_HORROR));
        findViewById(R.id.cardRomance).setOnClickListener(v ->
                openMovieList(Constants.GENRE_ROMANCE));
        findViewById(R.id.cardScifi).setOnClickListener(v ->
                openMovieList(Constants.GENRE_SCIFI));
    }

    /** ALUR 3: Genre -> Daftar Film. Mengirim genre (dan userId) lewat Intent. */
    private void openMovieList(String genre) {
        Intent intent = new Intent(this, MovieListActivity.class);
        intent.putExtra(Constants.EXTRA_GENRE, genre);
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        startActivity(intent);
    }
}
