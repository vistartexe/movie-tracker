package com.example.movietracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.movietracker.LoginActivity;
import com.example.movietracker.model.User;

/** Mengelola sesi login dengan SharedPreferences. */
public class SessionManager {

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveLogin(User user) {
        prefs.edit()
                .putBoolean(Constants.PREF_IS_LOGGED_IN, true)
                .putInt(Constants.PREF_USER_ID, user.getId())
                .putString(Constants.PREF_USERNAME, user.getUsername())
                .putString(Constants.PREF_FULL_NAME, user.getFullName())
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.PREF_IS_LOGGED_IN, false)
                && prefs.getInt(Constants.PREF_USER_ID, -1) != -1;
    }

    public int getUserId() {
        return prefs.getInt(Constants.PREF_USER_ID, -1);
    }

    public String getUsername() {
        return prefs.getString(Constants.PREF_USERNAME, "");
    }

    public String getFullName() {
        return prefs.getString(Constants.PREF_FULL_NAME, "");
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    /**
     * Dipanggil jika sebuah Activity menerima Intent tanpa userId.
     * Pengguna dikembalikan ke halaman Login (bukan crash).
     */
    public static void redirectToLogin(Activity activity) {
        Toast.makeText(activity,
                "Data pengguna tidak ditemukan. Silakan login ulang.",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
