package com.example.musicapp;

import android.graphics.Bitmap;
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

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songs;
    private OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }
    public SongAdapter(List<Song> songs,OnSongClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_in_album, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song currentSong = songs.get(position);
        holder.songNameTextView.setText(currentSong.getTitle());
        holder.artistNameTextView.setText(currentSong.getArtist());

        if (currentSong.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(currentSong.getImage(), 0, currentSong.getImage().length);
            holder.songImageView.setImageBitmap(bitmap);
        } else {
            // Set a default image if no image is available
            holder.songImageView.setImageResource(R.drawable.default_song_image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(currentSong);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
    public void updateList(List<Song> newList) {
        this.songs = newList;
        notifyDataSetChanged();
    }
    public static class SongViewHolder extends RecyclerView.ViewHolder {
        public ImageView songImageView;
        public TextView songNameTextView;
        public TextView artistNameTextView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songImageView = itemView.findViewById(R.id.playlist_image);
            songNameTextView = itemView.findViewById(R.id.song_name);
            artistNameTextView = itemView.findViewById(R.id.artist_name);
        }
    }
}
