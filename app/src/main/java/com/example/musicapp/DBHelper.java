package com.example.musicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.musicapp.model.Song;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "MelodyBox.db";
    private static final int DATABASE_VERSION = 2; // Tăng version để trigger onUpgrade
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ALBUMS = "albums";
    private static final String TABLE_SONGS = "songs";

    // Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_IMAGE = "image";

    public static final String COLUMN_ALBUM_ID = "album_id";
    public static final String COLUMN_ALBUM_TITLE = "title";
    public static final String COLUMN_ALBUM_IMAGE = "image";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_USER_ID = "user_id";

    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_SONG_URL = "song_url";
    public static final String COLUMN_SONG_IMAGE = "image";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT NOT NULL, "
                + COLUMN_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_PHONE + " TEXT UNIQUE NOT NULL, "
                + COLUMN_ROLE + " TEXT NOT NULL, "
                + COLUMN_IMAGE + " BLOB)";

        String createAlbumsTable = "CREATE TABLE " + TABLE_ALBUMS + "("
                + COLUMN_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ALBUM_TITLE + " TEXT NOT NULL, "
                + COLUMN_ALBUM_IMAGE + " BLOB, "
                + COLUMN_RELEASE_DATE + " TEXT, "
                + COLUMN_USER_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_ID + "))";

        String createSongsTable = "CREATE TABLE " + TABLE_SONGS + "("
                + COLUMN_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SONG_TITLE + " TEXT NOT NULL, "
                + COLUMN_ARTIST + " TEXT NOT NULL, "
                + COLUMN_ALBUM_ID + " INTEGER, "
                + COLUMN_DURATION + " INTEGER, "
                + COLUMN_SONG_URL + " TEXT, "
                + COLUMN_SONG_IMAGE + " BLOB, "
                + "FOREIGN KEY(" + COLUMN_ALBUM_ID + ") REFERENCES "
                + TABLE_ALBUMS + "(" + COLUMN_ALBUM_ID + "))";

        db.execSQL(createUsersTable);
        db.execSQL(createAlbumsTable);
        db.execSQL(createSongsTable);
        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng cũ và tạo lại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
    }
    public boolean addAlbum(int userId, String title, byte[] image, String releaseDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ALBUM_TITLE, title);
        contentValues.put(COLUMN_ALBUM_IMAGE, image);
        contentValues.put(COLUMN_RELEASE_DATE, releaseDate);
        contentValues.put(COLUMN_USER_ID, userId);  // Thêm user_id vào Album

        long result = db.insert(TABLE_ALBUMS, null, contentValues);
        Log.d(TAG, "Add album result: " + result);
        return result != -1;
    }

    public boolean addSong(String title, String artist, int albumId, int duration, String songUrl, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SONG_TITLE, title);
        contentValues.put(COLUMN_ARTIST, artist);
        contentValues.put(COLUMN_ALBUM_ID, albumId);
        contentValues.put(COLUMN_DURATION, duration);
        contentValues.put(COLUMN_SONG_URL, songUrl);
        contentValues.put(COLUMN_SONG_IMAGE, image);

        long result = db.insert(TABLE_SONGS, null, contentValues);
        Log.d(TAG, "Add song result: " + result);
        return result != -1;
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SONGS, null, null, null, null, null, null);

        Log.d(TAG, "getAllSongs: Total rows = " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                Song song = new Song(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getString(5),
                        cursor.getBlob(6)
                );
                songs.add(song);
                Log.d(TAG, "getAllSongs: Added song - " + song.getTitle());
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "getAllSongs: No songs found in database");
        }
        cursor.close();
        Log.d(TAG, "getAllSongs: Total songs retrieved = " + songs.size());
        return songs;
    }

    public Song getSongById(int songId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SONGS, null, COLUMN_SONG_ID + "=?",
                new String[]{String.valueOf(songId)}, null, null, null);
        Song song = null;
        if (cursor.moveToFirst()) {
            song = new Song(
                    cursor.getInt(0),  // COLUMN_SONG_ID
                    cursor.getString(1),  // COLUMN_SONG_TITLE
                    cursor.getString(2),  // COLUMN_ARTIST
                    cursor.getInt(3),  // COLUMN_ALBUM_ID
                    cursor.getInt(4),  // COLUMN_DURATION
                    cursor.getString(5),  // COLUMN_SONG_URL
                    cursor.getBlob(6)  // COLUMN_SONG_IMAGE
            );
        }
        cursor.close();
        return song;
    }
    public boolean addUser(String username, String password, String phone, String role, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_ROLE, role);
        if (image != null) {
            contentValues.put(COLUMN_IMAGE, image);
        }

        long result = db.insert(TABLE_USERS, null, contentValues);
        Log.d(TAG, "Add user result: " + result);
        return result != -1;
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("users", "id=?", new String[]{String.valueOf(userId)}) > 0;
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

        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PHONE, COLUMN_ROLE, COLUMN_IMAGE};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        try (Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                byte[] imageBytes = cursor.getBlob(5);
                user = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        imageBytes
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

        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PHONE, COLUMN_ROLE, COLUMN_IMAGE};
        String selection = COLUMN_PHONE + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {phoneNumber, password};

        try (Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                byte[] imageBytes = cursor.getBlob(5);
                user = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        imageBytes
                );
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by phone and password: " + e.getMessage());
        }

        return user;
    }
}