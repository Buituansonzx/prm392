package com.example.musicapp;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.model.Song;

import java.util.List;

public class ListeningHistoryAdapter extends RecyclerView.Adapter<ListeningHistoryAdapter.ViewHolder> {

    private List<Song> songList; // Danh sách bài hát

    public ListeningHistoryAdapter(List<Song> songList) {
        this.songList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.songNameTextView.setText(song.getTitle()); // Hiển thị tên bài hát
        holder.singerNameTextView.setText(song.getArtist()); // Hiển thị tên ca sĩ
        holder.timePostTextView.setText(song.getDurationFormatted()); // Hiển thị thời gian bài hát

        // Nếu bài hát có hình ảnh, hiển thị hình ảnh đó
        if (song.getImage() != null) {
            holder.imgSongImageView.setImageBitmap(BitmapFactory.decodeByteArray(song.getImage(), 0, song.getImage().length));
        } else {
            holder.imgSongImageView.setImageResource(R.drawable.img); // Hình ảnh mặc định nếu không có
        }
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0; // Tránh NullPointerException
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSongImageView;
        TextView songNameTextView;
        TextView singerNameTextView;
        TextView timePostTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSongImageView = itemView.findViewById(R.id.img_song);
            songNameTextView = itemView.findViewById(R.id.song_name);
            singerNameTextView = itemView.findViewById(R.id.singer_name);
            timePostTextView = itemView.findViewById(R.id.time_post);
        }
    }

    public void updateData(List<Song> newSongList) {
        this.songList = newSongList;
        notifyDataSetChanged(); // Cập nhật dữ liệu cho RecyclerView
    }
}
