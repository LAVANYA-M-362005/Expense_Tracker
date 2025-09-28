package com.example.expensetracker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    private int id;

    // NEW FIELD: Link to the logged-in user
    private int userId;

    private String description;
    private double amount;
    private String category;
    private String date; // store as String for simplicity (yyyy-MM-dd)

    // CONSTRUCTOR UPDATED to include userId
    public Expense(int userId, String description, double amount, String category, String date) {
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    // Getters & setters (You must add a getter for the new field)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
}