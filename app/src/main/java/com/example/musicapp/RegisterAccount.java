package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterAccount extends AppCompatActivity {

    EditText etUsername, etPassword, etConfirmPassword, etPhoneNumber;
    Button btnRegister;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.confirmPassword);
        etPhoneNumber = findViewById(R.id.phone_number);
        btnRegister = findViewById(R.id.btn_register);

        dbHelper = new DBHelper(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();

                if (validateInput(username, password, confirmPassword, phoneNumber)) {
                    // Assuming all new users are regular users, setting role to "user"
                    String role = "user"; // Can be set to "admin" based on your logic

                    boolean success = dbHelper.addUser(username, password, phoneNumber, role);
                    if (success) {
                        Toast.makeText(RegisterAccount.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        // Navigate to ManagerUserActivity
                        Intent intent = new Intent(RegisterAccount.this, login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterAccount.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean validateInput(String username, String password, String confirmPassword, String phoneNumber) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phoneNumber.matches("^0[0-9]{9}$")) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
