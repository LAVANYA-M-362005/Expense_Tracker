package com.example.expensetracker;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etEmail;
    Button btnRegister;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btnRegister = findViewById(R.id.btnRegister);

        dbHelper = new DBHelper(this);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Validate all fields
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = dbHelper.insertUser(username, password, email);
        if (isInserted) {
            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
            finish(); // back to login
        } else {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
        }
    }
}
