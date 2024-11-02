package com.example.musicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.musicapp.model.Album;
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
    private static final String TABLE_ALBUMS = "albums";
    private static final String TABLE_LISTENING_HISTORY = "user_listening_history";


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

    // Thêm các cột cho bảng Album
    public static final String COLUMN_ALBUM_ID = "album_id";
    public static final String COLUMN_ALBUM_TITLE = "title";
    public static final String COLUMN_ALBUM_IMAGE = "image";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_USER_ID = "user_id";

    // Thêm constant cho bảng UserListeningHistory
    public static final String COLUMN_HISTORY_ID = "history_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng Users trước
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT NOT NULL, "
                + COLUMN_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_PHONE + " TEXT UNIQUE NOT NULL, "
                + COLUMN_ROLE + " TEXT NOT NULL, "
                + COLUMN_IMAGE + " BLOB)";

        // 2. Tạo bảng Albums (phụ thuộc vào Users)
        String createAlbumsTable = "CREATE TABLE " + TABLE_ALBUMS + "("
                + COLUMN_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ALBUM_TITLE + " TEXT NOT NULL, "
                + COLUMN_ALBUM_IMAGE + " BLOB, "
                + COLUMN_RELEASE_DATE + " TEXT, "
                + COLUMN_USER_ID + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_ID + "))";

        // 3. Tạo bảng Songs (phụ thuộc vào Albums)
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
        String createHistoryTable = "CREATE TABLE " + TABLE_LISTENING_HISTORY + "("
                + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER NOT NULL, "
                + COLUMN_SONG_ID + " INTEGER NOT NULL, "
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), "
                + "FOREIGN KEY(" + COLUMN_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + COLUMN_SONG_ID + "))";


        // Thực thi theo đúng thứ tự
        db.execSQL(createUsersTable);
        db.execSQL(createAlbumsTable);
        db.execSQL(createSongsTable);
        db.execSQL(createHistoryTable);
        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa theo thứ tự ngược lại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTENING_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);    // Xóa Songs trước
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUMS);   // Xóa Albums sau
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

        try {
            // Truy vấn tất cả user trừ admin
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
    public boolean updateUser(User user) {
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
    public List<Song> getRecentSongs(int limit) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_SONGS + " ORDER BY " + COLUMN_SONG_ID + " DESC LIMIT ?";

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)})) {

            Log.d(TAG, "getRecentSongs: Total rows = " + cursor.getCount());

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
                    Log.d(TAG, "getRecentSongs: Added song - " + song.getTitle());
                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "getRecentSongs: No songs found in database");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getRecentSongs: " + e.getMessage());
        }

        Log.d(TAG, "getRecentSongs: Total songs retrieved = " + songs.size());
        return songs;
    }
    public int getOrCreateDefaultAlbum() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra xem album mặc định đã tồn tại chưa
        Cursor cursor = db.query(TABLE_ALBUMS, new String[]{COLUMN_ALBUM_ID},
                COLUMN_ALBUM_TITLE + "=?", new String[]{"Default Album"},
                null, null, null);

        if (cursor.moveToFirst()) {
            int albumId = cursor.getInt(0);
            cursor.close();
            return albumId;
        }

        // Nếu chưa có, tạo album mặc định
        ContentValues values = new ContentValues();
        values.put(COLUMN_ALBUM_TITLE, "Default Album");
        values.put(COLUMN_RELEASE_DATE, "2024-01-01");
        values.put(COLUMN_USER_ID, 1); // Giả sử user_id = 1 là admin

        long albumId = db.insert(TABLE_ALBUMS, null, values);
        cursor.close();
        return (int) albumId;
    }
    // Song methods
    public boolean addSong(String title, String artist, int albumId, int duration, String songUrl, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Nếu không có albumId được chỉ định, sử dụng album mặc định
        if (albumId <= 0) {
            albumId = getOrCreateDefaultAlbum();
        }

        contentValues.put(COLUMN_SONG_TITLE, title);
        contentValues.put(COLUMN_ARTIST, artist);
        contentValues.put(COLUMN_ALBUM_ID, albumId);
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
        values.put(COLUMN_ALBUM_ID, song.getAlbumId());
        values.put(COLUMN_DURATION, song.getDuration());
        values.put(COLUMN_SONG_URL, song.getSongUrl());
        values.put(COLUMN_SONG_IMAGE, song.getImage());

        int rowsAffected = db.update(TABLE_SONGS, values, COLUMN_SONG_ID + " = ?",
                new String[]{String.valueOf(song.getId())});
        return rowsAffected > 0;
    }
    public List<Song> getSongsByAlbumId(int albumId) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SONGS, null,
                COLUMN_ALBUM_ID + "=?", new String[]{String.valueOf(albumId)},
                null, null, null);

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
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songs;
    }
    public List<Album> getAlbumsByUserId(int userId) {
        List<Album> albums = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALBUMS, null,
                COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Album album = new Album(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getBlob(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                );
                albums.add(album);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return albums;
    }
    public boolean deleteSong(int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_SONGS, COLUMN_SONG_ID + " = ?", new String[]{String.valueOf(songId)});
        return rowsAffected > 0;
    }
    // Thêm các phương thức này vào class DBHelper

    public boolean addAlbum(String title, byte[] image, String releaseDate, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ALBUM_TITLE, title);
        values.put(COLUMN_ALBUM_IMAGE, image);
        values.put(COLUMN_RELEASE_DATE, releaseDate);
        values.put(COLUMN_USER_ID, userId);

        long result = db.insert(TABLE_ALBUMS, null, values);
        return result != -1;
    }

    public List<Album> getAllAlbums() {
        List<Album> albums = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALBUMS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Album album = new Album(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getBlob(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                );
                albums.add(album);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return albums;
    }

    public Album getAlbumById(int albumId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Album album = null;

        Cursor cursor = db.query(TABLE_ALBUMS, null,
                COLUMN_ALBUM_ID + "=?", new String[]{String.valueOf(albumId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            album = new Album(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getBlob(2),
                    cursor.getString(3),
                    cursor.getInt(4)
            );
        }
        cursor.close();
        return album;
    }

    public boolean updateAlbum(Album album) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ALBUM_TITLE, album.getTitle());
        values.put(COLUMN_ALBUM_IMAGE, album.getImage());
        values.put(COLUMN_RELEASE_DATE, album.getReleaseDate());
        values.put(COLUMN_USER_ID, album.getUserId());

        int rowsAffected = db.update(TABLE_ALBUMS, values,
                COLUMN_ALBUM_ID + " = ?",
                new String[]{String.valueOf(album.getId())});
        return rowsAffected > 0;
    }

    public boolean deleteAlbum(int albumId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Xóa tất cả các bài hát trong album trước
        db.delete(TABLE_SONGS, COLUMN_ALBUM_ID + " = ?",
                new String[]{String.valueOf(albumId)});
        // Sau đó xóa album
        int rowsAffected = db.delete(TABLE_ALBUMS,
                COLUMN_ALBUM_ID + " = ?",
                new String[]{String.valueOf(albumId)});
        return rowsAffected > 0;
    }
    public boolean addListeningHistory(int userId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_SONG_ID, songId);
        // Timestamp sẽ tự động được thêm vào nhờ DEFAULT CURRENT_TIMESTAMP

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

        String query = "SELECT h." + COLUMN_HISTORY_ID + ", h." + COLUMN_USER_ID +
                ", h." + COLUMN_SONG_ID + ", s." + COLUMN_SONG_TITLE +
                ", s." + COLUMN_ARTIST + ", h." + COLUMN_TIMESTAMP +
                " FROM " + TABLE_LISTENING_HISTORY + " h" +
                " JOIN " + TABLE_SONGS + " s ON h." + COLUMN_SONG_ID +
                " = s." + COLUMN_SONG_ID +
                " WHERE h." + COLUMN_USER_ID + " = ?" +
                " ORDER BY h." + COLUMN_TIMESTAMP + " DESC";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)})) {
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
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user listening history: " + e.getMessage());
        }

        return history;
    }

    // Xóa lịch sử nghe nhạc của một user
    public boolean clearUserListeningHistory(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_LISTENING_HISTORY,
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        return rowsDeleted > 0;
    }

}