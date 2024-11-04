package com.example.musicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.musicapp.model.Song;
import com.example.musicapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "MelodyBox.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_LISTENING_HISTORY = "user_listening_history";
    private static final String TABLE_FAVORITE_SONGS = "favorite_songs";

    // Columns for Users
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_IMAGE = "image";

    // Columns for Songs
    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_SONG_URL = "song_url";
    public static final String COLUMN_SONG_IMAGE = "image";

    // Thêm constant cho bảng UserListeningHistory
    public static final String COLUMN_HISTORY_ID = "history_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Columns for Favorite Songs
    public static final String COLUMN_FAVORITE_ID = "favorite_id";
    public static final String COLUMN_FAVORITE_SONG_ID = "song_id";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng Users
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT NOT NULL, "
                + COLUMN_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_PHONE + " TEXT UNIQUE NOT NULL, "
                + COLUMN_ROLE + " TEXT NOT NULL, "
                + COLUMN_IMAGE + " BLOB)";

        // 2. Tạo bảng Songs
        String createSongsTable = "CREATE TABLE " + TABLE_SONGS + "("
                + COLUMN_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SONG_TITLE + " TEXT NOT NULL, "
                + COLUMN_ARTIST + " TEXT NOT NULL, "
                + COLUMN_DURATION + " INTEGER, "
                + COLUMN_SONG_URL + " TEXT, "
                + COLUMN_SONG_IMAGE + " BLOB)";

        // 3. Tạo bảng Listening History
        String createHistoryTable = "CREATE TABLE " + TABLE_LISTENING_HISTORY + "("
                + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ID + " INTEGER NOT NULL, "
                + COLUMN_SONG_ID + " INTEGER NOT NULL, "
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), "
                + "FOREIGN KEY(" + COLUMN_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + COLUMN_SONG_ID + "))";

        // 4. Tạo bảng Favorite Songs
        String createFavoriteSongsTable = "CREATE TABLE " + TABLE_FAVORITE_SONGS + "("
                + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FAVORITE_SONG_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_FAVORITE_SONG_ID + ") REFERENCES "
                + TABLE_SONGS + "(" + COLUMN_SONG_ID + "))";

        // Thực thi theo đúng thứ tự
        db.execSQL(createUsersTable);
        db.execSQL(createSongsTable);
        db.execSQL(createHistoryTable);
        db.execSQL(createFavoriteSongsTable);
        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa theo thứ tự ngược lại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTENING_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);    // Xóa Songs trước
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);    // Xóa Users cuối cùng
        onCreate(db);
        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
    }

    // User methods
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
        return db.delete(TABLE_USERS, COLUMN_ID + "=?", new String[]{String.valueOf(userId)}) > 0;
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

    public Cursor getUserDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_ID,
                COLUMN_USERNAME,
                COLUMN_PASSWORD,
                COLUMN_PHONE,
                COLUMN_ROLE,
                COLUMN_IMAGE
        };

        try {            // Truy vấn tất cả user trừ admin
            return db.query(
                    TABLE_USERS,
                    columns,
                    COLUMN_ROLE + " != ?", // điều kiện loại trừ admin
                    new String[]{"admin"}, // tham số cho điều kiện
                    null,
                    null,
                    COLUMN_ID + " ASC" // sắp xếp theo ID tăng dần
            );
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting user details: " + e.getMessage());
            return null;
        }
    }

    public boolean updateUser (User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_PHONE, user.getPhoneNumber());
        values.put(COLUMN_ROLE, user.getRole());
        if (user.getImage() != null) {
            values.put(COLUMN_IMAGE, user.getImage());
        }

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        return rowsAffected > 0;
    }

    // Song methods
    public boolean addSong(String title, String artist, int duration, String songUrl, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_SONG_TITLE, title);
        contentValues.put(COLUMN_ARTIST, artist);
        contentValues.put(COLUMN_DURATION, duration);
        contentValues.put(COLUMN_SONG_URL, songUrl);
        if (image != null) {
            contentValues.put(COLUMN_SONG_IMAGE, image);
        }

        long result = db.insert(TABLE_SONGS, null, contentValues);
        Log.d(TAG, "Add song result: " + result + " for song: " + title);
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
        Song song = null;

        Cursor cursor = db.query(TABLE_SONGS, null, COLUMN_SONG_ID + "=?",
                new String[]{String.valueOf(songId)}, null, null, null);
        if (cursor.moveToFirst()) {
            song = new Song(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4),
                    cursor.getString(5),
                    cursor.getBlob(6)
            );
        }
        cursor.close();
        return song;
    }

    public boolean updateSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SONG_TITLE, song.getTitle());
        values.put(COLUMN_ARTIST, song.getArtist());
        values.put(COLUMN_DURATION, song.getDuration());
        values.put(COLUMN_SONG_URL, song.getSongUrl());
        values.put(COLUMN_SONG_IMAGE, song.getImage());

        int rowsAffected = db.update(TABLE_SONGS, values, COLUMN_SONG_ID + " = ?",
                new String[]{String.valueOf(song.getId())});
        return rowsAffected > 0;
    }

    public boolean deleteSong(int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_SONGS, COLUMN_SONG_ID + " = ?", new String[]{String.valueOf(songId)});
        return rowsAffected > 0;
    }

    // Favorite Songs methods
    public boolean addFavoriteSong(int userId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FAVORITE_SONG_ID, songId);

        long result = db.insert(TABLE_FAVORITE_SONGS, null, contentValues);
        Log.d(TAG, "Add favorite song result: " + result);
        return result != -1;
    }

    public List<Song> getFavoriteSongs(int userId) {
        List<Song> favoriteSongs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s.* FROM " + TABLE_SONGS + " s " +
                "INNER JOIN " + TABLE_FAVORITE_SONGS + " fs ON s." + COLUMN_SONG_ID + " = fs." + COLUMN_FAVORITE_SONG_ID +
                " WHERE fs." + COLUMN_ID + " = ?"; // Sửa COLUMN_FAVORITE_SONG_ID thành COLUMN_ID
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

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
                favoriteSongs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteSongs;
    }

    public boolean removeFavoriteSong(int userId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Sửa điều kiện WHERE để sử dụng COLUMN_FAVORITE_SONG_ID và COLUMN_ID
        int rowsAffected = db.delete(TABLE_FAVORITE_SONGS, COLUMN_FAVORITE_SONG_ID + " = ? AND " + COLUMN_ID + " = ?",
                new String[]{String.valueOf(songId), String.valueOf(userId)});
        return rowsAffected > 0;
    }
    public boolean addListeningHistory(int userId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, userId); // Sửa COLUMN_USER_ID thành COLUMN_ID
        values.put(COLUMN_SONG_ID, songId);
        long result = db.insert(TABLE_LISTENING_HISTORY, null, values);
        return result != -1;
    }
    public static class ListeningHistoryItem {
        private int historyId;
        private int userId;
        private int songId;
        private String songTitle;
        private String artist;
        private String timestamp;
        public ListeningHistoryItem(int historyId, int userId, int songId,
                                    String songTitle, String artist, String timestamp) {
            this.historyId = historyId;
            this.userId = userId;
            this.songId = songId;
            this.songTitle = songTitle;
            this.artist = artist;
            this.timestamp = timestamp;
        }
        // Getters
        public int getHistoryId() { return historyId; }
        public int getUserId() { return userId; }
        public int getSongId() { return songId; }
        public String getSongTitle() { return songTitle; }
        public String getArtist() { return artist; }
        public String getTimestamp() { return timestamp; }
    }
    public List<ListeningHistoryItem> getUserListeningHistory(int userId) {
        List<ListeningHistoryItem> history = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT h." + COLUMN_HISTORY_ID + ", h." + COLUMN_ID +
                ", h." + COLUMN_SONG_ID + ", s." + COLUMN_SONG_TITLE +
                ", s." + COLUMN_ARTIST + ", h." + COLUMN_TIMESTAMP +
                " FROM " + TABLE_LISTENING_HISTORY + " h" +
                " JOIN " + TABLE_SONGS + " s ON h." + COLUMN_SONG_ID + " = s." + COLUMN_SONG_ID +
                " WHERE h." + COLUMN_ID + " = ?" +
                " ORDER BY h." + COLUMN_TIMESTAMP + " DESC";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)})) {
            Log.d(TAG, "Executing query: " + query + " with userId: " + userId);
            if (cursor.moveToFirst()) {
                do {
                    ListeningHistoryItem item = new ListeningHistoryItem(
                            cursor.getInt(0),    // history_id
                            cursor.getInt(1),    // user_id
                            cursor.getInt(2),    // song_id
                            cursor.getString(3),  // song_title
                            cursor.getString(4),  // artist
                            cursor.getString(5)   // timestamp
                    );
                    history.add(item);
                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "No history found for userId: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user listening history: " + e.getMessage());
        }
        Log.d(TAG, "Total history items retrieved: " + history.size());
        return history;
    }
    // Xóa lịch sử nghe nhạc của một user
    public boolean clearUserListeningHistory(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_LISTENING_HISTORY,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(userId)});
        return rowsDeleted > 0;
    }
    //Lấy thông tin ng dùng qua sđt
    public User getUserByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PHONE, COLUMN_ROLE, COLUMN_IMAGE};
        String selection = COLUMN_PHONE + " = ?";
        String[] selectionArgs = {phoneNumber};
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
            Log.e(TAG, "Error getting user by phone number: " + e.getMessage());
        }
        return user;
    }
    //Cập nhật mật khẩu trong forgot password
    public boolean updateUserPassword(String phoneNumber, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword); // Giả sử COLUMN_PASSWORD là tên cột cho mật khẩu
        String selection = COLUMN_PHONE + " = ?";
        String[] selectionArgs = { phoneNumber };
        int count = db.update(TABLE_USERS, values, selection, selectionArgs);
        return count > 0; // Trả về true nếu cập nhật thành công
    }
}