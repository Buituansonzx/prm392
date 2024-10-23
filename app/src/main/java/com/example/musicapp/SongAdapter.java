package com.example.musicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<SongItem> songItems;

    public SongAdapter(List<SongItem> songItems) {
        this.songItems = songItems;
    }


    @NonNull
    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_in_album, parent, false);
        return new SongAdapter.SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.SongViewHolder holder, int position) {
        SongAdapter.SongItem currentItem = songItems.get(position);
        holder.songNameTextView.setText(currentItem.getSongName());
        holder.artistNameTextView.setText(currentItem.getArtistName());

    }

    @Override
    public int getItemCount() {
        return songItems.size();
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
    public static class SongItem {
        private String songName;
        private String artistName;
        private int imageResource;

        public SongItem(String songName, String artistName, int imageResource) {
            this.songName = songName;
            this.artistName = artistName;
            this.imageResource = imageResource;
        }

        public String getSongName() {
            return songName;
        }

        public String getArtistName() {
            return artistName;
        }

        public int getImageResource() {
            return imageResource;
        }
    }
}
