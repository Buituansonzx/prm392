package com.example.musicapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle ;
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

        initializeViews();
        dbHelper = new DBHelper(this);

        checkIfLoggedIn();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, RegisterAccount.class);
                startActivity(intent);
            }
        });
    }

    private void initializeViews() {
        phoneNumberEditText = findViewById(R.id.phone_number);
        passwordEditText = findViewById(R.id.password);
        rememberCheckBox = findViewById(R.id.remember);
        loginButton = findViewById(R.id.btn_login);
        signUpTextView = findViewById(R.id.textView);
    }

    private void checkIfLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);
        if (isLoggedIn) {
            int userId = prefs.getInt("USER_ID", -1);
            if (userId != -1) {
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return;
            }
        }
    }

    private void performLogin() {
        String phoneNumber = phoneNumberEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean rememberMe = rememberCheckBox.isChecked();

        if (phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both phone number and password", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            User user = dbHelper.getUserByPhoneAndPassword(phoneNumber, password);
            if (user != null && "user".equalsIgnoreCase(user.getRole())) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                if (rememberMe) {
                    saveLoginState(user.getId());
                }

                Intent intent = new Intent(this, Home.class);
                intent.putExtra("USER_ID", user.getId());
                startActivity(intent);
                finish();
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