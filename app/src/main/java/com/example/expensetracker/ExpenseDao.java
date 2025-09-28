package com.example.expensetracker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insertExpense(Expense expense);

    // Filter by userId
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY id DESC")
    List<Expense> getAllExpenses(int userId);

    // Filter by userId
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY id DESC LIMIT 3")
    List<Expense> getRecentExpenses(int userId);

    // Filter by userId
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId")
    double getTotalSpent(int userId);

    // Filter by userId
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date LIKE :month || '%'")
    double getMonthlySpent(int userId, String month);

    // Filter by userId
    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE userId = :userId GROUP BY category")
    List<CategoryTotal> getCategoryWiseSpending(int userId);

    // Filter by userId
    @Query("SELECT SUBSTR(date, 1, 7) as month, SUM(amount) as total FROM expenses WHERE userId = :userId GROUP BY month ORDER BY month ASC")
    List<MonthTotal> getMonthlySpending(int userId);

    // Removed getMostSpentCategory as it's redundant with getCategoryWiseSpending
}