package com.example.federateddemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.federateddemo.adapter.ViewPagerFragmentAdapter;
import com.example.federateddemo.databinding.ActivityVitalsBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class VitalsActivity extends AppCompatActivity {

    //  TODO remove hardcoded decryption key instead fetch from db or shared preferences
    private static final String TAG = "VitalsActivity";
    private ActivityVitalsBinding mBinding;


    ViewPager2 viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityVitalsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


//        viewPagerItemArrayList.add(new ViewPagerItem(1, null));
//        viewPagerItemArrayList.add(new ViewPagerItem(2, null));
//        viewPagerItemArrayList.add(new ViewPagerItem(3, null));

        SharedPreferences preferences = getApplication().getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        String userId = preferences.getString(Constants.USER_ID, "abc");

        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        adapter.setViewPagerItemArrayList(userId);
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
    }
}