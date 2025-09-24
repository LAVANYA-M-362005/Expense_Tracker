package com.example.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnRegister;
    DBHelper dbHelper;
    SharedPreferences sharedPreferences;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);

        // Auto-login if session exists
        if (sharedPreferences.getBoolean("loggedIn", false)) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {
        String usernameInput = etUsername.getText().toString().trim();
        String passwordInput = etPassword.getText().toString().trim();

        if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Debug log
        Log.d(TAG, "Attempting login: " + usernameInput);

        boolean isValid = dbHelper.checkUserCaseInsensitive(usernameInput, passwordInput);

        if (isValid) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("loggedIn", true);
            editor.putString("username", usernameInput);
            editor.apply();

            Log.d(TAG, "Login successful: " + usernameInput);

            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        } else {
            Log.d(TAG, "Login failed for: " + usernameInput);
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
