package com.example.federateddemo;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginActivity extends AppCompatActivity {

    String emailS, passwordS;
    //    Variables
    Button forgotPassword, login, callSignUp;
    ImageView logo;
    TextView title;
    TextInputLayout email, password;

    //    Firebase Variables
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        showConfirmDialogue("Do you want to exit the CoviCare App?");
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
        setContentView(R.layout.activity_login);

//        Hooks
//        Button Hooks
        forgotPassword = findViewById(R.id.forgot_pass_btn);
        login = findViewById(R.id.login_btn);
        callSignUp = findViewById(R.id.signup_switch_btn);

        logo = findViewById(R.id.app_logo);
        title = findViewById(R.id.welcome_text);

//        Edit Text Hooks
        email = findViewById(R.id.user_email_text);
        password = findViewById(R.id.password_text);

        firebaseAuth = FirebaseAuth.getInstance();

//        Login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateInput()){
                    showToast("Input Valid Data");
                    return;
                }else{
                    emailS = email.getEditText().getText().toString().trim();
                    passwordS = password.getEditText().getText().toString().trim();
                    firebaseAuth.signInWithEmailAndPassword(emailS, passwordS).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(@NonNull AuthResult authResult) {
                            showToast("Welcome to CoviCare");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("Error! "+e.getMessage());
                        }
                    });
                }
            }
        });

//        Go to Sign Up
        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, Signup_1.class);

//                Animate Transitions
                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(logo, "logo_tran");
                pairs[1] = new Pair<View, String>(title, "logo_text_tran");
                pairs[2] = new Pair<View, String>(email, "email_tran");
                pairs[3] = new Pair<View, String>(password, "password_tran");
                pairs[4] = new Pair<View, String>(callSignUp, "login_signup_tran");
                pairs[5] = new Pair<View, String>(login, "login_verify_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);

                startActivity(intent, options.toBundle());
            }
        });

    }

    private boolean validateEmail() {
        String emailVal = email.getEditText().getText().toString().trim();
        Log.i("Email", emailVal);
        if (emailVal.isEmpty()) {
            email.setError("Email cannot be empty");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            String emailRegexPattern = "^(.+)@(.+)$";
            Pattern p = Pattern.compile(emailRegexPattern);
            if (!p.matcher(emailVal).matches()) {
                email.setError("Email format is invalid");
                return false;
            } else {
                email.setError(null);
                email.setErrorEnabled(false);
                return true;
            }
        }
    }

    private boolean validatePassword() {
        String passwordVal = password.getEditText().getText().toString().trim();
        Log.i("Password", passwordVal);
        if (passwordVal.isEmpty()) {
            password.setError("Password cannot be empty");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            if (passwordVal.length() < 6) {
                password.setError("Minimum length of password is 6");
                return false;
            } else {
                password.setError(null);
                password.setErrorEnabled(false);
                String passwordRegexPattern = "(^" +
//                        "(?=.*[0-9])" +  // at least 1 digit
//                        "(?=.*[a-z])" + // at least 1 small letter
//                        "(?=.*[A-Z])" + // at least 1 capital letter
                        "(?=.*[a-zA-Z])" + // any letter
                        "(?=.*[@#$%^&+=])" + // at least one special character
                        "(?=\\S+$)" + // no white spaces
                        ".{6,}" + // at least 6 characters
                        "$)";
                Pattern p = Pattern.compile(passwordRegexPattern);
                if (!p.matcher(passwordVal).matches()) {
                    password.setError("Password format is invalid");
                    return false;
                } else {
                    password.setError(null);
                    password.setErrorEnabled(false);
                    return true;
                }
            }
        }
    }

    public void showToast(String message){
        StyleableToast toast = StyleableToast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG, R.style.MyToast);
        toast.setGravity(Gravity.BOTTOM);
        toast.show();
    }

    private void showConfirmDialogue(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogueTheme);
        View view = LayoutInflater.from(LoginActivity.this).inflate(
                R.layout.lt_dialogue_box,
                (LinearLayout) findViewById(R.id.layout_dialogue_container)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.alert_text)).setText(message);

        AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.hide();
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
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

    private boolean validateInput() {
        return !(!validatePassword() | !validateEmail());
    }
}