package com.example.expensetracker;


import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    Button btnLoginStart, btnSignUpStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
}
