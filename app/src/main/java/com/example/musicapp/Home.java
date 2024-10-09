package com.example.musicapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;
    private RecyclerView recyclerAlbum;
    private AlbumsAdapter albumsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Đảm bảo tệp layout của bạn tên là activity_home.xml

        // Thiết lập RecyclerView
        recyclerView = findViewById(R.id.recyclerview);
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
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", "Artist 1", R.drawable.img));
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", "Artist 1", R.drawable.img));
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", "Artist 1", R.drawable.img));
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", "Artist 1", R.drawable.img));
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", "Artist 1", R.drawable.img));
        albumItems.add(new AlbumsAdapter.AlbumsItem("Album 1", "Artist 1", R.drawable.img));

        // Thiết lập adapter và gán vào RecyclerView
        playlistAdapter = new PlaylistAdapter(playlistItems);
        recyclerView.setAdapter(playlistAdapter);
        albumsAdapter = new AlbumsAdapter(albumItems);
        recyclerAlbum.setAdapter(albumsAdapter);
    }
}
