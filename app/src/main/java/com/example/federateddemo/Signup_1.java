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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class Signup_1 extends AppCompatActivity {

    //    Variables
    Button next, callLogin;
    ImageView logo;
    TextView title;
    TextInputLayout fullName, email, password, confirmPassword;

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        showConfirmDialogue("Do you want to return to the Login screen?");
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
        setContentView(R.layout.activity_signup_1);


//        Hooks
//        Button Hooks
        next = findViewById(R.id.next_btn);
        callLogin = findViewById(R.id.login_btn);

        logo = findViewById(R.id.app_logo);
        title = findViewById(R.id.signup_welcome_text);

//        Edit Text Hooks
        fullName = findViewById(R.id.full_name_text);
        email = findViewById(R.id.email_id_text);
        password = findViewById(R.id.password_text);
        confirmPassword = findViewById(R.id.confirm_password_text);

        callLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialogue("Do you want to return to the Login screen?");
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateInput()) {
                    showToast("Input Valid Data");
                    return;
                } else {
                    Intent intent = new Intent(Signup_1.this, Signup_2.class);
                    intent.putExtra("fullName", fullName.getEditText().getText().toString().trim());
                    intent.putExtra("email", email.getEditText().getText().toString().trim());
                    intent.putExtra("password", password.getEditText().getText().toString().trim());

//                Animate Transitions
                    Pair[] pairs = new Pair[6];
                    pairs[0] = new Pair<View, String>(logo, "logo_tran");
                    pairs[1] = new Pair<View, String>(title, "logo_text_tran");
                    pairs[2] = new Pair<View, String>(email, "email_tran");
                    pairs[3] = new Pair<View, String>(password, "password_tran");
                    pairs[4] = new Pair<View, String>(callLogin, "login_signup_tran");
                    pairs[5] = new Pair<View, String>(next, "login_verify_tran");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Signup_1.this, pairs);

                    startActivity(intent, options.toBundle());
                }
            }
        });


    }

    //        Validate Functions
    private boolean validateFullName() {
        String fullNameVal = fullName.getEditText().getText().toString().trim();
        if (fullNameVal.isEmpty()) {
            fullName.setError("Full Name cannot be empty");
            return false;
        } else {
            fullName.setError(null);
            fullName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailVal = email.getEditText().getText().toString().trim();
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

    private boolean validateConfirmPassword() {
        String confirmPasswordVal = confirmPassword.getEditText().getText().toString().trim();
        String passwordVal = password.getEditText().getText().toString().trim();
        if (confirmPasswordVal.isEmpty()) {
            confirmPassword.setError("Confirm Password cannot be empty");
            return false;
        } else {
            confirmPassword.setError(null);
            confirmPassword.setErrorEnabled(false);
            Log.i(passwordVal, confirmPasswordVal);
            if (!passwordVal.equals(confirmPasswordVal)) {
                confirmPassword.setError("Password does not match");
                return false;
            } else {
                confirmPassword.setError(null);
                confirmPassword.setErrorEnabled(false);
                return true;
            }
        }
    }
    public void showToast(String message){
        StyleableToast toast = StyleableToast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG, R.style.MyToast);
        toast.setGravity(Gravity.BOTTOM);
        toast.show();
    }

    private void showConfirmDialogue(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(Signup_1.this, R.style.AlertDialogueTheme);
        View view = LayoutInflater.from(Signup_1.this).inflate(
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
                Intent intent = new Intent(Signup_1.this, LoginActivity.class);

//                Animate Transitions
//                Pair[] pairs = new Pair[6];
//                pairs[0] = new Pair<View, String>(logo, "logo_tran");
//                pairs[1] = new Pair<View, String>(title, "logo_text_tran");
//                pairs[2] = new Pair<View, String>(country_code, "email_tran");
//                pairs[3] = new Pair<View, String>(phoneNumber, "password_tran");
//                pairs[4] = new Pair<View, String>(callLogin, "login_signup_tran");
//                pairs[5] = new Pair<View, String>(signup, "login_verify_tran");
//
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( Signup_3.this, pairs);
//
//                startActivity(intent, options.toBundle());
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

    private boolean validateInput() {
        return !(!validateFullName() | !validatePassword() | !validateEmail() | !validateConfirmPassword());
    }
}