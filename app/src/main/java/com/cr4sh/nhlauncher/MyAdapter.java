package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    MainActivity myActivity;
    List<Item> items = new ArrayList<>();
    Handler handler = new Handler();
    int height;
    RecyclerView recyclerView;

    public MyAdapter(MainActivity activity, RecyclerView recyclerView) {
        this.myActivity = activity;
        this.recyclerView = recyclerView;

        // Attaching custom snap helper
        NHLSnapHelper snapHelper = new NHLSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    @SuppressLint("NotifyDataSetChanged")
    // Clear old data and display new!
    public void updateData(List<Item> newData) {
        items.clear();
        items.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        height = parent.getHeight();
        return new MyViewHolder(LayoutInflater.from(myActivity).inflate(R.layout.custom_button, parent, false));
    }

    // Used to create buttons, and set listeners for them
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        MainUtils mainUtils = new MainUtils(myActivity);
        MyPreferences myPreferences = new MyPreferences(myActivity);
        DialogUtils dialogUtils = new DialogUtils(myActivity.getSupportFragmentManager());

        Item item = getItem(position);

        holder.nameView.setText(item.getName().toUpperCase());
        holder.descriptionView.setText(item.getDescription().toUpperCase());

        @SuppressLint("DiscouragedApi") int imageResourceId = myActivity.getResources().getIdentifier(item.getImage(), "drawable", myActivity.getPackageName());

//        holder.imageView.setColorFilter(Color.parseColor(myPreferences.color80()), PorterDuff.Mode.MULTIPLY);
//        holder.imageView.setColorFilter(Color.parseColor(myPreferences.color80()), PorterDuff.Mode.);

        holder.imageView.setImageResource(imageResourceId);

        holder.nameView.setTextColor(Color.parseColor(myPreferences.color80()));
        holder.descriptionView.setTextColor(Color.parseColor(myPreferences.color80()));
//
        GradientDrawable drawable = new GradientDrawable();
//        drawable.setColor(Color.parseColor(myPreferences.buttonColor()));
        drawable.setCornerRadius(60);
        drawable.setStroke(8, Color.parseColor(myPreferences.color80()));
        holder.itemView.setBackground(drawable);

        int buttonCount = 7;
        int buttonPadding = 25;
        int buttonHeight = (height / buttonCount) - buttonPadding;

        // Set layout parameters for the button
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                buttonHeight
        );
        layoutParams.setMargins(buttonPadding, (buttonPadding / 2), buttonPadding, (buttonPadding / 2));
        holder.buttonView.setLayoutParams(layoutParams);

        holder.itemView.setOnClickListener(v -> {
            myActivity.buttonUsage = item.getUsage();
            mainUtils.buttonUsageIncrease(item.getName());
            new Thread(() -> mainUtils.run_cmd(item.getCmd())).start();
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

    private Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        // Vibrate when a view is detached (button is disappearing from the screen)
        if (handler != null) {
            handler.postDelayed(() -> VibrationUtil.vibrate(myActivity, 10), 0); // Adjust the delay time as needed
        }
    }
}
