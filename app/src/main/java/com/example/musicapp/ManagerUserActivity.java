package com.example.musicapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ManagerUserActivity extends AppCompatActivity {

    private static final String TAG = "ManagerUserActivity";
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_user);

        userRecyclerView = findViewById(R.id.userRecyclerView);
        dbHelper = new DBHelper(this);

        setupRecyclerView();
        loadUserData();
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(new ArrayList<>());
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setAdapter(userAdapter);
    }

    private void loadUserData() {
        new Thread(() -> {
            List<User> users = fetchUsersFromDatabase();
            Log.d(TAG, "Users fetched (including test data): " + users.toString());
            runOnUiThread(() -> {
                if (users.isEmpty()) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                    userAdapter.setUsers(users);
                    Log.d(TAG, "Users set to adapter: " + users.toString());
                }
            });
        }).start();
    }

    private List<User> fetchUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dbHelper.getUserDetails();
            Log.d(TAG, "Cursor returned from database: " + (cursor != null ? cursor.getCount() : "null"));
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    User user = new User(
                            cursor.getInt(0),  // id
                            cursor.getString(1),  // username
                            cursor.getString(2),  // password
                            cursor.getString(3),  // phone
                            cursor.getString(4)   // role
                    );
                    users.add(user);
                    Log.d(TAG, "User added: " + user.toString());
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user data", e);
            runOnUiThread(() -> Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return users;
    }

    private void showEmptyState() {
        Log.d(TAG, "Showing empty state");
        userRecyclerView.setVisibility(View.GONE);
        findViewById(R.id.emptyStateView).setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        Log.d(TAG, "Hiding empty state");
        userRecyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.emptyStateView).setVisibility(View .GONE);
    }
}