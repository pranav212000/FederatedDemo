package com.example.federateddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.federateddemo.databinding.ActivityScanQrBinding;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ScanQrActivity extends AppCompatActivity {
    private static final String TAG = "ScanQrActivity";
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

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
                    Log.e(TAG, "onError: " + exception.toString());
                    Toast.makeText(ScanQrActivity.this, "ERROR : " + exception.toString(), Toast.LENGTH_SHORT).show();
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

                    Log.d(TAG, "onSuccess: BARCODES : " + barcodes.toString());
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
                        }

                        Intent intent = new Intent(ScanQrActivity.this, VitalsActivity.class);

                        Bundle args = new Bundle();
                        args.putSerializable(Constants.BARCODES, barcodeRawValues);
                        intent.putExtra(Constants.BUNDLE, args);

                        startActivity(intent);
                        imageProxy.close();
                        finish();

                    }
                })
                .addOnFailureListener(e -> {
                    // Task failed with an exception
                    // ...
                    Toast.makeText(ScanQrActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }).addOnCompleteListener(task -> {

                    if (notFound[0]) {
                        Toast.makeText(ScanQrActivity.this, "NO QR CODE FOUND, TRY AGAIN!", Toast.LENGTH_SHORT).show();
                    }
                });
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


        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);




    }
}