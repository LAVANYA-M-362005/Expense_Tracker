package com.example.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    Button btnLogout;
    TextView tvWelcome;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);

        // Initialize UI elements
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);

        // Display welcome message with username using string resource
        String username = sharedPreferences.getString("username", "User");
        tvWelcome.setText(getString(R.string.welcome_user, username));

        // Logout button click listener
        btnLogout.setOnClickListener(v -> logoutUser());
    }

    // Logout method
    private void logoutUser() {
        // Clear session
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
