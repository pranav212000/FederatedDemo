package com.example.federateddemo;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.federateddemo.databinding.ActivityVitalsBinding;
import com.google.mlkit.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;

public class VitalsActivity extends AppCompatActivity {

    private static final String TAG = "VitalsActivity";

    private ActivityVitalsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityVitalsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra(Keys.BUNDLE);


        ArrayList<String> barcodeRawValues = (ArrayList<String>) args.getSerializable(Keys.BARCODES);
//        readDataFromBarcodes(barcodes);

        mBinding.deviceKey.setText(barcodeRawValues.get(0));

    }


    private void readDataFromBarcodes(List<Barcode> barcodes) {
        for (Barcode barcode : barcodes) {

            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();
            Log.d(TAG, "onSuccess: QR VALUE : " + rawValue);


            mBinding.deviceKey.setText(barcode.getRawValue());


            int valueType = barcode.getValueType();
            // See API reference for complete list of supported types
            switch (valueType) {
                case Barcode.TYPE_WIFI:
                    String ssid = barcode.getWifi().getSsid();
                    String password = barcode.getWifi().getPassword();
                    int type = barcode.getWifi().getEncryptionType();
                    break;
                case Barcode.TYPE_URL:
                    String title = barcode.getUrl().getTitle();
                    String url = barcode.getUrl().getUrl();
                    break;
            }
        }
    }
}