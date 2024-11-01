package com.example.musicapp.controler;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.DBHelper;
import com.example.musicapp.Play_song;
import com.example.musicapp.R;
import com.example.musicapp.SongAdapter;
import com.example.musicapp.model.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class manage_song extends AppCompatActivity {
    private static final int ADD_SONG_REQUEST_CODE = 1;

    private RecyclerView songRecyclerView;
    private SearchView searchView;
    private FloatingActionButton addSongFab;
    private TextView emptyStateView;
    private ProgressBar loadingProgressBar;
    private List<Song> songList;
    private SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_song);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadSongs();
        setupListeners();
    }

    private void initializeViews() {
        songRecyclerView = findViewById(R.id.songRecyclerView);
        searchView = findViewById(R.id.searchView);
        addSongFab = findViewById(R.id.addSongFab);
        emptyStateView = findViewById(R.id.emptyStateView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        songList = new ArrayList<>();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý bài hát");
        }
    }

    private void setupRecyclerView() {
        songAdapter = new SongAdapter(songList, new SongAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                Intent intent = new Intent(manage_song.this, Play_song.class);
                intent.putExtra("song", song);
                startActivity(intent);
            }

            @Override
            public void onSongOptionsClick(Song song, View view) {
                showPopupMenu(song, view);
            }
        });
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songRecyclerView.setAdapter(songAdapter);
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
                    editSong(song);
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

    private void editSong(Song song) {
        // Implement logic to edit song
        Toast.makeText(this, "Edit song: " + song.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void deleteSong(Song song) {
        DBHelper db = new DBHelper(this);
        db.deleteSong(song.getId());
        songList.remove(song);
        songAdapter.notifyDataSetChanged();
        updateEmptyState();
        Toast.makeText(this, "Deleted: " + song.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void loadSongs() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        DBHelper db = new DBHelper(this);
        songList.clear();
        songList.addAll(db.getAllSongs());
        songAdapter.notifyDataSetChanged();
        loadingProgressBar.setVisibility(View.GONE);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (songList.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
            songRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            songRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSongs(newText);
                return true;
            }
        });

        addSongFab.setOnClickListener(v -> {
            Intent intent = new Intent(manage_song.this, AddSongActivity.class);
            startActivityForResult(intent, ADD_SONG_REQUEST_CODE);
        });
    }

    private void filterSongs(String query) {
        List<Song> filteredList = new ArrayList<>();
        for (Song song : songList) {
            if (song.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(song);
            }
        }
        songAdapter.updateList(filteredList);
        updateEmptyState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_SONG_REQUEST_CODE && resultCode == RESULT_OK) {
            loadSongs(); // Tải lại danh sách bài hát sau khi thêm thành công
            Toast.makeText(this, "Thêm bài hát thành công", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}