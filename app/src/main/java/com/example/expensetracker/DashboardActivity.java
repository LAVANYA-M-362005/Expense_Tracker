package com.example.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class DashboardActivity extends AppCompatActivity {

    Button btnLogout, btnAddExpense, btnHistory, btnHome, btnDownload, btnViewMore;
    ImageButton btnProfile; // Changed to ImageButton based on XML
    ToggleButton toggleMode;
    TextView tvWelcome, tvTotalSpent, tvRemainingBudget, tvFooter;
    SharedPreferences sharedPreferences;
    PieChart pieChart;
    LineChart lineChart;

    // NEW FIELD: To store the logged-in user's ID
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);

        // CRITICAL STEP 1: Get the current user ID
        currentUserId = sharedPreferences.getInt("userId", -1);
        if (currentUserId == -1) {
            // Handle case where session is invalid/expired
            logoutUser();
            Toast.makeText(this, "Session expired, please log in.", Toast.LENGTH_LONG).show();
            return;
        }

        // Initialize UI elements
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnHistory = findViewById(R.id.btnHistory);
        btnHome = findViewById(R.id.btnHome);
        btnProfile = findViewById(R.id.btnProfile); // Correctly using ImageButton ID
        btnDownload = findViewById(R.id.btnDownload);
        btnViewMore = findViewById(R.id.btnViewMore);
        toggleMode = findViewById(R.id.toggleMode);

        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);

        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget);
        tvFooter = findViewById(R.id.tvFooter);

        // Display welcome message
        String username = sharedPreferences.getString("username", "User");
        tvWelcome.setText("Welcome " + username);

        // Logout button
        btnLogout.setOnClickListener(v -> logoutUser());

        // Navigation
        btnHome.setOnClickListener(v -> { /* Already on Dashboard */ });
        btnAddExpense.setOnClickListener(v -> startActivity(new Intent(this, AddExpenseActivity.class)));
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // Dark/Light Mode toggle (CLEANUP: Removed incorrect expense data logic)
        toggleMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setTheme(R.style.DarkTheme);
            } else {
                setTheme(R.style.LightTheme);
            }
            // Recreate the activity to apply the new theme
            recreate();
        });

        // View More (go to History)
        btnViewMore.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));

        // Download button
        btnDownload.setOnClickListener(v -> {
            ExpenseDao dao = ExpenseDatabase.getInstance(this).expenseDao();

            // NOTE: Download methods still need to be updated to accept userId
            List<CategoryTotal> categoryData = dao.getCategoryWiseSpending(currentUserId);
            List<MonthTotal> monthData = dao.getMonthlySpending(currentUserId);

            // Export CSV
            File catFile = CsvExporter.exportCategoryReport(this, categoryData);
            File monthFile = CsvExporter.exportMonthlyReport(this, monthData);

            // Export PDF
            File pdfFile = PdfExporter.exportReport(this, categoryData, monthData);

            Toast.makeText(this, "Reports saved:\n" +
                    catFile.getAbsolutePath() + "\n" +
                    monthFile.getAbsolutePath() + "\n" +
                    pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        });


        // Footer info
        tvFooter.setText("Expense Tracker App | Consultant: ABC Solutions | Contact: support@expensetracker.com");

        // CRITICAL STEP 2: Load data into charts and recent expenses after getting the userId
        loadCharts();
        loadRecentExpenses();
        loadSummary(); // NEW: Load Total Spent and Remaining Budget
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        finish();
    }

    /**
     * Loads the Total Spent and Remaining Budget for the current month and user.
     */
    private void loadSummary() {
        ExpenseDao expenseDao = ExpenseDatabase.getInstance(this).expenseDao();
        DBHelper dbHelper = new DBHelper(this);

        // Get Total Spent (user-specific)
        double totalSpent = expenseDao.getTotalSpent(currentUserId);
        tvTotalSpent.setText(String.format("Total Spent: ₹%.2f", totalSpent));

        // Get Monthly Budget and Spent (user-specific)
        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Calendar.getInstance().getTime());

        // Fetch budget from DBHelper (requires getBudgetForUserAndMonth() to be implemented in DBHelper)
        double monthlyBudget = dbHelper.getBudgetForUserAndMonth(currentUserId, currentMonth);
        if (monthlyBudget == 0.0) {
            // Set a default or prompt user to set a budget
            tvRemainingBudget.setText("Set a Budget!");
            tvRemainingBudget.setTextColor(Color.GRAY);
            return;
        }

        double monthlySpent = expenseDao.getMonthlySpent(currentUserId, currentMonth);

        double remainingBudget = monthlyBudget - monthlySpent;
        tvRemainingBudget.setText(String.format("Remaining Budget: ₹%.2f", remainingBudget));

        // Change text color based on remaining budget
        if (remainingBudget < 0) {
            tvRemainingBudget.setTextColor(Color.RED);
        } else {
            tvRemainingBudget.setTextColor(Color.parseColor("#00675B")); // Example: Dark Green
        }
    }


    /**
     * Loads the last 3 expenses for the current user.
     */
    private void loadRecentExpenses() {
        // CRITICAL FIX: Pass userId to the DAO
        List<Expense> expenses = ExpenseDatabase.getInstance(this).expenseDao().getRecentExpenses(currentUserId);
        RecyclerView recyclerRecent = findViewById(R.id.recyclerRecentExpenses);
        recyclerRecent.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecent.setAdapter(new ExpenseAdapter(expenses));
    }

    /**
     * Loads Pie and Line charts data specific to the current user.
     */
    private void loadCharts() {
        // CRITICAL FIX: Pass userId to the DAO
        ExpenseDao dao = ExpenseDatabase.getInstance(this).expenseDao();

        // ===== PIE CHART (Category-wise) =====
        List<CategoryTotal> categoryTotals = dao.getCategoryWiseSpending(currentUserId);
        List<PieEntry> pieEntries = new ArrayList<>();

        for (CategoryTotal ct : categoryTotals) {
            pieEntries.add(new PieEntry((float) ct.total, ct.category));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Category Spending");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(12f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();

        // ===== LINE CHART (Monthly Spending) =====
        List<MonthTotal> monthTotals = dao.getMonthlySpending(currentUserId);
        List<Entry> lineEntries = new ArrayList<>();
        List<String> months = new ArrayList<>();

        int index = 0;
        for (MonthTotal mt : monthTotals) {
            lineEntries.add(new Entry(index, (float) mt.total));
            months.add(mt.month);
            index++;
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Monthly Spending");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setValueTextSize(10f);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));

        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }
}