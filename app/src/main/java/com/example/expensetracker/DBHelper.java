package com.example.expensetracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "expense_tracker.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = "DBHelper";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table for User Authentication
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "email TEXT)");

        // Table for Categories (though not used much with Room Expense Entity directly)
        db.execSQL("CREATE TABLE categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE)");

        // Table for Expenses (Kept for consistency, but Room is used for expense tracking)
        db.execSQL("CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "category_id INTEGER," +
                "amount REAL," +
                "date TEXT," +
                "note TEXT," +
                "receipt_path TEXT)");

        // Table for Budgets (Used by Dashboard for Remaining Budget calculation)
        db.execSQL("CREATE TABLE budgets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "month TEXT," +
                "amount REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS expenses");
        db.execSQL("DROP TABLE IF EXISTS budgets");
        onCreate(db);
    }

    // Insert user with trimmed inputs and debug logging
    public boolean insertUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username.trim());
        values.put("password", password.trim());
        values.put("email", email.trim());

        long result = db.insert("users", null, values);

        if (result == -1) {
            Log.d(TAG, "Insert failed for username: " + username);
        } else {
            Log.d(TAG, "User inserted successfully: " + username);
        }

        return result != -1;
    }

    // Original plain login (optional)
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE username=?", new String[]{username});
    }

    public boolean updateUser(String username, String newEmail, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", newEmail.trim());
        values.put("password", newPassword.trim());

        int rows = db.update("users", values, "username=?", new String[]{username});
        return rows > 0;
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("users", "username=?", new String[]{username});
        return rows > 0;
    }

    // Case-insensitive login with trimmed inputs
    public boolean checkUserCaseInsensitive(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE LOWER(username)=? AND password=?",
                new String[]{username.toLowerCase().trim(), password.trim()});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Get User ID after successful login
    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE username=?",
                new String[]{username.trim()});

        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            if (idIndex != -1) {
                userId = cursor.getInt(idIndex);
            } else {
                // Fallback for older Android versions where getColumnIndexOrThrow is preferred
                try {
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Column 'id' not found in users table.", e);
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return userId;
    }

    // NEW METHOD: Get Budget for the specified user and month
    public double getBudgetForUserAndMonth(int userId, String month) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Month should be in YYYY-MM format, matching how it's saved in the budgets table
        Cursor cursor = db.rawQuery(
                "SELECT amount FROM budgets WHERE user_id=? AND month=?",
                new String[]{String.valueOf(userId), month});

        double budget = 0.0;
        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve the amount column safely
            int amountIndex = cursor.getColumnIndex("amount");
            if (amountIndex != -1) {
                budget = cursor.getDouble(amountIndex);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return budget;
    }
}