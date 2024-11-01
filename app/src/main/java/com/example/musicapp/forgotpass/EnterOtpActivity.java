package com.example.musicapp.forgotpass;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class EnterOtpActivity extends AppCompatActivity {
    private EditText otpEditText;
    private Button verifyOtpButton;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        otpEditText = findViewById(R.id.otpEditText);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        verificationId = getIntent().getStringExtra("verificationId");

        verifyOtpButton.setOnClickListener(view -> {
            String otp = otpEditText.getText().toString().trim();
            if (TextUtils.isEmpty(otp)) {
                Toast.makeText(EnterOtpActivity.this, "Vui lòng nhập OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
            signInWithPhoneAuthCredential(credential);
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Xác minh OTP thành công
                        onVerificationSuccess();
                    } else {
                        // Xác minh OTP thất bại
                        String message = task.getException() != null ? task.getException().getMessage() : "Xác minh thất bại";
                        Toast.makeText(EnterOtpActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onVerificationSuccess() {
        Intent intent = new Intent(EnterOtpActivity.this, ResetPasswordActivity.class);
        String phoneNumber = getIntent().getStringExtra("userPhoneNumber"); // Lấy số điện thoại từ Intent
        intent.putExtra("USER_PHONE_NUMBER", phoneNumber); // Truyền số điện thoại vào Intent
        startActivity(intent);
        finish(); // Đóng màn hình hiện tại
    }
}
