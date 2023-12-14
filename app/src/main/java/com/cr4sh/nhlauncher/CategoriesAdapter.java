package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesViewHolder> {

    MainActivity myActivity;
    List<String> item = new ArrayList<>();
    List<Integer> itemImg = new ArrayList<>();

    public CategoriesAdapter(MainActivity activity) {
        this.myActivity = activity;
        // Initialize the list

    }

    @SuppressLint("NotifyDataSetChanged")
    // Clear old data and display new!
    public void updateData(List<String> newData, List<Integer> newData2) {
        item.clear();
        itemImg.clear();
        item.addAll(newData);
        itemImg.addAll(newData2);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoriesViewHolder(LayoutInflater.from(myActivity).inflate(R.layout.custom_category_item, parent, false));
    }

    // Used to create buttons, and set listeners for them

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, @SuppressLint("RecyclerView") int position) {

        MainUtils mainUtils = new MainUtils(myActivity);
        MyPreferences myPreferences = new MyPreferences(myActivity);

        String categoryName = item.get(position);
        String categoryImage = String.valueOf(itemImg.get(position));
        holder.nameView.setText(categoryName);

        // Load image dynamically based on categoryImage
        @SuppressLint("DiscouragedApi") int imageResourceId = myActivity.getResources().getIdentifier(categoryImage, "drawable", myActivity.getPackageName());
        holder.imageView.setImageResource(imageResourceId);

        holder.imageView.setColorFilter(Color.parseColor(myPreferences.color80()), PorterDuff.Mode.MULTIPLY);

        holder.nameView.setTextColor(Color.parseColor(myPreferences.color80()));


        holder.itemView.setOnClickListener(v -> {
            myActivity.changeCategoryPreview(categoryImage, categoryName);
            myActivity.backButton.callOnClick();
            mainUtils.spinnerChanger((position));
        });

    }

    @Override
    public int getItemCount() {
        return item.size();
    }
}
