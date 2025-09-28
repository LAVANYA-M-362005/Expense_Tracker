package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Import View for the modern API
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class StartActivity extends AppCompatActivity {

    Button btnLoginStart, btnSignUpStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Set up edge-to-edge content (if you want the layout to go under the status bar)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // 2. Hide the system bars (Status Bar and Navigation Bar)
        hideSystemBars();

        setContentView(R.layout.activity_start);

        btnLoginStart = findViewById(R.id.btnLoginStart);
        btnSignUpStart = findViewById(R.id.btnSignUpStart);

        btnLoginStart.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnSignUpStart.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Hides the status bar and navigation bar using the modern WindowInsetsController API.
     */
    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        if (windowInsetsController == null) {
            return;
        }

        // Configure the behavior of the system bars (Status Bar and Navigation Bar)
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }
}