package com.example.musicapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListeningHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListeningHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening_history);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tạo Adapter (chưa có dữ liệu thực tế)
        adapter = new ListeningHistoryAdapter();
        recyclerView.setAdapter(adapter);
    }
}
