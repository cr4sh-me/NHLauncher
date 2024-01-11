package com.cr4sh.nhlauncher.CategoriesRecycler;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.NHLManager;
import com.cr4sh.nhlauncher.NHLPreferences;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtil;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesViewHolder> {

    private final MainActivity myActivity = NHLManager.getInstance().getMainActivity();
    private final List<String> item = new ArrayList<>();
    private final List<Integer> itemImg = new ArrayList<>();
    private int height;
    private int margin;
    private GradientDrawable drawable;
    private NHLPreferences NHLPreferences;

    public CategoriesAdapter() {

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
        NHLPreferences = new NHLPreferences(myActivity);
        int originalHeight = parent.getMeasuredHeight();
        margin = 20;
        height = (originalHeight / 8) - margin; // Button height without margin

        drawable = new GradientDrawable();
        if (NHLPreferences.isNewButtonStyleActive()) {
            drawable.setColor(Color.parseColor(NHLPreferences.color50()));
            drawable.setCornerRadius(60);
        } else {
            drawable.setCornerRadius(60);
            drawable.setStroke(8, Color.parseColor(NHLPreferences.color80()));
        }
        drawable.setBounds(0, 0, 0, height); // Set bounds for the drawable

        return new CategoriesViewHolder(LayoutInflater.from(myActivity).inflate(R.layout.custom_category_item, parent, false));
    }


    // Used to create buttons, and set listeners for them
    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, @SuppressLint("RecyclerView") int position) {

        MainUtils mainUtils = new MainUtils(myActivity);
        String categoryName = item.get(position);
        String categoryImage = String.valueOf(itemImg.get(position));
        holder.nameView.setText(categoryName);

        // Load image dynamically based on categoryImage
        @SuppressLint("DiscouragedApi") int imageResourceId = myActivity.getResources().getIdentifier(categoryImage, "drawable", myActivity.getPackageName());
        holder.imageView.setImageResource(imageResourceId);

        holder.imageView.setColorFilter(Color.parseColor(NHLPreferences.color80()), PorterDuff.Mode.MULTIPLY);

        holder.nameView.setTextColor(Color.parseColor(NHLPreferences.color80()));

        holder.itemView.setBackground(drawable);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        params.setMargins(margin, (margin / 2), margin, (margin / 2));
        holder.categoryLayout.setLayoutParams(params);

//        Log.d("CategoriesAdapter", "Parent height: " + originalHeight);
//        Log.d("CategoriesAdapter", "Button height with margin: " + (height + margin));

        holder.itemView.setOnClickListener(v -> {
            VibrationUtil.vibrate(myActivity, 10);
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
