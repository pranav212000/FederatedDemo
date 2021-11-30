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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.federateddemo.databinding.ActivityMainBinding;
import com.example.federateddemo.ml.ModelMobilenet20epoch;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
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
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {


    private static final int RESULT_LOAD_IMAGE = 101;
    private static final int CAMERA_REQUEST_CODE = 99;
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 102;
    private static final int SCAN_QR_CODE_REQUEST_CODE = 103;
    int preference = ScanConstants.OPEN_CAMERA;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        }


        binding.chooseImage.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        });


        binding.capture.setOnClickListener(v -> {

            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);

        });

        binding.scanQrCode.setOnClickListener(v -> {


            captureImage();

//
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


    }

    private void captureImage() {
        startActivity(new Intent(MainActivity.this, CameraActivity.class));
    }


    private void infer(Bitmap bitmap) throws IOException {


        Log.d(TAG, "infer: " + bitmap.getWidth() + " x " + bitmap.getHeight());
//        ModelMobilenet95 model = ModelMobilenet95.newInstance(this);
        ModelMobilenet20epoch modelMobilenet20epoch = ModelMobilenet20epoch.newInstance(this);

        // Creates inputs for reference.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                        .build();

        TensorImage tfImage = new TensorImage(DataType.FLOAT32);
        tfImage.load(bitmap);
        tfImage = imageProcessor.process(tfImage);

        Log.d(TAG, "infer: remaining " + tfImage.getBuffer().remaining());


//        TensorImage tfImage = TensorImage.fromBitmap(bitmap);


        ByteBuffer byteBuffer = ByteBuffer.allocate(224 * 224 * 3);
        byteBuffer.rewind();
        byteBuffer = tfImage.getBuffer();
        Log.d(TAG, "infer: " + tfImage.getBuffer().array().length);

        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
        inputFeature0.loadBuffer(byteBuffer);


//        ModelMobilenet95.Outputs outputs = model.process(inputFeature0);
        ModelMobilenet20epoch.Outputs outputs = modelMobilenet20epoch.process(inputFeature0);

        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

        Log.d(TAG, "infer: " + outputFeature0.toString());

        float[] data = outputFeature0.getFloatArray();

        binding.pred.setText(String.valueOf(data[0]));
        float pred = outputFeature0.getFloatArray()[0];

        binding.predsLayout.setVisibility(View.VISIBLE);

        if (pred > 1 - pred) {
            binding.result.setText("POSITIVE");
            binding.result.setTextColor(getResources().getColor(R.color.red));
        } else {
            binding.result.setText("NEGATIVE");
            binding.result.setTextColor(getResources().getColor(R.color.green));
        }

        Log.d(TAG, "infer: outputdata " + Arrays.toString(data));


        // Releases model resources if no longer used.
//        model.close();
        modelMobilenet20epoch.close();
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
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.d(TAG, "onActivityResult: picturePath : " + picturePath);
            binding.imageName.setText(Paths.get(picturePath).getFileName().toString());
            ImageView imageView = findViewById(R.id.image);
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitmap);

            try {
                infer(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            binding.image.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                binding.image.setImageBitmap(bitmap);
                try {
                    infer(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
        }
    }
}