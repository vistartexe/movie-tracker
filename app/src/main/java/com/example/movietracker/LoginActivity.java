package com.example.movietracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movietracker.database.DatabaseHelper;
import com.example.movietracker.model.User;
import com.example.movietracker.utils.Constants;
import com.example.movietracker.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsernameOrEmail, tilPassword;
    private TextInputEditText etUsernameOrEmail, etPassword;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Jika sesi login masih aktif, langsung ke halaman utama
        if (sessionManager.isLoggedIn()) {
            goToMain(sessionManager.getUserId(),
                    sessionManager.getUsername(),
                    sessionManager.getFullName());
            return;
        }

        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        tilUsernameOrEmail = findViewById(R.id.tilUsernameOrEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etUsernameOrEmail = findViewById(R.id.etUsernameOrEmail);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(v -> attemptLogin());

        // Tombol "Done" di keyboard juga menjalankan login
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        findViewById(R.id.btnGoRegister).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        tilUsernameOrEmail.setError(null);
        tilPassword.setError(null);

        String input = etUsernameOrEmail.getText() == null
                ? "" : etUsernameOrEmail.getText().toString().trim();
        String password = etPassword.getText() == null
                ? "" : etPassword.getText().toString();

        boolean valid = true;
        if (input.isEmpty()) {
            tilUsernameOrEmail.setError("Username atau email wajib diisi");
            valid = false;
        }
        if (password.isEmpty()) {
            tilPassword.setError("Password wajib diisi");
            valid = false;
        }
        if (!valid) {
            return;
        }

        User user = databaseHelper.loginUser(input, password);

        if (user == null) {
            Toast.makeText(this, "Username/email atau password salah",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Simpan sesi, lalu kirim data pengguna ke MainActivity
        sessionManager.saveLogin(user);
        goToMain(user.getId(), user.getUsername(), user.getFullName());
    }

    /** ALUR 1: Login -> Home. Mengirim userId, username, dan fullName lewat Intent. */
    private void goToMain(int userId, String username, String fullName) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        intent.putExtra(Constants.EXTRA_USERNAME, username);
        intent.putExtra(Constants.EXTRA_FULL_NAME, fullName);
        startActivity(intent);
        finish(); // agar tombol Back dari Home tidak kembali ke Login
    }
}
