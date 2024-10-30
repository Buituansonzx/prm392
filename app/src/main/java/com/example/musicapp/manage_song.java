package com.example.musicapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.model.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class manage_song extends AppCompatActivity {
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
        songAdapter = new SongAdapter(songList, song -> {
            Toast.makeText(this, "Đã chọn bài hát: " + song.getTitle(), Toast.LENGTH_SHORT).show();
        });
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songRecyclerView.setAdapter(songAdapter);
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
            Toast.makeText(this, "Chức năng thêm bài hát đang được phát triển", Toast.LENGTH_SHORT).show();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}