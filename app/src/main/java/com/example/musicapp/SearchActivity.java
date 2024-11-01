package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.controler.Home;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Thiết lập item đã chọn là Home
        bottomNavigationView.setSelectedItemId(R.id.nav_search);

        // Xử lý sự kiện chọn item
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if(item.getItemId() == R.id.nav_search){
                return true;
            }else if(item.getItemId() == R.id.nav_home){
                intent = new Intent(this, Home.class);
            }else if(item.getItemId() == R.id.nav_setting){
                intent = new Intent(this, SettingActivity.class);
            }else if(item.getItemId() == R.id.nav_user){
                intent = new Intent(this, UserActivity.class);
            }
            if (intent != null) {
                startActivity(intent);
                finish();
            }
            return true;
        });
    }
}