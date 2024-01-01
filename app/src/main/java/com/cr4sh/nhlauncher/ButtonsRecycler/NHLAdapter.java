package com.cr4sh.nhlauncher.ButtonsRecycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.NHLManager;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.DialogUtils;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtil;

import java.util.ArrayList;
import java.util.List;

public class NHLAdapter extends RecyclerView.Adapter<NHLViewHolder> {
    private final MainActivity myActivity = NHLManager.getInstance().getMainActivity();
    private final List<NHLItem> items = new ArrayList<>();
    private final Handler handler = new Handler();
    private int originalHeight;
    private int height;
    private int margin;
    private GradientDrawable drawable;
    private MyPreferences myPreferences;


    public NHLAdapter() {
    }

    @SuppressLint("NotifyDataSetChanged")
    // Clear old data and display new!
    public void updateData(List<NHLItem> newData) {
        items.clear();
        items.addAll(newData);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void startPapysz() {
        // Replace all item images with a single image
        for (NHLItem item : items) {
            item.setImage("papysz2"); // Replace with your single image name
        }

        // Notify the adapter to update the views
        notifyDataSetChanged();
    }

    private void saveRecyclerHeight(int active) {
        SharedPreferences prefs = myActivity.getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("recyclerHeight", active);
        editor.apply();
    }

    @NonNull
    @Override
    public NHLViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        myPreferences = new MyPreferences(myActivity);

        if(myPreferences.getRecyclerMainHeight() == 0){
            originalHeight = parent.getMeasuredHeight();
            saveRecyclerHeight(originalHeight);
        } else {
            originalHeight = myPreferences.getRecyclerMainHeight();
        }

        margin = 20;
        height = (originalHeight / 8) - margin; // Button height without margin

        drawable = new GradientDrawable();
        if (myPreferences.isNewButtonStyleActive()) {
            drawable.setColor(Color.parseColor(myPreferences.color50()));
            drawable.setCornerRadius(60);
        } else {
            drawable.setCornerRadius(60);
            drawable.setStroke(8, Color.parseColor(myPreferences.color80()));
        }
        drawable.setBounds(0, 0, 0, height); // Set bounds for the drawable


        return new NHLViewHolder(LayoutInflater.from(myActivity).inflate(R.layout.custom_button, parent, false));
    }

    // Used to create buttons, and set listeners for them
    @Override
    public void onBindViewHolder(@NonNull NHLViewHolder holder, @SuppressLint("RecyclerView") int position) {

        MainUtils mainUtils = new MainUtils(myActivity);
        DialogUtils dialogUtils = new DialogUtils(myActivity.getSupportFragmentManager());

        NHLItem item = getItem(position);

        holder.nameView.setText(item.getName().toUpperCase());
        holder.descriptionView.setText(item.getDescription().toUpperCase());

        @SuppressLint("DiscouragedApi") int imageResourceId = myActivity.getResources().getIdentifier(item.getImage(), "drawable", myActivity.getPackageName());

//        holder.imageView.setColorFilter(Color.parseColor(myPreferences.color80()), PorterDuff.Mode.MULTIPLY);
//        holder.imageView.setColorFilter(Color.parseColor(myPreferences.color80()), PorterDuff.Mode.);

        holder.imageView.setImageResource(imageResourceId);

        holder.nameView.setTextColor(Color.parseColor(myPreferences.color80()));
        holder.descriptionView.setTextColor(Color.parseColor(myPreferences.color80()));

        holder.itemView.setBackground(drawable);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        params.setMargins(margin, (margin / 2), margin, (margin / 2));
        holder.buttonView.setLayoutParams(params);

        Log.d("MyAdapter", "Parent height: " + originalHeight);
        Log.d("MyAdapter", "Button height with margin: " + (height + margin));

        holder.itemView.setOnClickListener(v -> {
            myActivity.buttonUsage = item.getUsage();
            mainUtils.buttonUsageIncrease(item.getName());
            myActivity.executor.execute(() -> mainUtils.run_cmd(item.getCmd()));
        });

        holder.itemView.setOnLongClickListener(view -> {
            VibrationUtil.vibrate(myActivity, 10);
            myActivity.buttonCategory = item.getCategory();
            myActivity.buttonName = item.getName();
            myActivity.buttonDescription = item.getDescription();
            myActivity.buttonCmd = item.getCmd();
            dialogUtils.openButtonMenuDialog(myActivity);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private NHLItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull NHLViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Vibrate when a view is detached (button is disappearing from the screen)
        handler.postDelayed(() -> VibrationUtil.vibrate(myActivity, 10), 0);
    }
}
