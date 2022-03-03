package com.example.federateddemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.federateddemo.adapter.GridViewAdapter;
import com.example.federateddemo.databinding.ActivityMainBinding;
import com.example.federateddemo.interfaces.OnHomePageClickListener;
import com.example.federateddemo.ml.ModelMobilenet20epoch;
import com.example.federateddemo.models.HomePageButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements OnHomePageClickListener {


    private static final int RESULT_LOAD_IMAGE = 101;
    private static final int CAMERA_REQUEST_CODE = 99;
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 102;
    private static final int SCAN_QR_CODE_REQUEST_CODE = 103;
    int preference = ScanConstants.OPEN_CAMERA;
    AlertDialog alertDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        }


        ArrayList<HomePageButton> homepageButtons = new ArrayList<>();


        homepageButtons.add(new HomePageButton(getString(R.string.capture_image), R.drawable.ic_capture_image, Constants.CAPTURE_IMAGE));
        homepageButtons.add(new HomePageButton(getString(R.string.pick_image), R.drawable.ic_pick_image, Constants.PICK_IMAGE));
        homepageButtons.add(new HomePageButton(getString(R.string.scan_qr_code), R.drawable.ic_scan_qr, Constants.SCAN_QR));
        homepageButtons.add(new HomePageButton(getString(R.string.vital_history), R.drawable.ic_vital_history, Constants.VITALS_HISTORY));
        homepageButtons.add(new HomePageButton(getString(R.string.evaluate_mental_health), R.drawable.ic_eval_mental_temp, Constants.MENTAL_HEALTH));

        GridViewAdapter adapter = new GridViewAdapter(this, homepageButtons, this);

        mBinding.gridview.setAdapter(adapter);
//        CourseGVAdapter adapter = new CourseGVAdapter(this, courseModelArrayList);
//        coursesGV.setAdapter(adapter);


        mBinding.chooseImage.setOnClickListener(v -> {
            mBinding.imageName.setVisibility(View.VISIBLE);
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        });


        mBinding.capture.setOnClickListener(v -> {
            mBinding.imageName.setVisibility(View.GONE);
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);

        });

        mBinding.questionList.setOnClickListener(v -> {

            startActivity(new Intent(MainActivity.this, QuizActivity.class));
        });

        mBinding.scanQrCode.setOnClickListener(v -> {


            captureImage();


//            BarcodeScannerOptions options =
//                    new BarcodeScannerOptions.Builder()
//                            .setBarcodeFormats(
//                                    Barcode.FORMAT_QR_CODE,
//                                    Barcode.FORMAT_AZTEC)
//                            .build();


//            try {
//
//                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//                intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
//
//                startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
//
//            } catch (Exception e) {
//
//                Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
//                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
//                startActivity(marketIntent);
//
//            }
        });


        firebaseAuth = FirebaseAuth.getInstance();

        mBinding.logout.setOnClickListener(view -> showConfirmDialogue("Do you want to log out of CoviCare?", firebaseAuth));


    }


    private void captureImage() {
        startActivity(new Intent(MainActivity.this, ScanQrActivity.class));
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please give permission to read image", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            Intent intent = new Intent(getApplicationContext(), DisplayImageActivity.class);
            intent.putExtra(Constants.FROM_ACTIVITY, Constants.PICK_IMAGE);
            intent.putExtra(Constants.IMAGE_URI, selectedImage.toString());
            startActivity(intent);
//
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//            Log.d(TAG, "onActivityResult: picturePath : " + picturePath);
//
//
//            mBinding.imageName.setText(Paths.get(picturePath).getFileName().toString());
//            ImageView imageView = findViewById(R.id.image);
//            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
//            imageView.setImageBitmap(bitmap);
//
//            try {
//                infer(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (data != null) {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                Intent intent = new Intent(getApplicationContext(), DisplayImageActivity.class);
                intent.putExtra(Constants.FROM_ACTIVITY, Constants.CAPTURE_IMAGE);
                intent.putExtra(Constants.IMAGE_URI, uri.toString());
                startActivity(intent);
            }
        }
    }


    private void showConfirmDialogue(String message, FirebaseAuth firebaseAuth) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogueTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(
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
                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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

    private void showExitConfirmDialogue(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogueTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(
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

    @Override
    public void onItemClick(HomePageButton homePageButton) {

        Toast.makeText(this, homePageButton.getButtonName(), Toast.LENGTH_SHORT).show();


        switch (homePageButton.getButtonId()) {
            case Constants.CAPTURE_IMAGE:
                Intent intent = new Intent(this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
                break;

            case Constants.PICK_IMAGE:
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                startActivityForResult(pickIntent, RESULT_LOAD_IMAGE);
                break;
            case Constants.SCAN_QR:

                startActivity(new Intent(MainActivity.this, ScanQrActivity.class));
                break;
            case Constants.VITALS_HISTORY:

                startActivity(new Intent(MainActivity.this, VitalsActivity.class));
                break;
            case Constants.MENTAL_HEALTH:

                startActivity(new Intent(MainActivity.this, QuizActivity.class));
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + homePageButton.getButtonId());
        }

    }
}


