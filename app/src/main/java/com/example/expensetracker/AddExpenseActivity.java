package com.example.expensetracker;

import android.content.SharedPreferences; // NEW IMPORT
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    EditText etDescription, etAmount, etCategory;
    Button btnSave;

    // NEW FIELD: To access the logged-in user's ID
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // NEW INITIALIZATION: Get the session data
        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);

        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        etCategory = findViewById(R.id.etCategory);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        // CRITICAL STEP 1: Get userId from session
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: User not logged in. Please log in again.", Toast.LENGTH_LONG).show();
            // Optional: Redirect to login activity here if needed
            return;
        }

        String desc = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = etCategory.getText().toString().trim();

        if (desc.isEmpty() || amountStr.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // CRITICAL STEP 2: Pass userId to the Expense constructor
        // Assuming you have updated the Expense constructor to accept userId
        Expense expense = new Expense(userId, desc, amount, category, date);
        ExpenseDatabase.getInstance(this).expenseDao().insertExpense(expense);

        Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();
        finish(); // Go back to Dashboard
    }
}