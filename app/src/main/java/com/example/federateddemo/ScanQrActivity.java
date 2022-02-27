package com.example.federateddemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import com.example.federateddemo.databinding.ActivityScanQrBinding;
import com.example.federateddemo.room.VitalViewModel;
import com.example.federateddemo.room.dao.VitalDao;
import com.example.federateddemo.room.database.CoviCareDatabase;
import com.example.federateddemo.room.entities.Vital;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.macasaet.fernet.Key;
import com.macasaet.fernet.StringValidator;
import com.macasaet.fernet.Token;
import com.macasaet.fernet.TokenExpiredException;
import com.macasaet.fernet.TokenValidationException;
import com.macasaet.fernet.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ScanQrActivity extends AppCompatActivity {
    private static final String TAG = "ScanQrActivity";
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    public static final String DECRYPTION_KEY = "sG9X6xiiSjrEKNAnlwK6BcYjBKhovxBuoAKU3v8v7bI=";

    private ActivityScanQrBinding mBinding;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityScanQrBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());


        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        checkCameraProviderAvailability();
        mBinding.capture.setOnClickListener(v -> {
            ImageCapture.OutputFileOptions outputFileOptions =
                    new ImageCapture.OutputFileOptions.Builder(new File("/images")).build();

            imageCapture.takePicture(ContextCompat.getMainExecutor(ScanQrActivity.this), new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                    super.onCaptureSuccess(imageProxy);


                    @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();

                    if (mediaImage != null) {
                        InputImage image =
                                InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                        // Pass image to an ML Kit Vision API

                        processImage(image, imageProxy);
                    }
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    super.onError(exception);
                    Log.e(TAG, "onError: " + exception);
                    Toast.makeText(ScanQrActivity.this, "ERROR : " + exception, Toast.LENGTH_SHORT).show();
                }
            });

        });

    }

    private void processImage(InputImage image, ImageProxy imageProxy) {

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE)
                        .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        final boolean[] notFound = {false};

        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    // Task completed successfully
                    // ...

                    Log.d(TAG, "onSuccess: BARCODES : " + barcodes);
                    if (barcodes.isEmpty()) {
                        notFound[0] = true;
                        Log.d(TAG, "onSuccess: NO QR CODES FOUND!");
                    } else {

                        notFound[0] = false;
                        for (Barcode b : barcodes) {
                            Log.d(TAG, "onSuccess: barcodes : " + b.toString());
                        }

                        ArrayList<String> barcodeRawValues = new ArrayList<>();

                        for (Barcode barcode : barcodes) {
                            barcodeRawValues.add(barcode.getRawValue());
                            Log.d(TAG, "processImage: raw value" + barcode.getRawValue());
                        }

                        String decryptedData = decryptData(barcodeRawValues.get(0));
                        Log.d(TAG, "processImage: decrypted data : " + decryptedData);

                        parseData(decryptedData);

                        imageProxy.close();
                        finish();

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ScanQrActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(task -> {
                    if (notFound[0]) {
                        Toast.makeText(ScanQrActivity.this, "NO QR CODE FOUND, TRY AGAIN!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void parseData(String decryptedData) {
        try {
            JSONObject obj = new JSONObject(decryptedData);
            Log.d(TAG, "parseData: obj : " + obj);
            String userId = obj.getString(Constants.USER_ID);
            Double pulse = obj.getDouble(Constants.PULSE);
            Double temperature = obj.getDouble(Constants.TEMPERATURE);
            Double spo2 = obj.getDouble(Constants.SPO2);
            String dateString = obj.getString(Constants.TIMESTAMP);
            SimpleDateFormat formatter6 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = formatter6.parse(dateString);


            Vital vital = new Vital(userId, temperature, spo2, pulse, date, false);

            CoviCareDatabase db = Room.databaseBuilder(getApplicationContext(),
                    CoviCareDatabase.class, Constants.DATABASE_NAME).build();





            VitalViewModel vitalViewModel = ViewModelProviders.of(ScanQrActivity.this).get(VitalViewModel.class);

            vitalViewModel.getAllVitals().observe(ScanQrActivity.this, vitals -> {
                Toast.makeText(this, "onChanged", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "parseData: vitals : " + vitals);

            });


//            TODO store locally sqlite
            Log.d(TAG, "parseData: vital : " + vital);


            Intent intent = new Intent(ScanQrActivity.this, VitalsActivity.class);

            startActivity(intent);


        } catch (JSONException | ParseException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
            Toast.makeText(ScanQrActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
    }

    private String decryptData(String encryptedText) {
        final Key key = new Key(DECRYPTION_KEY);
        final Token token = Token.fromString(encryptedText);
        final Validator<String> validator = new StringValidator() {

        };
        String decryptedData = "";
        try {
            decryptedData = token.validateAndDecrypt(key, validator);
        } catch (TokenExpiredException e) {
            Toast.makeText(ScanQrActivity.this, getResources().getString(R.string.token_expiration_error), Toast.LENGTH_SHORT).show();
        } catch (TokenValidationException e) {
            Toast.makeText(ScanQrActivity.this, getResources().getString(R.string.token_validation_error), Toast.LENGTH_SHORT).show();

        }
        return decryptedData;
    }


    private void checkCameraProviderAvailability() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(mBinding.previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(mBinding.previewView.getDisplay().getRotation())
                .build();


        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);


    }
}