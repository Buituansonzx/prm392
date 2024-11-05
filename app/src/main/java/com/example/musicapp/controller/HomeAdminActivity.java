package com.example.musicapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;

public class HomeAdminActivity extends AppCompatActivity {
    private Button btnManageUsers;
    private Button btnManageSongs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnManageUsers = findViewById(R.id.btn_manager_user);
        btnManageSongs = findViewById(R.id.btn_manager_song);
    }

    private void setupClickListeners() {
        btnManageUsers.setOnClickListener(view -> {
            if (checkAdminPermission()) {
                navigateToActivity(ManagerUserActivity.class);
            } else {
                showPermissionDeniedMessage();
            }
        });

        btnManageSongs.setOnClickListener(v -> {
            if (checkAdminPermission()) {
                navigateToActivity(manage_song.class);
            } else {
                showPermissionDeniedMessage();
            }
        });


//        btnProfile.setOnClickListener(v -> navigateToActivity(ProfileActivity.class));
    }

    private void navigateToActivity(Class<?> destinationActivity) {
        try {
            Intent intent = new Intent(this, destinationActivity);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }
    }

    private boolean checkAdminPermission() {
        // TODO: Implement actual admin permission check
        // This should check if the current user has admin privileges
        return true;
    }

    private void showPermissionDeniedMessage() {
        Toast.makeText(this,
                "You don't have permission to access this feature",
                Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this,
                "Error: " + message,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Optional: Handle back button press
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        // Clean up any resources if needed
        super.onDestroy();
    }
}