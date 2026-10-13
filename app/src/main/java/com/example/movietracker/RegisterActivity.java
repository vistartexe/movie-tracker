package com.example.movietracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movietracker.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFullName, tilUsername, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etUsername, etEmail, etPassword, etConfirmPassword;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        tilFullName = findViewById(R.id.tilFullName);
        tilUsername = findViewById(R.id.tilUsername);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        MaterialButton btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> attemptRegister());
        findViewById(R.id.tvGoLogin).setOnClickListener(v -> goToLogin());
    }

    private void attemptRegister() {
        // Hapus pesan error sebelumnya
        tilFullName.setError(null);
        tilUsername.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String fullName = getText(etFullName);
        String username = getText(etUsername);
        String email = getText(etEmail);
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString();
        String confirm = etConfirmPassword.getText() == null
                ? "" : etConfirmPassword.getText().toString();

        // ----- Validasi input -----
        boolean valid = true;

        if (fullName.isEmpty()) {
            tilFullName.setError("Nama lengkap wajib diisi");
            valid = false;
        }

        if (username.isEmpty()) {
            tilUsername.setError("Username wajib diisi");
            valid = false;
        } else if (!username.matches("[A-Za-z0-9_.]{3,20}")) {
            tilUsername.setError("3-20 karakter: huruf, angka, titik, atau garis bawah");
            valid = false;
        }

        if (email.isEmpty()) {
            tilEmail.setError("Email wajib diisi");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Format email tidak valid");
            valid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Password wajib diisi");
            valid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password minimal 6 karakter");
            valid = false;
        }

        if (confirm.isEmpty()) {
            tilConfirmPassword.setError("Konfirmasi password wajib diisi");
            valid = false;
        } else if (!password.equals(confirm)) {
            tilConfirmPassword.setError("Password tidak cocok");
            valid = false;
        }

        if (!valid) {
            return;
        }

        // ----- Simpan ke database -----
        int result = databaseHelper.registerUser(fullName, username, email, password);

        switch (result) {
            case DatabaseHelper.REGISTER_SUCCESS:
                Toast.makeText(this, "Registrasi berhasil! Silakan login.",
                        Toast.LENGTH_LONG).show();
                goToLogin();
                break;
            case DatabaseHelper.REGISTER_USERNAME_TAKEN:
                tilUsername.setError("Username sudah digunakan");
                break;
            case DatabaseHelper.REGISTER_EMAIL_TAKEN:
                tilEmail.setError("Email sudah terdaftar");
                break;
            default:
                Toast.makeText(this, "Registrasi gagal. Coba lagi.",
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
