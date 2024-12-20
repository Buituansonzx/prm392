package com.example.musicapp;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.controller.Home;
import com.example.musicapp.controller.Play_song;
import com.example.musicapp.model.Song;
import com.example.musicapp.SongAdapter; // Đảm bảo đã nhập đúng gói
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SongAdapter.OnSongClickListener {

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<Song> songList; // Danh sách bài hát gốc
    private List<Song> filteredSongs; // Danh sách bài hát sau khi tìm kiếm
    private static final String TAG = "Search";
    private DBHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Nhận userId từ Intent
        userId = getIntent().getIntExtra("USER_ID", -1); // Nhận userId
        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc activity nếu không có userId hợp lệ
        }

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo danh sách bài hát
        dbHelper = new DBHelper(this); // Khởi tạo DBHelper ở đây
        songList = getSongList(); // Lấy danh sách bài hát từ DBHelper
        filteredSongs = new ArrayList<>(songList); // Khởi tạo danh sách tìm kiếm

        // Khởi tạo và thiết lập adapter
        songAdapter = new SongAdapter(filteredSongs, this);
        recyclerView.setAdapter(songAdapter);

        // Tìm kiếm bài hát (giả sử có EditText cho tìm kiếm)
        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterSongs(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Thêm OnEditorActionListener để xử lý sự kiện nút OK
        searchEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                String query = searchEditText.getText().toString();
                filterSongs(query); // Gọi phương thức tìm kiếm

                hideKeyboard(); // Ẩn bàn phím sau khi nhấn OK
                return true; // Ngăn chặn hành động mặc định (xuống dòng)
            }
            return false;
        });

        // Thiết lập Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if(item.getItemId() == R.id.nav_search){
                return true;
            }else if(item.getItemId() == R.id.nav_home){
                intent = new Intent(this, Home.class);
                intent.putExtra("USER_ID", userId); // Truyền userId qua Intent
            }else if(item.getItemId() == R.id.nav_setting){
                intent = new Intent(this, SettingActivity.class);
                intent.putExtra("USER_ID", userId);
            }else if(item.getItemId() == R.id.nav_user){
                intent = new Intent(this, UserActivity.class);
                intent.putExtra("USER_ID", userId);
            }
            if (intent != null) {
                startActivity(intent);
                finish();
            }
            return true;
        });
    }

    private List<Song> getSongList() {
        // Thay thế bằng logic của bạn để lấy danh sách bài hát
        List<Song> allSongs = dbHelper.getAllSongs();
        Log.d(TAG, "All songs loaded: " + allSongs.size());
        return allSongs; // Trả về danh sách bài hát từ DBHelper

    }

    private void filterSongs(String query) {
        filteredSongs.clear();
        if (query.isEmpty()) {
            filteredSongs.addAll(songList);
        } else {
            for (Song song : songList) {
                if (song.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        song.getArtist().toLowerCase().contains(query.toLowerCase())) {
                    filteredSongs.add(song);
                }
            }
        }
        songAdapter.updateList(filteredSongs); // Cập nhật danh sách hiển thị
    }
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onSongClick(Song song) {
        Intent intent = new Intent(this, Play_song.class); // Thay "Play_song.class" bằng lớp phát nhạc của bạn
        intent.putExtra("position", songList.indexOf(song)); // Truyền vị trí của bài hát trong danh sách
        intent.putExtra("USER_ID", userId); // Nếu bạn cần truyền ID người dùng
        startActivity(intent);
    }

    @Override
    public void onSongOptionsClick(Song song, View view) {
        // Xử lý sự kiện khi nhấn vào nút menu
    }


    //Test git
}
