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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class Signup_3 extends AppCompatActivity {

    // Variables
    Button signup, callLogin;
    ImageView logo;
    TextView title;
    TextInputLayout phoneNumber;
    CountryCodePicker country_code;
    // Firebase Variables
    FirebaseDatabase rootNode;
    DatabaseReference databaseReference;
    //Local Information Variables
    private String fullName, email, password, gender, dateOfBirth, countryCodeVal, countryNameVal;

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
        setContentView(R.layout.activity_signup_3);

//        Hooks
//        Button Hooks
        signup = findViewById(R.id.signup_btn);
        callLogin = findViewById(R.id.login_btn);

        logo = findViewById(R.id.app_logo);
        title = findViewById(R.id.signup_welcome_text);
        country_code = findViewById(R.id.country_code_picker);

        Intent prevIntent = getIntent();
        fullName = prevIntent.getStringExtra("fullName");
        email = prevIntent.getStringExtra("email");
        password = prevIntent.getStringExtra("password");
        gender = prevIntent.getStringExtra("gender");
        dateOfBirth = prevIntent.getStringExtra("dateOfBirth");

        countryNameVal = country_code.getSelectedCountryName();
        countryCodeVal = country_code.getSelectedCountryCode();

//        Edit Text Hooks
        phoneNumber = findViewById(R.id.phone_no_text);

        callLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialogue("Do you want to return to the Login screen?");
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePhoneNumber()) {
                    showToast("Input Valid Data");
                    return;
                } else {
                    Intent intent = new Intent(Signup_3.this, VerifyEmail.class);
                    intent.putExtra("fullName", fullName);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("gender", gender);
                    intent.putExtra("dateOfBirth", dateOfBirth);
                    intent.putExtra("phoneNumber", phoneNumber.getEditText().getText().toString().trim());
                    intent.putExtra("countryName", countryNameVal);
                    intent.putExtra("countryCode", countryCodeVal);

                    Log.i("CountryName", countryNameVal);
                    Log.i("CountryVal", countryCodeVal);

//                Animate Transitions
                    Pair[] pairs = new Pair[3];
                    pairs[0] = new Pair<View, String>(logo, "logo_tran");
                    pairs[1] = new Pair<View, String>(title, "logo_text_tran");
                    pairs[2] = new Pair<View, String>(signup, "login_verify_tran");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Signup_3.this, pairs);

                    startActivity(intent, options.toBundle());
                }
            }
        });

        country_code.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryNameVal = country_code.getSelectedCountryName();
                countryCodeVal = country_code.getSelectedCountryCode();
                Log.i("New Country", countryNameVal + " " + countryCodeVal);
            }
        });

    }

    public void showToast(String message) {
        StyleableToast toast = StyleableToast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG, R.style.MyToast);
        toast.setGravity(Gravity.BOTTOM);
        toast.show();
    }

    private void showConfirmDialogue(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Signup_3.this, R.style.AlertDialogueTheme);
        View view = LayoutInflater.from(Signup_3.this).inflate(
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
                Intent intent = new Intent(Signup_3.this, LoginActivity.class);

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

    private boolean validatePhoneNumber() {
        String phoneNumberVal = phoneNumber.getEditText().getText().toString().trim();
        if (phoneNumberVal.isEmpty()) {
            phoneNumber.setError("Phone number cannot be empty");
            return false;
        } else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            String phoneNumberRegexPattern = "(0/91)?[7-9][0-9]{9}";
            Pattern p = Pattern.compile(phoneNumberRegexPattern);
            if (!p.matcher(phoneNumberVal).matches()) {
                phoneNumber.setError("Phone number format is invalid");
                return false;
            } else {
                phoneNumber.setError(null);
                phoneNumber.setErrorEnabled(false);
                return true;
            }
        }
    }

}