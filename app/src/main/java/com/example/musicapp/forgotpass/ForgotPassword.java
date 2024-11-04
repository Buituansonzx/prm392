package com.example.musicapp.forgotpass;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ForgotPassword extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private Button sendOtpButton;
    private FirebaseAuth firebaseAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        firebaseAuth = FirebaseAuth.getInstance();

        sendOtpButton.setOnClickListener(view -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(ForgotPassword.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }

            sendVerificationCode(phoneNumber);
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        // Kiểm tra độ dài của số điện thoại và tự động thêm mã quốc gia nếu cần
        if (phoneNumber.length() == 10 && !phoneNumber.startsWith("+84")) {
            phoneNumber = "+84" + phoneNumber.substring(1); // Thay số 0 đầu tiên bằng +84
        }
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                // Tự động xác minh hoàn tất
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(ForgotPassword.this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                                Log.e("FirebaseAuth", "Verification failed", e);
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                ForgotPassword.this.verificationId = verificationId;
                                String phoneNumber = phoneNumberEditText.getText().toString().trim(); // Lấy số điện thoại

                                Intent intent = new Intent(ForgotPassword.this, EnterOtpActivity.class);
                                intent.putExtra("verificationId", verificationId);
                                intent.putExtra("userPhoneNumber", phoneNumber); // Thêm số điện thoại vào Intent
                                startActivity(intent);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
