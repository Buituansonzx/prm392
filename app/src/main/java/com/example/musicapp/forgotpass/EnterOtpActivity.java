package com.example.musicapp.forgotpass;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        otpEditText = findViewById(R.id.otpEditText);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        firebaseAuth = FirebaseAuth.getInstance();

        verificationId = getIntent().getStringExtra("verificationId");

        verifyOtpButton.setOnClickListener(view -> {
            String otp = otpEditText.getText().toString().trim();
            if (TextUtils.isEmpty(otp)) {
                Toast.makeText(EnterOtpActivity.this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyCode(otp);
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EnterOtpActivity.this, "Xác minh thành công", Toast.LENGTH_SHORT).show();
                // Chuyển đến màn hình thay đổi mật khẩu
                Intent intent = new Intent(EnterOtpActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(EnterOtpActivity.this, "Xác minh thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}