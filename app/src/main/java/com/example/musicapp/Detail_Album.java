package com.example.musicapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Detail_Album extends AppCompatActivity {
    private RecyclerView songRecycler;
    private SongAdapter songAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_album);
        songRecycler = findViewById(R.id.recyclersong);
        LinearLayoutManager layoutManagerSong = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        songRecycler.setLayoutManager(layoutManagerSong);
        // Tạo danh sách playlist items (giả sử bạn có class PlaylistItem trong PlaylistAdapter)
        List<SongAdapter.SongItem> songItems = new ArrayList<>();
        songItems.add(new SongAdapter.SongItem("Song 1", "Artist 1", R.drawable.img));
        songItems.add(new SongAdapter.SongItem("Song 2", "Artist 2", R.drawable.img));
        songItems.add(new SongAdapter.SongItem("Song 3", "Artist 3", R.drawable.img));
        songItems.add(new SongAdapter.SongItem("Song 1", "Artist 1", R.drawable.img));
        songItems.add(new SongAdapter.SongItem("Song 1", "Artist 1", R.drawable.img));
        songItems.add(new SongAdapter.SongItem("Song 1", "Artist 1", R.drawable.img));
        songAdapter = new SongAdapter(songItems);
        songRecycler.setAdapter(songAdapter);
    }
}