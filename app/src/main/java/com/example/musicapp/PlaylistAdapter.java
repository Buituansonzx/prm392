package com.example.musicapp;

import android.content.Intent;
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

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<PlaylistItem> playlistItems;

    public PlaylistAdapter(List<PlaylistItem> playlistItems) {
        this.playlistItems = playlistItems;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistItem currentItem = playlistItems.get(position);
        holder.bind(currentItem);
    }

    @Override
    public int getItemCount() {
        return playlistItems.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private final ImageView playlistImage;
        private final TextView songNameText;
        private final TextView artistNameText;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.playlist_image);
            songNameText = itemView.findViewById(R.id.song_name);
            artistNameText = itemView.findViewById(R.id.artist_name);
        }

        public void bind(PlaylistItem item) {
            Song song = item.getSong();

            // Set text
            songNameText.setText(song.getTitle());
            artistNameText.setText(song.getArtist());

            // Set image
            if (song.getImage() != null && song.getImage().length > 0) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(
                            song.getImage(),
                            0,
                            song.getImage().length
                    );
                    playlistImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    playlistImage.setImageResource(R.drawable.img);
                }
            } else {
                playlistImage.setImageResource(R.drawable.img);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), Play_song.class);
                intent.putExtra("song", song);
                v.getContext().startActivity(intent);
            });
        }
    }

    public static class PlaylistItem {
        private final Song song;

        public PlaylistItem(Song song) {
            this.song = song;
        }

        public Song getSong() {
            return song;
        }
    }

    // Helper methods
    public void updatePlaylist(List<PlaylistItem> newItems) {
        this.playlistItems = newItems;
        notifyDataSetChanged();
    }

    public void addItem(PlaylistItem item) {
        this.playlistItems.add(item);
        notifyItemInserted(playlistItems.size() - 1);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < playlistItems.size()) {
            playlistItems.remove(position);
            notifyItemRemoved(position);
        }
    }
}