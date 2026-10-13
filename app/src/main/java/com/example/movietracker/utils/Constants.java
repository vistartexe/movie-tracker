package com.example.movietracker.utils;

public class Constants {

    // ===== Key Intent Extras =====
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_FULL_NAME = "fullName";
    public static final String EXTRA_GENRE = "genre";
    public static final String EXTRA_MOVIE_ID = "movieId";
    public static final String EXTRA_MOVIE_TITLE = "movieTitle";
    public static final String EXTRA_MOVIE_GENRE = "movieGenre";
    public static final String EXTRA_MOVIE_YEAR = "movieYear";

    // ===== SharedPreferences =====
    public static final String PREFS_NAME = "movie_picker_prefs";
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_FULL_NAME = "full_name";

    // ===== Nilai genre =====
    public static final String GENRE_ACTION = "Action";
    public static final String GENRE_COMEDY = "Comedy";
    public static final String GENRE_HORROR = "Horror";
    public static final String GENRE_ROMANCE = "Romance";
    public static final String GENRE_SCIFI = "Sci-Fi";

    private Constants() {
        // Kelas utilitas, tidak perlu dibuat objeknya
    }
}
