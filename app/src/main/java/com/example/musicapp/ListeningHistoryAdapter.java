package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.controller.Play_song;
import com.example.musicapp.model.Song;

import java.util.List;

public class ListeningHistoryAdapter extends RecyclerView.Adapter<ListeningHistoryAdapter.ListeningHistoryViewHolder> {

    private final List<Song> songList;
    private final Context context;

    public ListeningHistoryAdapter(List<Song> songList, Context context) {
        if (songList == null) {
            throw new IllegalArgumentException("songList cannot be null");
        }
        this.songList = songList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListeningHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listening_history, parent, false);
        return new ListeningHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListeningHistoryViewHolder holder, int position) {
        Song song = songList.get(position);

        // Kiểm tra song không phải null trước khi gán giá trị
        if (song != null) {
            holder.titleTextView.setText(song.getTitle());
            holder.artistTextView.setText(song.getArtist());
        }

        // Xử lý sự kiện khi bấm nút chi tiết
        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, Play_song.class);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class ListeningHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView artistTextView;
        Button btnDetail;

        ListeningHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.song_title);
            artistTextView = itemView.findViewById(R.id.artist_name);
            btnDetail = itemView.findViewById(R.id.btn_detail); // Đảm bảo bạn có button trong layout
        }
    }
}
