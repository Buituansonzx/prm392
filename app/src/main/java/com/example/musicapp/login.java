package com.example.musicapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private CheckBox rememberCheckBox;
    private Button loginButton;
    private TextView signUpTextView;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views and DBHelper
        phoneNumberEditText = findViewById(R.id.phone_number);
        passwordEditText = findViewById(R.id.passwordd);
        rememberCheckBox = findViewById(R.id.remember);
        loginButton = findViewById(R.id.btn_login);
        signUpTextView = findViewById(R.id.textView);
        dbHelper = new DBHelper(this);

        // Set click listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                boolean rememberMe = rememberCheckBox.isChecked();

                // Perform login logic here
                User user = dbHelper.getUserByPhoneAndPassword(phoneNumber, password);
                if (user != null && "user".equalsIgnoreCase(user.getRole())) {
                    // Successful login as user
                    Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    // Save login state if "Remember Me" is checked
                    if (rememberMe) {
                        // TODO: Implement save login state
                    }

                    // Navigate to Home activity
                    Intent intent = new Intent(login.this, Home.class);
                    intent.putExtra("USER_ID", user.getId());
                    startActivity(intent);
                    finish(); // Close login activity
                } else {
                    // Failed login
                    Toast.makeText(login.this, "Invalid credentials or not a user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for sign up text
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to sign up activity
                 Intent intent = new Intent(login.this, RegisterAccount.class);
                 startActivity(intent);
            }
        });
    }
    private void performLogin(String phoneNumber, String password, boolean rememberMe) {
        if (phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both phone number and password", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            User user = dbHelper.getUserByPhoneAndPassword(phoneNumber, password);
            if (user != null && "user".equalsIgnoreCase(user.getRole())) {
                // Successful login as user
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                if (rememberMe) {
                    saveLoginState(user.getId());
                }

                // Navigate to Home activity
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("USER_ID", user.getId());
                startActivity(intent);
                finish(); // Close login activity
            } else {
                Toast.makeText(this, "Invalid credentials or not a user", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void saveLoginState(int userId) {
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("USER_ID", userId);
        editor.putBoolean("IS_LOGGED_IN", true);
        editor.apply();
    }
}