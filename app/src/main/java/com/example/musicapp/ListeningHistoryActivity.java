package com.example.musicapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.model.Song;

import java.util.ArrayList;
import java.util.List;

public class ListeningHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListeningHistoryAdapter adapter;
    private List<Song> songHistory; // Danh sách bài hát lịch sử nghe
    private DBHelper dbHelper; // Đối tượng DBHelper để truy cập cơ sở dữ liệu
    private int userId; // ID của người dùng (có thể lấy từ SharedPreferences hoặc Intent)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening_history);

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // Lấy userId từ Intent (bạn cần gửi userId khi khởi động Activity này)
        userId = getIntent().getIntExtra("USER_ID", -1); // Thay -1 bằng giá trị mặc định nếu cần

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Lấy danh sách bài hát từ lịch sử nghe của người dùng
        songHistory = new ArrayList<>();
        List<DBHelper.ListeningHistoryItem> historyItems = dbHelper.getUserListeningHistory(userId);
        for (DBHelper.ListeningHistoryItem item : historyItems) {
            songHistory.add(new Song(item.getSongId(), item.getSongTitle(), item.getArtist(), 1, 240000, null, null));
        }

        // Tạo Adapter và gán vào RecyclerView
        adapter = new ListeningHistoryAdapter(songHistory); // Truyền danh sách bài hát vào Adapter
        recyclerView.setAdapter(adapter);

        // Khởi tạo nút quay lại
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Quay lại màn hình trước
            }
        });
    }
}