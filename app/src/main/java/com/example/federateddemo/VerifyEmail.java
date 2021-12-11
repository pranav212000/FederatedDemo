package com.example.federateddemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.federateddemo.Helpers.UserHelperClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;

import io.github.muddz.styleabletoast.StyleableToast;

public class VerifyEmail extends AppCompatActivity {

    TextView display_email_text;
    Button cancelBtn, signupBtn, resendBtn;
    ProgressBar progressBar;
    AlertDialog alertDialog;
    //    FirebaseAuth Variables
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    PhoneAuthCredential phoneAuthCredential;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //    Variables
    private String fullName, email, password, gender, dateOfBirth, phoneNumber, countryCode, countryName;
    private String verificationId, oneTimePassword, userId;
    private BiometricPrompt biometricPrompt;
    private Executor executor;

//    @Override
//    public void onBackPressed() {
//        Log.d("CDA", "onBackPressed Called");
//        showConfirmDialogue("Do you want to Cancel Verification and Return to Signup?");
//    }

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
        setContentView(R.layout.activity_verify_email);




        executor = ContextCompat.getMainExecutor(VerifyEmail.this);
        biometricPrompt = new BiometricPrompt(VerifyEmail.this, executor, new BiometricPrompt.AuthenticationCallback() {
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
                startActivity(new Intent(VerifyEmail.this, MainActivity.class));
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







//        Hooks
        display_email_text = findViewById(R.id.verify_email_info_text);

//        Button Hooks
        cancelBtn = findViewById(R.id.cancel_btn);
        signupBtn = findViewById(R.id.signup_btn);
        resendBtn = findViewById(R.id.resend_email);

        progressBar = findViewById(R.id.progress_bar);

//        Get Variables from previous intent
        Intent prevIntent = getIntent();
        fullName = prevIntent.getStringExtra("fullName");
        email = prevIntent.getStringExtra("email");
        password = prevIntent.getStringExtra("password");
        gender = prevIntent.getStringExtra("gender");
        dateOfBirth = prevIntent.getStringExtra("dateOfBirth");
        phoneNumber = prevIntent.getStringExtra("phoneNumber");
        countryName = prevIntent.getStringExtra("countryName");
        countryCode = prevIntent.getStringExtra("countryCode");
        oneTimePassword = prevIntent.getStringExtra("oneTimePassword");
        verificationId = prevIntent.getStringExtra("verificationId");

        display_email_text.setText("Click link sent to\n" + email);

//        Create a new user using emailId
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.INVISIBLE);
        resendBtn.setClickable(false);
        signupBtn.setClickable(false);
        cancelBtn.setClickable(false);

        createUser(firebaseAuth);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }

                if (user == null) {
                    showToast("User Account Null!", Toast.LENGTH_LONG);
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(@NonNull AuthResult authResult) {
                            user = firebaseAuth.getCurrentUser();

                            if (user.isEmailVerified()) {
                                progressBar.setVisibility(View.VISIBLE);

//                                Link Phone number to user
//                                firebaseAuth.getCurrentUser().linkWithCredential(PhoneAuthProvider.getCredential(verificationId, oneTimePassword)).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                                    @Override
//                                    public void onSuccess(@NonNull AuthResult authResult) {
//                                        showToast("Phone Number linked to user", Toast.LENGTH_LONG);
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        showToast("Phone Number not linked to user", Toast.LENGTH_LONG);
//                                        showToast("Error! "+e.getMessage(), Toast.LENGTH_LONG);
//                                        Log.i("Error! ", e.getMessage());
//                                    }
//                                });

                                //Go to Dashboard
                                userId = user.getUid();

                                firebaseDatabase = FirebaseDatabase.getInstance();
                                databaseReference = firebaseDatabase.getReference("users");

                                UserHelperClass userHelperClass = new UserHelperClass(fullName, email, userId, gender, dateOfBirth, phoneNumber, countryCode, countryName);

                                databaseReference.child(userId).setValue(userHelperClass);


                                biometricPrompt.authenticate(promptInfo);


                            } else {
                                showToast("Please Verify Mail Sent to " + email, Toast.LENGTH_LONG);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("User was not successfully signed in. Please Verify user at " + email, Toast.LENGTH_LONG);
                        }
                    });
                    Log.i("Verification Status", String.valueOf(user.isEmailVerified()));

                }
            }
        });

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null) {
                    showToast("User Account Null!", Toast.LENGTH_LONG);
                } else {
                    sendVerificationEmail(user);
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerifyEmail.this, Signup_1.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void createUser(FirebaseAuth firebaseAuth) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(@NonNull AuthResult authResult) {
                user = firebaseAuth.getCurrentUser();
                progressBar.setVisibility(View.INVISIBLE);
                sendVerificationEmail(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                signupBtn.setClickable(false);
                progressBar.setVisibility(View.INVISIBLE);
                showToast("New User Account Not Created!", Toast.LENGTH_LONG);
                showToast("Error!: " + e.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        progressBar.setVisibility(View.VISIBLE);
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void aVoid) {
                progressBar.setVisibility(View.INVISIBLE);
                showToast("Verification Email Sent to " + email, Toast.LENGTH_SHORT);
                signupBtn.setClickable(true);

                resendBtn.setClickable(false);
                cancelBtn.setClickable(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                signupBtn.setClickable(false);
                resendBtn.setVisibility(View.VISIBLE);
                resendBtn.setClickable(true);
                cancelBtn.setClickable(true);

                showToast("Verification Email Not Sent!", Toast.LENGTH_LONG);
                showToast("Error!: " + e.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    private void showConfirmDialogue(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(VerifyEmail.this, R.style.AlertDialogueTheme);
        View view = LayoutInflater.from(VerifyEmail.this).inflate(
                R.layout.lt_dialogue_box,
                (LinearLayout) findViewById(R.id.layout_dialogue_container)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.alert_text)).setText(message);

        alertDialog = builder.create();
        view.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.hide();
                Intent intent = new Intent(VerifyEmail.this, Signup_1.class);
                startActivity(intent);
                finish();
            }
        });
        view.findViewById(R.id.negative_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.hide();
            }
        });

        alertDialog.show();
    }

    public void showToast(String message, int toastLength) {
        StyleableToast toast = StyleableToast.makeText(getApplicationContext(), message, toastLength, R.style.MyToast);
        toast.setGravity(Gravity.BOTTOM);
        toast.show();
    }
}