package com.example.musicapp.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.SongAdapter;
import com.example.musicapp.model.Song;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Detail_Album extends AppCompatActivity {
    private RecyclerView songRecycler;
    private SongAdapter songAdapter;
    private ImageView imageView;
    private final List<Song> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_album);

        // Khởi tạo views
        imageView = findViewById(R.id.imageView2);
        songRecycler = findViewById(R.id.recyclersong);

        // Thiết lập LayoutManager cho RecyclerView
        LinearLayoutManager layoutManagerSong = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        songRecycler.setLayoutManager(layoutManagerSong);

        // Tạo danh sách bài hát mẫu
        createSampleSongs();

        // Khởi tạo adapter với danh sách bài hát và listener
        songAdapter = new SongAdapter(songs, new SongAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                // Xử lý khi bài hát được click
                Intent intent = new Intent(Detail_Album.this, Play_song.class);
                intent.putExtra("song", song);
                startActivity(intent);
            }

            @Override
            public void onSongOptionsClick(Song song, View view) {
                // Hiển thị popup menu
                showPopupMenu(song, view);
            }
        });

        // Gán adapter cho RecyclerView
        songRecycler.setAdapter(songAdapter);
    }

    private void showPopupMenu(Song song, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.song_item_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    // Xử lý khi chọn edit
                    Toast.makeText(Detail_Album.this, "Edit: " + song.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.action_delete) {
                    // Xử lý khi chọn delete
                    deleteSong(song);
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }

    private void deleteSong(Song song) {
        songs.remove(song);
        songAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Deleted: " + song.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void createSampleSongs() {
        byte[] imageBytes = getImageAsByteArray(R.drawable.default_song_image);
        songs.add(new Song(1, "Song 1", "Artist 1", 1, 180000, "https://example.com/song1.mp3", imageBytes));
        songs.add(new Song(2, "Song 2", "Artist 2", 1, 200000, "https://example.com/song2.mp3", imageBytes));
        songs.add(new Song(3, "Song 3", "Artist 3", 1, 220000, "https://example.com/song3.mp3", imageBytes));
        songs.add(new Song(4, "Song 4", "Artist 4", 1, 190000, "https://example.com/song4.mp3", imageBytes));
        songs.add(new Song(5, "Song 5", "Artist 5", 1, 210000, "https://example.com/song5.mp3", imageBytes));
    }

    private byte[] getImageAsByteArray(int resourceId) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
            if (bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                return stream.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void backToHome(View v) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}