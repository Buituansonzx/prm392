package com.example.musicapp.forgotpass;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicapp.DBHelper;
import com.example.musicapp.R;
import com.example.musicapp.controler.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;
    private Button buttonResetPassword;
    private String userPhoneNumber;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password); // Đảm bảo rằng bạn đã tạo layout này

        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);

        // Lấy số điện thoại từ Intent
        userPhoneNumber = getIntent().getStringExtra("USER_PHONE_NUMBER");

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // Thiết lập sự kiện cho nút Đặt lại mật khẩu
        buttonResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(ResetPasswordActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(ResetPasswordActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật mật khẩu trong cơ sở dữ liệu
        if (dbHelper.updateUserPassword(userPhoneNumber, newPassword)) {
            Toast.makeText(ResetPasswordActivity.this, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show();

            // Chuyển hướng đến màn hình đăng nhập sau khi thành công
            Intent intent = new Intent(ResetPasswordActivity.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Đóng ResetPasswordActivity
        } else {
            Toast.makeText(ResetPasswordActivity.this, "Đặt lại mật khẩu thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}