package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity {

    private PlaylistAdapter playlistAdapter;
    private RecyclerView recyclerAlbum;
    private AlbumsAdapter albumsAdapter;
    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Get user ID from intent
        userId = getIntent().getIntExtra("USER_ID", -1);

        if (userId == -1) {
            // Handle error: No valid user ID
            Toast.makeText(this, "Error: No valid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TODO: Load user data and set up the home screen
        loadUserData();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Thiết lập item đã chọn là Home
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Xử lý sự kiện chọn item
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if(item.getItemId() == R.id.nav_home){
                return true;
            }else if(item.getItemId() == R.id.nav_search){
                intent = new Intent(this, SearchActivity.class);
            }else if(item.getItemId() == R.id.nav_setting){
                intent = new Intent(this, SettingActivity.class);
            }else if(item.getItemId() == R.id.nav_user){
                intent = new Intent(this, UserActivity.class);
            }
            if (intent != null) {
                startActivity(intent);
                finish();
            }
            return true;
        });

        // Thiết lập RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerAlbum = findViewById(R.id.recyclerablbums);
        // Thiết lập LayoutManager cho RecyclerView (hiển thị ngang)
        LinearLayoutManager layoutManagerPlaylist = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerAlbum = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManagerPlaylist);
        recyclerAlbum.setLayoutManager(layoutManagerAlbum);

        // Tạo danh sách playlist items (giả sử bạn có class PlaylistItem trong PlaylistAdapter)
        List<PlaylistAdapter.PlaylistItem> playlistItems = new ArrayList<>();
        playlistItems.add(new PlaylistAdapter.PlaylistItem("Song 1", "Artist 1", R.drawable.img));
        playlistItems.add(new PlaylistAdapter.PlaylistItem("Song 2", "Artist 2", R.drawable.img));
        playlistItems.add(new PlaylistAdapter.PlaylistItem("Song 3", "Artist 3", R.drawable.img));
        playlistItems.add(new PlaylistAdapter.PlaylistItem("Song 1", "Artist 1", R.drawable.img));
        playlistItems.add(new PlaylistAdapter.PlaylistItem("Song 1", "Artist 1", R.drawable.img));
        playlistItems.add(new PlaylistAdapter.PlaylistItem("Song 1", "Artist 1", R.drawable.img));
        // ... Thêm các mục khác nếu cần
        List<AlbumsAdapter.AlbumsItem> albumItems = new ArrayList<>();
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", R.drawable.img));
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", R.drawable.img));
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", R.drawable.img));
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", R.drawable.img));
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", R.drawable.img));
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", R.drawable.img));

        // Thiết lập adapter và gán vào RecyclerView
        playlistAdapter = new PlaylistAdapter(playlistItems);
        recyclerView.setAdapter(playlistAdapter);
        albumsAdapter = new AlbumsAdapter(albumItems);
        recyclerAlbum.setAdapter(albumsAdapter);
    }
    private void loadUserData() {
        DBHelper dbHelper = new DBHelper(this);
        User user = dbHelper.getUserById(userId);

        if (user != null) {
            // TODO: Set up the home screen with user data
            // For example:
            // TextView welcomeText = findViewById(R.id.welcome_text);
            // welcomeText.setText("Welcome, " + user.getUsername() + "!");
        } else {
            // Handle error: User not found
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
