package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesViewHolder> {

    MainActivity myActivity;
    List<String> item = new ArrayList<>();
    List<Integer> itemImg = new ArrayList<>();
    int height;
    RecyclerView recyclerView;

    public CategoriesAdapter(MainActivity activity, RecyclerView recyclerView) {
        this.myActivity = activity;
        this.recyclerView = recyclerView;

        // Attaching custom snap helper
        NHLSnapHelper snapHelper = new NHLSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
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
        height = parent.getHeight();
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


        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(100);
        drawable.setStroke(8, Color.parseColor(myPreferences.color50()));
        holder.categoryLayout.setBackground(drawable);

        int buttonCount = 9;
        int buttonPadding = 25;
        int buttonHeight = (height / buttonCount) - buttonPadding;

        // Set layout parameters for the button
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                buttonHeight
        );
        layoutParams.setMargins(buttonPadding, (buttonPadding / 2), buttonPadding, (buttonPadding / 2));
        holder.categoryLayout.setLayoutParams(layoutParams);


        holder.itemView.setOnClickListener(v -> {
//            myActivity.isSpecialEnabled = false;
            myActivity.backButton.callOnClick();
            mainUtils.spinnerChanger((position));
            myActivity.currentCategoryNumber = position;
        });

    }

    @Override
    public int getItemCount() {
        return item.size();
    }
}
