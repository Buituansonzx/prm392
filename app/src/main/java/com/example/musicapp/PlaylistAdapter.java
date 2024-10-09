package com.example.musicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapp.R;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<PlaylistItem> playlistItems;

    // Constructor
    public PlaylistAdapter(List<PlaylistItem> playlistItems) {
        this.playlistItems = playlistItems;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistItem currentItem = playlistItems.get(position);
        holder.songNameTextView.setText(currentItem.getSongName());
        holder.artistNameTextView.setText(currentItem.getArtistName());

        // Đặt hình ảnh cho ImageView. Bạn có thể sử dụng thư viện Glide hoặc Picasso để tải ảnh từ URL.
        holder.songImageView.setImageResource(currentItem.getImageResource());
    }

    @Override
    public int getItemCount() {
        return playlistItems.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        public ImageView songImageView;
        public TextView songNameTextView;
        public TextView artistNameTextView;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            songImageView = itemView.findViewById(R.id.playlist_image);
            songNameTextView = itemView.findViewById(R.id.song_name);
            artistNameTextView = itemView.findViewById(R.id.artist_name);
        }
    }

    // Playlist item model class
    public static class PlaylistItem {
        private String songName;
        private String artistName;
        private int imageResource;

        public PlaylistItem(String songName, String artistName, int imageResource) {
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
