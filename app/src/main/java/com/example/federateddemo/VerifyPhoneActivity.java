package com.example.federateddemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.federateddemo.databinding.ActivityVerifyPhoneBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    private static final String TAG = "VerifyPhoneActivity";
    private ActivityVerifyPhoneBinding mBinding;
    private String number;
    private FirebaseAuth mAuth;
    private BiometricPrompt biometricPrompt;
    private Executor executor;
    private BiometricPrompt.PromptInfo promptInfo;
    private boolean isVerificationCompleted = false;
    private String verificationId;

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(VerifyPhoneActivity.this, "Code sent!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCodeSent: ");
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            Log.d(TAG, "onVerificationCompleted: ");
            if (code != null) {
                mBinding.progressbar.setIndeterminate(true);

                Log.d(TAG, "onVerificationCompleted: " + code);
                mBinding.progressbar.setIndeterminate(false);
                mBinding.editTextCode.setText(code);
                verifyCode(code);
            } else {
                signInWithCredential(phoneAuthCredential);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    private String firebaseToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityVerifyPhoneBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        number = getIntent().getStringExtra(Keys.NUMBER);

        mBinding.buttonSignIn.setOnClickListener(v -> {

            if (mBinding.editTextCode.getText().toString().equals("000000")) {
                biometricPrompt.authenticate(promptInfo);
//                startActivity(new Intent(VerifyPhoneActivity.this, MainActivity.class));
            }
        });

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(VerifyPhoneActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VerifyPhoneActivity.this, MainActivity.class));
                finish();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.

//        mAuth = FirebaseAuth.getInstance();
//
//        mBinding.editTextCode.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                if (mBinding.editTextCode.getText().length() < 6)
//                    mBinding.editTextCode.setError("Enter valid otp");
//                else
//                    onButtonClick(mBinding.editTextCode.getText().toString());
//
//                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                return true;
//            }
//
//            return false;
//        });

//        sendVerificationCode();

    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        isVerificationCompleted = true;
                        Log.d(TAG, "onComplete: Verification Complete");

                    }
                });
    }

    private void sendVerificationCode() {
        Log.d(TAG, "sendVerificationCode: " + number);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    public void onButtonClick(String otp) {
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        if (otp.isEmpty() || otp.length() < 6) {
            mBinding.editTextCode.setError("Enter Code...");
            mBinding.editTextCode.requestFocus();
            findViewById(R.id.progressbar).setVisibility(View.GONE);
            return;
        }
        verifyCode(otp);
    }
}