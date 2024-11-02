package com.example.musicapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapp.model.Album;
import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder> {

    private List<Album> albumList;
    private OnAlbumClickListener listener;
    private DBHelper dbHelper; // Tham chiếu đến DBHelper

    public AlbumsAdapter(List<Album> albumList, OnAlbumClickListener listener, DBHelper dbHelper) {
        this.albumList = albumList;
        this.listener = listener;
        this.dbHelper = dbHelper; // Khởi tạo DBHelper
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.albumTitle.setText(album.getTitle());
        holder.albumReleaseDate.setText("Release Date: " + album.getReleaseDate());

        // Kiểm tra mảng byte hình ảnh không null và không rỗng
        if (album.getImage() != null && album.getImage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(album.getImage(), 0, album.getImage().length);
            holder.albumImage.setImageBitmap(bitmap);
        } else {
            // Nếu không có hình ảnh, có thể đặt hình ảnh mặc định
            holder.albumImage.setImageResource(R.drawable.icon_album); // Đặt hình ảnh mặc định nếu không có
        }

        // Xử lý sự kiện click cho item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAlbumClick(album);
            }
        });

        // Xử lý sự kiện click cho nút xóa
        holder.deleteAlbumButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Lấy vị trí hiện tại
            if (currentPosition != RecyclerView.NO_POSITION) {
                // Xóa album khỏi cơ sở dữ liệu
                if (dbHelper.deleteAlbum(album.getId())) {
                    // Nếu xóa thành công, xóa album khỏi danh sách
                    albumList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, albumList.size());
                } else {
                    // Có thể xử lý lỗi nếu không xóa thành công
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {

        TextView albumTitle;
        TextView albumReleaseDate;
        ImageView albumImage;
        Button deleteAlbumButton; // Tham chiếu đến nút xóa

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumTitle = itemView.findViewById(R.id.albumTitle);
            albumReleaseDate = itemView.findViewById(R.id.albumReleaseDate);
            albumImage = itemView.findViewById(R.id.imageView);
            deleteAlbumButton = itemView.findViewById(R.id.deleteAlbumButton); // Khởi tạo tham chiếu
        }
    }

    // Giao diện cho click listener
    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }
}
