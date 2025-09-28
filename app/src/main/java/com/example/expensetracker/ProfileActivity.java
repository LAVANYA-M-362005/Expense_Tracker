package com.example.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView tvProfileInfo;
    EditText etEmail, etPassword;
    Button btnUpdateProfile, btnDeleteAccount;

    SharedPreferences sharedPreferences;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProfileInfo = findViewById(R.id.tvProfileInfo);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);
        dbHelper = new DBHelper(this);

        // Get username from session
        String username = sharedPreferences.getString("username", "User");

        // Fetch user details
        Cursor cursor = dbHelper.getUserByUsername(username);
        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            tvProfileInfo.setText("User Profile\n\nUsername: " + username);
            etEmail.setText(email);
            cursor.close();
        }

        // Update profile
        btnUpdateProfile.setOnClickListener(v -> {
            String newEmail = etEmail.getText().toString().trim();
            String newPassword = etPassword.getText().toString().trim();

            if (newEmail.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = dbHelper.updateUser(username, newEmail, newPassword);

            if (updated) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete account
        btnDeleteAccount.setOnClickListener(v -> {
            String currentUser = sharedPreferences.getString("username", "User");

            boolean deleted = dbHelper.deleteUser(currentUser);

            if (deleted) {
                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();

                // Clear session
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Redirect to login
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
