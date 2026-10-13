package com.example.movietracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.movietracker.model.Movie;
import com.example.movietracker.model.User;
import com.example.movietracker.utils.Constants;
import com.example.movietracker.utils.PasswordUtil;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "movie_picker.db";
    private static final int DB_VERSION = 1;

    // ===== Nama tabel =====
    private static final String TABLE_USERS = "users";
    private static final String TABLE_MOVIES = "movies";
    private static final String TABLE_FAVORITES = "favorites";
    private static final String TABLE_RATINGS = "ratings";

    // ===== Hasil registrasi =====
    public static final int REGISTER_SUCCESS = 0;
    public static final int REGISTER_USERNAME_TAKEN = 1;
    public static final int REGISTER_EMAIL_TAKEN = 2;
    public static final int REGISTER_FAILED = 3;

    // Query dasar untuk mengambil film + rata-rata rating (dipakai berulang)
    private static final String MOVIE_SELECT =
            "SELECT m.id, m.title, m.genre, m.year, m.synopsis, m.poster, "
                    + "COALESCE(AVG(r.score), 0) AS avg_rating, "
                    + "COUNT(r.id) AS rating_count "
                    + "FROM movies m "
                    + "LEFT JOIN ratings r ON r.movie_id = m.id ";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Aktifkan foreign key (di SQLite bawaan Android defaultnya mati)
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "full_name TEXT NOT NULL, "
                + "username TEXT NOT NULL UNIQUE COLLATE NOCASE, "
                + "email TEXT NOT NULL UNIQUE COLLATE NOCASE, "
                + "password_hash TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_MOVIES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "genre TEXT NOT NULL, "
                + "year INTEGER NOT NULL, "
                + "synopsis TEXT NOT NULL, "
                + "poster TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "movie_id INTEGER NOT NULL, "
                + "UNIQUE (user_id, movie_id), "
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, "
                + "FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE " + TABLE_RATINGS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "movie_id INTEGER NOT NULL, "
                + "score INTEGER NOT NULL CHECK (score BETWEEN 1 AND 5), "
                + "updated_at INTEGER NOT NULL, "
                + "UNIQUE (user_id, movie_id), "
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, "
                + "FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE)");

        insertInitialMovies(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Untuk tugas kuliah: hapus semua tabel lalu buat ulang.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // =====================================================================
    // DATA AWAL FILM (15 film, masing-masing genre 3 film)
    // =====================================================================

    private void insertInitialMovies(SQLiteDatabase db) {
        // ----- Action -----
        addMovie(db, "Mad Max: Fury Road", Constants.GENRE_ACTION, 2015,
                "Di gurun pasca-kiamat, Furiosa melarikan diri bersama lima perempuan "
                        + "dari penguasa tiran. Max terseret dalam pengejaran penuh ledakan "
                        + "yang berlangsung hampir sepanjang film.", "poster_action");
        addMovie(db, "John Wick", Constants.GENRE_ACTION, 2014,
                "Seorang mantan pembunuh bayaran pensiun dan hidup tenang, hingga "
                        + "kehilangan sesuatu yang berharga. Ia kembali ke dunia lamanya "
                        + "untuk membalas dendam.", "poster_action");
        addMovie(db, "The Dark Knight", Constants.GENRE_ACTION, 2008,
                "Batman, Jim Gordon, dan Harvey Dent berusaha membersihkan Gotham dari "
                        + "kejahatan, namun muncul Joker yang ingin menjerumuskan kota "
                        + "ke dalam kekacauan.", "poster_action");

        // ----- Comedy -----
        addMovie(db, "Superbad", Constants.GENRE_COMEDY, 2007,
                "Dua sahabat SMA berusaha mendapatkan minuman untuk pesta sebelum lulus. "
                        + "Rencana sederhana itu berubah menjadi petualangan kacau semalaman.",
                "poster_comedy");
        addMovie(db, "The Hangover", Constants.GENRE_COMEDY, 2009,
                "Tiga sahabat terbangun setelah pesta lajang di Las Vegas tanpa ingat "
                        + "apa pun, dan calon pengantin pria menghilang.", "poster_comedy");
        addMovie(db, "Home Alone", Constants.GENRE_COMEDY, 1990,
                "Seorang anak laki-laki tak sengaja tertinggal di rumah saat keluarganya "
                        + "berlibur, lalu harus menjaga rumah dari dua pencuri.",
                "poster_comedy");

        // ----- Horror -----
        addMovie(db, "Get Out", Constants.GENRE_HORROR, 2017,
                "Seorang fotografer mengunjungi keluarga pacarnya di pedesaan. "
                        + "Perlahan ia menyadari ada hal mengerikan di balik keramahan "
                        + "mereka.", "poster_horror");
        addMovie(db, "The Conjuring", Constants.GENRE_HORROR, 2013,
                "Pasangan penyelidik paranormal membantu sebuah keluarga yang rumah "
                        + "barunya diganggu kekuatan gelap.", "poster_horror");
        addMovie(db, "A Quiet Place", Constants.GENRE_HORROR, 2018,
                "Sebuah keluarga bertahan hidup dalam keheningan total karena makhluk "
                        + "buas memburu setiap sumber suara.", "poster_horror");

        // ----- Romance -----
        addMovie(db, "Titanic", Constants.GENRE_ROMANCE, 1997,
                "Kisah cinta Jack dan Rose, dua penumpang dari kelas sosial berbeda, "
                        + "di atas kapal mewah yang ditakdirkan karam.", "poster_romance");
        addMovie(db, "The Notebook", Constants.GENRE_ROMANCE, 2004,
                "Seorang lelaki membacakan kisah cinta masa muda kepada seorang "
                        + "perempuan tua yang mulai kehilangan ingatannya.",
                "poster_romance");
        addMovie(db, "La La Land", Constants.GENRE_ROMANCE, 2016,
                "Seorang pianis jazz dan calon aktris bertemu di Los Angeles, saling "
                        + "jatuh cinta, dan diuji oleh impian masing-masing.",
                "poster_romance");

        // ----- Sci-Fi -----
        addMovie(db, "Inception", Constants.GENRE_SCIFI, 2010,
                "Seorang pencuri yang menyusup ke dalam mimpi mendapat tugas "
                        + "menanamkan sebuah ide ke benak target.", "poster_scifi");
        addMovie(db, "Interstellar", Constants.GENRE_SCIFI, 2014,
                "Saat Bumi tak lagi layak huni, sekelompok astronot menembus lubang "
                        + "cacing untuk mencari rumah baru bagi umat manusia.",
                "poster_scifi");
        addMovie(db, "The Matrix", Constants.GENRE_SCIFI, 1999,
                "Seorang peretas mengetahui bahwa dunia yang ia kenal ternyata "
                        + "simulasi, dan ia terlibat dalam perlawanan terhadap mesin.",
                "poster_scifi");
    }

    private void addMovie(SQLiteDatabase db, String title, String genre, int year,
                          String synopsis, String poster) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("genre", genre);
        values.put("year", year);
        values.put("synopsis", synopsis);
        values.put("poster", poster);
        db.insert(TABLE_MOVIES, null, values);
    }

    // =====================================================================
    // USERS
    // =====================================================================

    /** Mendaftarkan user baru. Mengembalikan salah satu konstanta REGISTER_*. */
    public int registerUser(String fullName, String username, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();

        if (isValueExists(db, "username", username)) {
            return REGISTER_USERNAME_TAKEN;
        }
        if (isValueExists(db, "email", email)) {
            return REGISTER_EMAIL_TAKEN;
        }

        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("username", username);
        values.put("email", email);
        values.put("password_hash", PasswordUtil.hashPassword(password));

        long result = db.insert(TABLE_USERS, null, values);
        return (result == -1) ? REGISTER_FAILED : REGISTER_SUCCESS;
    }

    // Nama kolom hanya berasal dari kode kita (bukan input user), nilainya pakai tanda "?"
    private boolean isValueExists(SQLiteDatabase db, String column, String value) {
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS + " WHERE " + column
                        + " = ? COLLATE NOCASE LIMIT 1",
                new String[]{value});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    /** Login dengan username ATAU email. Mengembalikan User, atau null jika gagal. */
    public User loginUser(String usernameOrEmail, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, full_name, username, email, password_hash FROM " + TABLE_USERS
                        + " WHERE username = ? COLLATE NOCASE OR email = ? COLLATE NOCASE LIMIT 1",
                new String[]{usernameOrEmail, usernameOrEmail});

        User user = null;
        if (cursor.moveToFirst()) {
            String storedHash = cursor.getString(4);
            if (PasswordUtil.verifyPassword(password, storedHash)) {
                user = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3));
            }
        }
        cursor.close();
        return user;
    }

    // =====================================================================
    // MOVIES
    // =====================================================================

    /** Mengambil semua film dalam satu genre, lengkap dengan rata-rata rating. */
    public List<Movie> getMoviesByGenre(String genre) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                MOVIE_SELECT + "WHERE m.genre = ? GROUP BY m.id ORDER BY m.title ASC",
                new String[]{genre});
        return readMovieList(cursor);
    }

    /** Mengambil satu film berdasarkan ID. Mengembalikan null jika tidak ditemukan. */
    public Movie getMovieById(int movieId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                MOVIE_SELECT + "WHERE m.id = ? GROUP BY m.id",
                new String[]{String.valueOf(movieId)});
        Movie movie = null;
        if (cursor.moveToFirst()) {
            movie = cursorToMovie(cursor);
        }
        cursor.close();
        return movie;
    }

    private List<Movie> readMovieList(Cursor cursor) {
        List<Movie> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(cursorToMovie(cursor));
        }
        cursor.close();
        return list;
    }

    private Movie cursorToMovie(Cursor cursor) {
        return new Movie(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("genre")),
                cursor.getInt(cursor.getColumnIndexOrThrow("year")),
                cursor.getString(cursor.getColumnIndexOrThrow("synopsis")),
                cursor.getString(cursor.getColumnIndexOrThrow("poster")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("avg_rating")),
                cursor.getInt(cursor.getColumnIndexOrThrow("rating_count")));
    }

    // =====================================================================
    // FAVORITES
    // =====================================================================

    public boolean isFavorite(int userId, int movieId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_FAVORITES
                        + " WHERE user_id = ? AND movie_id = ? LIMIT 1",
                new String[]{String.valueOf(userId), String.valueOf(movieId)});
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    /** Menambahkan favorit. Mengembalikan true jika berhasil. */
    public boolean addFavorite(int userId, int movieId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("movie_id", movieId);
        long result = db.insertWithOnConflict(
                TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        // -1 bisa berarti sudah ada (ignore) atau gagal; cek ulang agar akurat
        return result != -1 || isFavorite(userId, movieId);
    }

    /** Menghapus favorit. Mengembalikan true jika ada baris yang terhapus. */
    public boolean removeFavorite(int userId, int movieId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_FAVORITES, "user_id = ? AND movie_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(movieId)});
        return rows > 0;
    }

    /** Mengambil semua film favorit milik satu user. */
    public List<Movie> getFavoriteMovies(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                MOVIE_SELECT
                        + "INNER JOIN favorites f ON f.movie_id = m.id "
                        + "WHERE f.user_id = ? GROUP BY m.id ORDER BY m.title ASC",
                new String[]{String.valueOf(userId)});
        return readMovieList(cursor);
    }

    // =====================================================================
    // RATINGS
    // =====================================================================

    /** Rating milik user untuk film tertentu. Mengembalikan 0 jika belum pernah memberi rating. */
    public int getUserRating(int userId, int movieId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT score FROM " + TABLE_RATINGS
                        + " WHERE user_id = ? AND movie_id = ? LIMIT 1",
                new String[]{String.valueOf(userId), String.valueOf(movieId)});
        int score = 0;
        if (cursor.moveToFirst()) {
            score = cursor.getInt(0);
        }
        cursor.close();
        return score;
    }

    /**
     * Menyimpan rating. Jika user sudah pernah memberi rating untuk film ini,
     * rating lama diperbarui (UPDATE), jika belum maka dibuat baru (INSERT).
     */
    public boolean saveRating(int userId, int movieId, int score) {
        if (score < 1 || score > 5) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("score", score);
        values.put("updated_at", System.currentTimeMillis());

        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_RATINGS
                        + " WHERE user_id = ? AND movie_id = ? LIMIT 1",
                new String[]{String.valueOf(userId), String.valueOf(movieId)});
        boolean alreadyRated = cursor.moveToFirst();
        cursor.close();

        if (alreadyRated) {
            int rows = db.update(TABLE_RATINGS, values, "user_id = ? AND movie_id = ?",
                    new String[]{String.valueOf(userId), String.valueOf(movieId)});
            return rows > 0;
        } else {
            values.put("user_id", userId);
            values.put("movie_id", movieId);
            long result = db.insert(TABLE_RATINGS, null, values);
            return result != -1;
        }
    }
}
