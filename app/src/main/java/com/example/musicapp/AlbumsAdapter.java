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

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder> {

    private List<AlbumsItem> albumItems;

    // Constructor
    public AlbumsAdapter(List<AlbumsItem> albumItems) {
        this.albumItems = albumItems;
    }

    @NonNull
    @Override
    public AlbumsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new AlbumsViewHolder(itemView1);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsViewHolder holder, int position) {
        AlbumsItem currentItem = albumItems.get(position);
        holder.songNameTextView.setText(currentItem.getAlbumName());
        holder.artistNameTextView.setText(currentItem.getArtistName());

        // Đặt hình ảnh cho ImageView. Bạn có thể sử dụng thư viện Glide hoặc Picasso để tải ảnh từ URL.
        holder.songImageView.setImageResource(currentItem.getImageResource());
    }

    @Override
    public int getItemCount() {
        return albumItems.size();
    }

    public static class AlbumsViewHolder extends RecyclerView.ViewHolder {
        public ImageView songImageView;
        public TextView songNameTextView;
        public TextView artistNameTextView;

        public AlbumsViewHolder(@NonNull View itemView) {
            super(itemView);
            songImageView = itemView.findViewById(R.id.playlist_image);
            songNameTextView = itemView.findViewById(R.id.song_name);
            artistNameTextView = itemView.findViewById(R.id.artist_name);
        }
    }

    // Playlist item model class
    public static class AlbumsItem {
        private String albumName;
        private String artistName;
        private int imageResource;

        public AlbumsItem(String albumName, String artistName, int imageResource) {
            this.albumName = albumName;
            this.artistName = artistName;
            this.imageResource = imageResource;
        }

        public String getAlbumName() {
            return albumName;
        }

        public String getArtistName() {
            return artistName;
        }

        public int getImageResource() {
            return imageResource;
        }
    }
}
