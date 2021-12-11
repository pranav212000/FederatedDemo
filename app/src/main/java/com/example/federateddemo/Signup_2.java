package com.example.federateddemo;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import io.github.muddz.styleabletoast.StyleableToast;

public class Signup_2 extends AppCompatActivity {

    private String fullName, email, password, gender, dateOfBirth;
    private int day, month, year;

    //    Variables
    RadioGroup gender_radio_group;
    RadioButton selected_gender;
    Button next, callLogin;
    ImageView logo;
    DatePicker datePickerDob;
    TextView title;

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
        setContentView(R.layout.activity_signup_2);

//        Hooks
//        Button Hooks
        next = findViewById(R.id.next_btn);
        callLogin = findViewById(R.id.login_btn);

        logo = findViewById(R.id.app_logo);
        title = findViewById(R.id.signup_welcome_text);
        datePickerDob = findViewById(R.id.dob_date_picker);

//        Radio Button Hooks
        gender_radio_group = findViewById(R.id.radio_group_gender);

//        Get data from previous intent
        Intent prevIntent = getIntent();
        fullName = prevIntent.getStringExtra("fullName");
        email = prevIntent.getStringExtra("email");
        password = prevIntent.getStringExtra("password");

        callLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialogue("Do you want to return to the Login screen?");
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateInput()) {
                    return;
                }else{
                    Intent intent = new Intent(Signup_2.this, Signup_3.class);

//                Get gender
                    selected_gender = findViewById(gender_radio_group.getCheckedRadioButtonId());
                    gender = selected_gender.getText().toString().trim();
//                Get Date of Birth
                    day = datePickerDob.getDayOfMonth();
                    month = datePickerDob.getMonth();
                    year = datePickerDob.getYear();
                    dateOfBirth = day+"/"+month+"/"+year;

                    intent.putExtra("fullName", fullName);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("gender", gender);
                    intent.putExtra("dateOfBirth", dateOfBirth);

//                Animate Transitions
                    Pair[] pairs = new Pair[6];
                    pairs[0] = new Pair<View, String>(logo, "logo_tran");
                    pairs[1] = new Pair<View, String>(title, "logo_text_tran");
                    pairs[2] = new Pair<View, String>(gender_radio_group, "email_tran");
                    pairs[3] = new Pair<View, String>(datePickerDob, "password_tran");
                    pairs[4] = new Pair<View, String>(callLogin, "login_signup_tran");
                    pairs[5] = new Pair<View, String>(next, "login_verify_tran");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( Signup_2.this, pairs);

                    startActivity(intent, options.toBundle());


                }
            }
        });

    }

    private boolean validateGender(){
        if(gender_radio_group.getCheckedRadioButtonId() == -1) {
            showToast("Please Select Gender");
            return false;
        }else{
            return true;
        }
    }

    private boolean validateAge(){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = currentYear - datePickerDob.getYear();

        if(userAge < 14){
            showToast("Your age must be more than 13");
            return false;
        }else{
            return true;
        }
    }

    public void showToast(String message){
        StyleableToast toast = StyleableToast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG, R.style.MyToast);
        toast.setGravity(Gravity.BOTTOM);
        toast.show();
    }

    private void showConfirmDialogue(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(Signup_2.this, R.style.AlertDialogueTheme);
        View view = LayoutInflater.from(Signup_2.this).inflate(
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
                Intent intent = new Intent(Signup_2.this, LoginActivity.class);

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

    private boolean validateInput(){
        return !(!validateGender() | !validateAge());
    }


}