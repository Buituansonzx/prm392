package com.example.musicapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.model.Album;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AlbumManagement extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private RecyclerView recyclerViewAlbums;
    private Button addAlbumButton, chooseImageButton;
    private EditText titleEditText;
    private ImageView albumImageView;
    private byte[] albumImage;
    private DBHelper dbHelper;
    private AlbumsAdapter albumsAdapter;
    private ArrayList<Album> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_management);

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // Nhận userId từ Intent
        int userId = getIntent().getIntExtra("USER_ID", -1);

        // Khởi tạo UI
        recyclerViewAlbums = findViewById(R.id.recyclerViewAlbums);
        addAlbumButton = findViewById(R.id.addAlbumButton);
        titleEditText = findViewById(R.id.titleEditText);
        albumImageView = findViewById(R.id.albumImageView);
        chooseImageButton = findViewById(R.id.chooseImageButton);

        // Khởi tạo danh sách album và adapter
        albumList = new ArrayList<>();
        albumsAdapter = new AlbumsAdapter(albumList, album -> {
            // Xử lý khi album được click
            Intent intent = new Intent(AlbumManagement.this, Detail_Album.class);
            intent.putExtra("album", album);
            startActivity(intent);
        }, dbHelper);

        recyclerViewAlbums.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAlbums.setAdapter(albumsAdapter);

        // Lấy danh sách album từ cơ sở dữ liệu
        loadAlbums(userId);

        // Xử lý nút "Thêm Album"
        addAlbumButton.setOnClickListener(v -> addAlbum(userId));

        // Xử lý nút chọn hình ảnh
        chooseImageButton.setOnClickListener(v -> openFileChooser());
    }

    private void loadAlbums(int userId) {
        // Lấy danh sách album từ cơ sở dữ liệu
        albumList.clear();
        albumList.addAll(dbHelper.getAlbumsByUserId(userId));
        albumsAdapter.notifyDataSetChanged();
    }

    private void addAlbum(int userId) {
        String title = titleEditText.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề album", Toast.LENGTH_SHORT).show();
            return;
        }
        if (albumImage == null) {
            Toast.makeText(this, "Vui lòng chọn hình ảnh cho album", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy ngày hiện tại
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        String releaseDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            releaseDate = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        if (dbHelper.addAlbum(title, albumImage, releaseDate, userId)) {
            Toast.makeText(this, "Album đã được thêm!", Toast.LENGTH_SHORT).show();
            titleEditText.setText(""); // Xóa trường nhập tiêu đề
            albumImageView.setImageResource(R.drawable.icon_album); // Đặt hình ảnh mặc định
            albumImage = null; // Đặt lại hình ảnh album

            // Tải lại danh sách album sau khi thêm thành công
            loadAlbums(userId);
        } else {
            Toast.makeText(this, "Thêm album thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                albumImageView.setImageBitmap(bitmap);
                albumImage = bitmapToByteArray(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Không thể chọn hình ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
