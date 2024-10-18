package com.example.musicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "Users.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";

    // Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ROLE = "role";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT NOT NULL, "
                + COLUMN_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_PHONE + " TEXT UNIQUE NOT NULL, "
                + COLUMN_ROLE + " TEXT NOT NULL)";
        db.execSQL(createTable);
        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
    }

    public boolean addUser(String username, String password, String phone, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_ROLE, role);

        long result = db.insert(TABLE_USERS, null, contentValues);
        Log.d(TAG, "Add user result: " + result);
        return result != -1;
    }
    public boolean isPhoneNumberExists(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_PHONE + "=?", new String[]{phoneNumber},
                null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
    public User getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PHONE, COLUMN_ROLE};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        try (Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                user = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by ID: " + e.getMessage());
        }

        return user;
    }

    public Cursor getUserDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        Log.d(TAG, "getUserDetails query executed. Cursor count: " + cursor.getCount());
        return cursor;
    }

    public User getUserByPhoneAndPassword(String phoneNumber, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PHONE, COLUMN_ROLE};
        String selection = COLUMN_PHONE + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {phoneNumber, password};

        try (Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                user = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by phone and password: " + e.getMessage());
        }

        return user;
    }
}