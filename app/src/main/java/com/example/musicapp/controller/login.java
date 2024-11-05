package com.example.musicapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.DBHelper;
import com.example.musicapp.R;
import com.example.musicapp.forgotpass.ForgotPassword;
import com.example.musicapp.model.User;

public class login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    public static final String EXTRA_USER_ID = "USER_ID";

    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private DBHelper dbHelper;
    private TextView forgotPassTextView;
    // int a ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        dbHelper = new DBHelper(this);

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
        forgotPassTextView.setOnClickListener(new View.OnClickListener() { // Add this listener
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    private void initializeViews() {
        phoneNumberEditText = findViewById(R.id.phone_number);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);
        signUpTextView = findViewById(R.id.textView);
        forgotPassTextView = findViewById(R.id.forgotPass);
    }

    private void performLogin() {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both phone number and password", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Log.d(TAG, "Login button clicked");
            Log.d(TAG, "Phone: " + phoneNumber + ", Password: " + password);

            User user = dbHelper.getUserByPhoneAndPassword(phoneNumber, password);
            Log.d(TAG, "User found: " + (user != null));

            if (user != null && user.getRole() != null) {
                Log.d(TAG, "User role: " + user.getRole());
                if ("user".equalsIgnoreCase(user.getRole())) {
                    Toast.makeText(this, "Login Successful as User", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, Home.class);
                    intent.putExtra(EXTRA_USER_ID, user.getId());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if ("admin".equalsIgnoreCase(user.getRole())) {
                    Toast.makeText(this, "Login Successful as Admin", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, HomeAdminActivity.class);
                    intent.putExtra(EXTRA_USER_ID, user.getId());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Invalid user role", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during login", e);
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}