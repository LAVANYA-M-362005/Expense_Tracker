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
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "email TEXT)");

        db.execSQL("CREATE TABLE categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE)");

        db.execSQL("CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "category_id INTEGER," +
                "amount REAL," +
                "date TEXT," +
                "note TEXT," +
                "receipt_path TEXT)");

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
}
