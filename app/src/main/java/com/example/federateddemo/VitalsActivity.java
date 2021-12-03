package com.example.federateddemo;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.federateddemo.databinding.ActivityVitalsBinding;
import com.google.mlkit.vision.barcode.Barcode;
import com.macasaet.fernet.Key;
import com.macasaet.fernet.StringValidator;
import com.macasaet.fernet.Token;
import com.macasaet.fernet.TokenExpiredException;
import com.macasaet.fernet.TokenValidationException;
import com.macasaet.fernet.Validator;

import java.util.ArrayList;
import java.util.List;

public class VitalsActivity extends AppCompatActivity {

    //  TODO remove hardcoded decryption key instead fetch from db or shared preferences
    public static final String DECRYPTION_KEY = "sG9X6xiiSjrEKNAnlwK6BcYjBKhovxBuoAKU3v8v7bI=";
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


        String decryptedData = decryptData(barcodeRawValues.get(0));
        if(decryptedData.isEmpty())
            finish();

        Log.d(TAG, "onCreate: decrypted data : " + decryptedData);
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
            Toast.makeText(VitalsActivity.this, getResources().getString(R.string.token_expiration_error), Toast.LENGTH_SHORT).show();
        } catch (TokenValidationException e) {
            Toast.makeText(VitalsActivity.this, getResources().getString(R.string.token_validation_error), Toast.LENGTH_SHORT).show();

        }
        return decryptedData;
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