package com.example.musicapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddSongActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private ImageView songImageView;
    private Button selectImageButton;
    private EditText titleEditText;
    private EditText artistEditText;
    private EditText songUrlEditText;
    private Button saveSongButton;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        dbHelper = new DBHelper(this);

        initializeViews();
        setupToolbar();
        setupListeners();
    }

    private void initializeViews() {
        songImageView = findViewById(R.id.songImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        titleEditText = findViewById(R.id.titleEditText);
        artistEditText = findViewById(R.id.artistEditText);
        songUrlEditText = findViewById(R.id.songUrlEditText);
        saveSongButton = findViewById(R.id.saveSongButton);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Thêm bài hát mới");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListeners() {
        selectImageButton.setOnClickListener(v -> checkPermissionAndOpenImageChooser());
        saveSongButton.setOnClickListener(v -> saveSong());
    }

    private void checkPermissionAndOpenImageChooser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            openImageChooser();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "Quyền bị từ chối, không thể chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                songImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSong() {
        String title = titleEditText.getText().toString().trim();
        String artist = artistEditText.getText().toString().trim();
        String songUrl = songUrlEditText.getText().toString().trim();

        if (title.isEmpty() || artist.isEmpty() || songUrl.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] imageBytes = getImageBytes();
        int albumId = 1; // Giả sử albumId là 1
        int duration = 0; // Giả sử duration là 0

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu bài hát...");
        progressDialog.show();

        new Thread(() -> {
            boolean isSuccess = dbHelper.addSong(title, artist, albumId, duration, songUrl, imageBytes);
            runOnUiThread(() -> {
                progressDialog.dismiss();
                if (isSuccess) {
                    Toast.makeText(AddSongActivity.this, "Đã lưu bài hát thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddSongActivity.this, "Lỗi khi lưu bài hát", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private byte[] getImageBytes() {
        if (songImageView.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) songImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}