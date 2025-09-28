package com.example.expensetracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast; // NEW IMPORT
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList; // NEW IMPORT
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private SharedPreferences sharedPreferences;
    private int currentUserId = -1; // Field to hold the user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history); // Assuming you have an activity_history layout

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);

        // Get the current user ID
        currentUserId = sharedPreferences.getInt("userId", -1);

        if (currentUserId == -1) {
            // Handle case where user ID is missing (e.g., redirect to login or show error)
            // For now, we'll just show a toast and use an empty list.
            // You should implement a proper session check/redirect.
            Toast.makeText(this, "Error: User session invalid. Please log in.", Toast.LENGTH_LONG).show();
        }


        recyclerView = findViewById(R.id.recyclerHistory); // Assuming ID is recyclerHistory
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAllExpenses();
    }

    private void loadAllExpenses() {
        if (currentUserId != -1) {
            // FIX: Pass the currentUserId to the getAllExpenses method
            List<Expense> expenses = ExpenseDatabase.getInstance(this).expenseDao().getAllExpenses(currentUserId);

            adapter = new ExpenseAdapter(expenses);
            recyclerView.setAdapter(adapter);
        } else {
            // Initialize with an empty adapter if user ID is missing
            adapter = new ExpenseAdapter(new ArrayList<>());
            recyclerView.setAdapter(adapter);
        }
    }
}