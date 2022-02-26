package com.example.federateddemo.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.federateddemo.Constants;
import com.example.federateddemo.fragments.VitalDisplayFragment;
import com.example.federateddemo.models.ViewPagerItem;

import java.util.ArrayList;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    private ArrayList<ViewPagerItem> viewPagerItemArrayList;


    public ViewPagerFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public ViewPagerFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }


    public void setViewPagerItemArrayList(ArrayList<ViewPagerItem> viewPagerItemArrayList) {
        this.viewPagerItemArrayList = viewPagerItemArrayList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Fragment fragment = new VitalDisplayFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.PAGER_ITEM, viewPagerItemArrayList.get(position));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return viewPagerItemArrayList.size();
    }
}
