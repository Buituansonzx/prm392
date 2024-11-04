package com.example.musicapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.musicapp.controller.Home;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingActivity extends AppCompatActivity {

    private Switch switchDarkMode, switchNotifications;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchNotifications = findViewById(R.id.switch_notifications);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Thiết lập item đã chọn là Setting
        bottomNavigationView.setSelectedItemId(R.id.nav_setting);

        // Xử lý sự kiện chọn item trong BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;

            if (item.getItemId() == R.id.nav_search) {
                intent = new Intent(this, SearchActivity.class);
            } else if (item.getItemId() == R.id.nav_home) {
                intent = new Intent(this, Home.class);
            } else if (item.getItemId() == R.id.nav_setting) {
                return true; // Đã ở SettingActivity
            } else if (item.getItemId() == R.id.nav_user) {
                intent = new Intent(this, UserActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                finish(); // Kết thúc SettingActivity để tránh chồng lớp
            }
            return true;
        });

        // Lấy trạng thái Dark Mode từ SharedPreferences
//        boolean isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
//        switchDarkMode.setChecked(isDarkModeEnabled);
//
//        // Áp dụng chế độ Dark Mode khi khởi động Activity
//        AppCompatDelegate.setDefaultNightMode(isDarkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//
//        // Lắng nghe sự thay đổi trạng thái của switch Dark Mode
//        switchDarkMode.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            if (isChecked) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                editor.putBoolean("dark_mode", true);
//                Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
//            } else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                editor.putBoolean("dark_mode", false);
//                Toast.makeText(this, "Dark Mode Disabled", Toast.LENGTH_SHORT).show();
//            }
//            editor.apply(); // Lưu trạng thái Dark Mode
//        });
//
//        // Xử lý logic cho switch Notifications
//        switchNotifications.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
//            if (isChecked) {
//                Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
