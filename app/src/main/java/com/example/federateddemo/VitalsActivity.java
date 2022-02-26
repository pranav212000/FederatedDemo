package com.example.federateddemo;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.federateddemo.adapter.ViewPagerFragmentAdapter;
import com.example.federateddemo.databinding.ActivityVitalsBinding;
import com.example.federateddemo.models.ViewPagerItem;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.mlkit.vision.barcode.Barcode;
import com.macasaet.fernet.Key;
import com.macasaet.fernet.StringValidator;
import com.macasaet.fernet.Token;
import com.macasaet.fernet.TokenExpiredException;
import com.macasaet.fernet.TokenValidationException;
import com.macasaet.fernet.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VitalsActivity extends AppCompatActivity {

    //  TODO remove hardcoded decryption key instead fetch from db or shared preferences
    public static final String DECRYPTION_KEY = "sG9X6xiiSjrEKNAnlwK6BcYjBKhovxBuoAKU3v8v7bI=";
    private static final String TAG = "VitalsActivity";
    private ActivityVitalsBinding mBinding;


    ViewPager2 viewPager;
    ArrayList<ViewPagerItem> viewPagerItemArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityVitalsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


        viewPagerItemArrayList.add(new ViewPagerItem(1, null, null, null, null));
        viewPagerItemArrayList.add(new ViewPagerItem(2, null, null, null, null));
        viewPagerItemArrayList.add(new ViewPagerItem(3, null, null, null, null));


        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        adapter.setViewPagerItemArrayList(viewPagerItemArrayList);
        viewPager = mBinding.pager;
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);


        new TabLayoutMediator(mBinding.tabLayout, mBinding.pager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Week");
                    break;
                case 1:
                    tab.setText("Month");
                    break;
                case 2:
                    tab.setText("Year");
                    break;

            }
        }).attach();


        for (int i = 0; i < mBinding.tabLayout.getTabCount(); i++) {

            TextView tv = (TextView) LayoutInflater.from(this)
                    .inflate(R.layout.custom_tab, null);

            Objects.requireNonNull(mBinding.tabLayout.getTabAt(i)).setCustomView(tv);
        }


//
//        Intent intent = getIntent();
//        Bundle args = intent.getBundleExtra(Constants.BUNDLE);
//
//
//        ArrayList<String> barcodeRawValues = (ArrayList<String>) args.getSerializable(Constants.BARCODES);
////        readDataFromBarcodes(barcodes);

//        mBinding.deviceKey.setText(barcodeRawValues.get(0));

//
//        String decryptedData = decryptData(barcodeRawValues.get(0));
//        if (decryptedData.isEmpty())
//            finish();
//        else {
//
//            try {
//                JSONObject obj = new JSONObject(decryptedData);
//                String username = obj.getString(Constants.USERNAME);
//                String heart_rate = String.valueOf(obj.getDouble(Constants.HEART_RATE));
//                String temperature = obj.getDouble(Constants.TEMPERATURE) + "\u2103";
//                String spo2 = obj.getInt(Constants.SPO2) + "%";
//
//                mBinding.userName.setText(username);
//                mBinding.deviceKey.setText(DECRYPTION_KEY);
//                mBinding.temperature.setText(temperature);
//                mBinding.oxygen.setText(spo2);
//                mBinding.pulse.setText(heart_rate);
//
//
//                Log.d(TAG, "onCreate: heart_rate : " + heart_rate);
//
//            } catch (JSONException e) {
//                Log.e(TAG, "onCreate: " + e.getMessage());
//                Toast.makeText(VitalsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//
//
//        }
//
//        Log.d(TAG, "onCreate: decrypted data : " + decryptedData);
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


//    private void readDataFromBarcodes(List<Barcode> barcodes) {
//        for (Barcode barcode : barcodes) {
//
//            Rect bounds = barcode.getBoundingBox();
//            Point[] corners = barcode.getCornerPoints();
//
//            String rawValue = barcode.getRawValue();
//            Log.d(TAG, "onSuccess: QR VALUE : " + rawValue);
//
//
//            mBinding.deviceKey.setText(barcode.getRawValue());
//
//
//            int valueType = barcode.getValueType();
//            // See API reference for complete list of supported types
//            switch (valueType) {
//                case Barcode.TYPE_WIFI:
//                    String ssid = barcode.getWifi().getSsid();
//                    String password = barcode.getWifi().getPassword();
//                    int type = barcode.getWifi().getEncryptionType();
//                    break;
//                case Barcode.TYPE_URL:
//                    String title = barcode.getUrl().getTitle();
//                    String url = barcode.getUrl().getUrl();
//                    break;
//            }
//        }
//    }
}