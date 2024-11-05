package com.example.musicapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.musicapp.controller.Home;
import com.example.musicapp.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1; // Mã yêu cầu cho việc chọn hình ảnh
    private ImageView imageViewProfile;
    private EditText editTextUsername, editTextPassword; // Thêm trường mật khẩu
    private TextView textViewPhone;
    private Button buttonUpdate, buttonBack, buttonChangeImage;
    private User currentUser; // Biến lưu thông tin người dùng
    private DBHelper dbHelper; // Đối tượng DBHelper
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user); // Đảm bảo đây là layout bạn đã cập nhật

        // Khởi tạo các view
        imageViewProfile = findViewById(R.id.imageViewProfile);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword); // Trường mật khẩu
        textViewPhone = findViewById(R.id.textViewPhone);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonBack = findViewById(R.id.buttonBack);
        buttonChangeImage = findViewById(R.id.buttonChangeImage); // Nếu bạn vẫn muốn giữ nút thay đổi hình ảnh

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // Nhận userId từ Intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc activity nếu không có userId hợp lệ
            return;
        }

        // Tải thông tin người dùng
        loadUserData();

        // Thiết lập BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_user);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.nav_user) {
                return true;
            } else if (item.getItemId() == R.id.nav_home) {
                intent = new Intent(this, Home.class);
                intent.putExtra("USER_ID", userId);
            } else if (item.getItemId() == R.id.nav_setting) {
                intent = new Intent(this, SettingActivity.class);
                intent.putExtra("USER_ID", userId);
            } else if (item.getItemId() == R.id.nav_search) {
                intent = new Intent(this, SearchActivity.class);
                intent.putExtra("USER_ID", userId);
            }
            if (intent != null) {
                startActivity(intent);
                finish();
            }
            return true;
        });

        // Xử lý sự kiện khi nhấn nút cập nhật
        buttonUpdate.setOnClickListener(v -> updateUserInfo());

        // Xử lý sự kiện khi nhấn nút quay lại
        buttonBack.setOnClickListener(v -> finish()); // Đóng activity

        // Xử lý sự kiện khi nhấn nút thay đổi hình ảnh
        buttonChangeImage.setOnClickListener(v -> openGallery());
    }

    private void loadUserData() {
        // Lấy thông tin người dùng từ DB dựa trên userId được truyền vào Intent
        currentUser = dbHelper.getUserById(userId);

        if (currentUser != null) {
            // Hiển thị thông tin người dùng
            Log.d("UserActivity", "User data loaded successfully: " + currentUser.getUsername());
            editTextUsername.setText(currentUser.getUsername());
            textViewPhone.setText(currentUser.getPhoneNumber());
            if (currentUser.getImage() != null) {
                // Chuyển đổi byte[] thành Bitmap và hiển thị
                Bitmap bitmap = BitmapFactory.decodeByteArray(currentUser.getImage(), 0, currentUser.getImage().length);
                imageViewProfile.setImageBitmap(bitmap);
            } else {
                Log.d("UserActivity", "No image data found for user.");
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            Log.d("UserActivity", "User data not found for ID: " + userId);
        }
    }

    private void updateUserInfo() {
        String username = editTextUsername.getText().toString().trim();
        String phoneNumber = textViewPhone.getText().toString(); // Giữ số điện thoại không thay đổi
        String password = editTextPassword.getText().toString().trim(); // Lấy mật khẩu

        // Kiểm tra dữ liệu nhập
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền tất cả các trường", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin người dùng
        currentUser.setUsername(username);
        currentUser.setPhoneNumber(phoneNumber); // Số điện thoại không thay đổi
        currentUser.setPassword(password); // Cập nhật mật khẩu mới

        boolean isUpdated = dbHelper.updateUser(currentUser);
        if (isUpdated) {
            Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
            Log.d("UserActivity", "User information updated successfully.");
        } else {
            Toast.makeText(this, "Cập nhật thông tin không thành công", Toast.LENGTH_SHORT).show();
            Log.d("UserActivity", "Failed to update user information.");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imageViewProfile.setImageURI(imageUri); // Hiển thị hình ảnh trên ImageView

            // Chuyển đổi URI thành byte[]
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageBytes = stream.toByteArray();
                currentUser.setImage(imageBytes); // Lưu trữ ảnh vào đối tượng User
                Log.d("UserActivity", "Image selected and converted to byte array.");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("UserActivity", "Error converting image to byte array.", e);
            }
        }
    }
}
