package com.example.musicapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AlbumManagement extends AppCompatActivity {

    private RecyclerView recyclerAlbum;
    private Button addAlbumButton;
    private ArrayList<String> albumList;
    private AlbumAdapter albumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_management); // Liên kết với layout XML

        // Khởi tạo các thành phần trong giao diện
        recyclerAlbum = findViewById(R.id.recyclerAlbum);
        addAlbumButton = findViewById(R.id.addAlbumButton);

        // Giả lập dữ liệu danh sách album (có thể lấy từ cơ sở dữ liệu hoặc API trong thực tế)
        albumList = new ArrayList<>();
        albumList.add("Album 1");
        albumList.add("Album 2");
        albumList.add("Album 3");

        // Thiết lập RecyclerView
        recyclerAlbum.setLayoutManager(new LinearLayoutManager(this));
        albumAdapter = new AlbumAdapter(albumList);
        recyclerAlbum.setAdapter(albumAdapter);

        // Xử lý sự kiện khi nhấn vào nút "Add Album"
        addAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở một Activity khác hoặc Dialog để thêm album mới
                // Tạm thời hiển thị một album mới cho ví dụ
                albumList.add("New Album");
                albumAdapter.notifyItemInserted(albumList.size() - 1); // Cập nhật dữ liệu
            }
        });

        // Xử lý sự kiện nút quay lại
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước đó
            }
        });
    }
}
