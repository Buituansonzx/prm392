package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.controller.Home;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Thiết lập item đã chọn là User
        bottomNavigationView.setSelectedItemId(R.id.nav_user);

        // Xử lý sự kiện chọn item
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if(item.getItemId() == R.id.nav_home){
                intent = new Intent(this, Home.class);
            }else if(item.getItemId() == R.id.nav_search){
                intent = new Intent(this, SearchActivity.class);
            }else if(item.getItemId() == R.id.nav_setting){
                intent = new Intent(this, SettingActivity.class);
            }else if(item.getItemId() == R.id.nav_user){
                return true;
            }
            if (intent != null) {
                startActivity(intent);
                finish();
            }
            return true;
        });
    }
}