package com.example.musicapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.DBHelper;
import com.example.musicapp.ListeningHistoryActivity;
import com.example.musicapp.PlaylistAdapter;
import com.example.musicapp.R;
import com.example.musicapp.SearchActivity;
import com.example.musicapp.SettingActivity;
import com.example.musicapp.UserActivity;
import com.example.musicapp.model.Song;
import com.example.musicapp.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Home extends AppCompatActivity {

    private static final String TAG = "Home";
    private PlaylistAdapter playlistAdapter;
    private PlaylistAdapter favoritePlaylistAdapter; // Adapter cho bài hát yêu thích
    private RecyclerView recyclerFavorites; // RecyclerView cho bài hát yêu thích
    private int userId;
    private ImageView notificationIcon;
    private ImageView profileImage;
    private DBHelper dbHelper;
    private ExecutorService executorService;
    private Handler mainHandler;
    private Button btnListeningHistory; // Nút điều hướng đến ListeningHistoryActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        try {
            dbHelper = new DBHelper(this);
            Log.d(TAG, "DBHelper initialized");

            userId = getIntent().getIntExtra("USER_ID", -1);
            if (userId == -1) {
                throw new IllegalArgumentException("No valid user ID provided");
            }
            Log.d(TAG, "User  ID: " + userId);

            initializeViews();
            setupClickListeners();
            loadUserData();
            setupBottomNavigation();
            setupRecyclerViews();
            loadSongsAndAlbums();
            loadFavoriteSongs();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        notificationIcon = findViewById(R.id.icon_noti);
        profileImage = findViewById(R.id.circleImageView);
        recyclerFavorites = findViewById(R.id.recyclerFavorites); // Khởi tạo RecyclerView cho bài hát yêu thích
        btnListeningHistory = findViewById(R.id.btn_listening_history); // Khởi tạo nút ListeningHistory
        Log.d(TAG, "Views initialized");
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteSongs(); // Tải lại danh sách bài hát yêu thích
    }
    private void setupClickListeners() {
        notificationIcon.setOnClickListener(v ->
                Toast.makeText(this, "Notifications coming soon!", Toast.LENGTH_SHORT).show());

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        btnListeningHistory.setOnClickListener(v -> { // Xử lý sự kiện bấm nút ListeningHistory
            Intent intent = new Intent(Home.this, ListeningHistoryActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        Log.d(TAG, "Click listeners set up");
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_search) {
                intent = new Intent(this, SearchActivity.class);
            } else if (itemId == R.id.nav_setting) {
                intent = new Intent(this, SettingActivity.class);
            } else if (itemId == R.id.nav_user) {
                intent = new Intent(this, UserActivity.class);
            }

            if (intent != null) {
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
            }
            return true;
        });
    }

    private void setupRecyclerViews() {
        RecyclerView recyclerViewPlaylist = findViewById(R.id.recyclerview);
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Log.d(TAG, "RecyclerViews set up");
    }

    private void loadUserData() {
        executorService.execute(() -> {
            try {
                User user = dbHelper.getUserById(userId);
                if (user == null) {
                    throw new IllegalStateException("User  not found for ID: " + userId);
                }
                mainHandler.post(() -> {
                    Log.d(TAG, "User  data loaded: " + user.toString());
                    // TODO: Update UI with user data
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    Log.e(TAG, "Error loading user data", e);
                    Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadSongsAndAlbums() {
        executorService.execute(() -> {
            try {
                List<Song> allSongs = dbHelper.getAllSongs();
                mainHandler.post(() -> {
                    playlistAdapter = new PlaylistAdapter(allSongs);
                    RecyclerView recyclerView = findViewById(R.id.recyclerview);
                    recyclerView.setAdapter(playlistAdapter);
                    setupPlaylistItemClickListener();
                    Log.d(TAG, "All songs loaded: " + allSongs.size());
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    Log.e(TAG, "Error loading songs and albums", e);
                    Toast.makeText(Home.this, "Error loading content: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadFavoriteSongs() {
        executorService.execute(() -> {
            try {
                List<Song> favoriteSongs = dbHelper.getFavoriteSongs(userId); // Tải bài hát yêu thích cho người dùng
                mainHandler.post(() -> {
                    favoritePlaylistAdapter = new PlaylistAdapter(favoriteSongs);
                    recyclerFavorites.setAdapter(favoritePlaylistAdapter);
                    setupFavoriteItemClickListener();
                    Log.d(TAG, "Favorite songs loaded: " + favoriteSongs.size());
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    Log.e(TAG, "Error loading favorite songs", e);
                    Toast.makeText(Home.this, "Error loading favorite songs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupPlaylistItemClickListener() {
        playlistAdapter.setOnItemClickListener((position, playlist) -> {
            Song selectedSong = playlist.get(position);

            // Thêm bài hát vào lịch sử nghe nhạc
            boolean isAdded = dbHelper.addListeningHistory(userId, selectedSong.getId());

            if (isAdded) {
                Log.d(TAG, "Added song to listening history: " + selectedSong.getTitle());
            } else {
                Log.e(TAG, "Failed to add song to listening history");
            }

            // Chuyển sang màn hình phát nhạc
            Intent intent = new Intent(Home.this, Play_song.class);
            intent.putExtra("position", position);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
    }

    private void setupFavoriteItemClickListener() {
        favoritePlaylistAdapter.setOnItemClickListener((position, playlist) -> {
            Song selectedSong = playlist.get(position);

            // Thêm bài hát yêu thích vào lịch sử nghe nhạc
            boolean isAdded = dbHelper.addListeningHistory(userId, selectedSong.getId());

            if (isAdded) {
                Log.d(TAG, "Added favorite song to listening history: " + selectedSong.getTitle());
            } else {
                Log.e(TAG, "Failed to add favorite song to listening history");
            }

            // Chuyển sang màn hình phát nhạc
            Intent intent = new Intent(Home.this, Play_song.class);
            intent.putExtra("position", position);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("IS_FAVORITE", true); // Đánh dấu là bài hát yêu thích
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
            Log.d(TAG, "DBHelper closed");
        }
        if (executorService != null) {
            executorService.shutdown();
            Log.d(TAG, "ExecutorService shutdown");
        }
    }
}