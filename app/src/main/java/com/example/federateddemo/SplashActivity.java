package com.example.federateddemo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 2000; // 2 seconds delay for splash screen

    //    Variables
    Animation top_Animation, bottom_Animation;
    ImageView logo;
    TextView title, slogan;
    private BiometricPrompt biometricPrompt;
    private boolean isSignedIn = false;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            isSignedIn = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Hide Status Bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        Hide Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_splash);

//        Hook Animations
        top_Animation = AnimationUtils.loadAnimation(this, R.anim.splash_top_anim);
        bottom_Animation = AnimationUtils.loadAnimation(this, R.anim.splash_bottom_anim);

//        Hook Variables
        logo = findViewById(R.id.app_logo);
        title = findViewById(R.id.app_name);
        slogan = findViewById(R.id.app_slogan);

//        Set Animations
        logo.setAnimation(top_Animation);
        title.setAnimation(bottom_Animation);
        slogan.setAnimation(bottom_Animation);


        Executor executor = ContextCompat.getMainExecutor(SplashActivity.this);
        biometricPrompt = new BiometricPrompt(SplashActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
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
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
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

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
//        Button biometricLoginButton = findViewById(R.id.biometric_login);
//        biometricLoginButton.setOnClickListener(view -> {
//        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isSignedIn) {
                    biometricPrompt.authenticate(promptInfo);
                } else {


                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

//                Animate Transitions
                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(logo, "logo_tran");
                    pairs[1] = new Pair<View, String>(title, "logo_text_tran");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);

                    startActivity(intent, options.toBundle());
                }
            }
        }, SPLASH_SCREEN);
    }
}