package com.example.musicapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.model.Album;

public class Detail_Album extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_album); // Thay thế với layout của bạn

        // Nhận dữ liệu từ Intent
        Album album = (Album) getIntent().getSerializableExtra("album");
        if (album != null) {
            // Xử lý dữ liệu album (ví dụ: hiển thị thông tin trên giao diện)
        }
    }
}
