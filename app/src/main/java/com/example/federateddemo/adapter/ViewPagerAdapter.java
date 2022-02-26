package com.example.federateddemo.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.federateddemo.databinding.ViewpagerItemBinding;
import com.example.federateddemo.models.ViewPagerItem;

import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder> {


    ArrayList<ViewPagerItem> viewPagerItemArrayList;

    public ViewPagerAdapter(ArrayList<ViewPagerItem> viewPagerItemArrayList) {
        this.viewPagerItemArrayList = viewPagerItemArrayList;
    }

    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewpagerItemBinding item = ViewpagerItemBinding.inflate(layoutInflater, parent, false);

        return new ViewPagerHolder(item);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {

        ViewPagerItem item = viewPagerItemArrayList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return viewPagerItemArrayList.size();
    }

    public static class ViewPagerHolder extends RecyclerView.ViewHolder {

        final ViewpagerItemBinding binding;

        public ViewPagerHolder(ViewpagerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ViewPagerItem viewPagerItem) {

            binding.itemNo.setText(String.valueOf(viewPagerItem.getId()));
        }


    }


}
