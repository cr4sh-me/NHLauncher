package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    MainActivity myActivity;
    List<Item> items = new ArrayList<>();

    public MyAdapter(MainActivity activity) {
        this.myActivity = activity;
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
        return new MyViewHolder(LayoutInflater.from(myActivity).inflate(R.layout.custom_button, parent, false));
    }

    // Used to create buttons, and set listeners for them
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        MainUtils mainUtils = new MainUtils(myActivity);
        MyPreferences myPreferences = new MyPreferences(myActivity);

//        if (myPreferences.animateButtons()) {
//            Animation fadeInAnimation = AnimationUtils.loadAnimation(myActivity, R.anim.panning);
//            holder.itemView.startAnimation(fadeInAnimation);
//        }

        Item item = getItem(position);

        holder.nameView.setText(item.getName().toUpperCase());
        holder.descriptionView.setText(item.getDescription().toUpperCase());

        @SuppressLint("DiscouragedApi") int imageResourceId = myActivity.getResources().getIdentifier(item.getImage(), "drawable", myActivity.getPackageName());
        holder.imageView.setImageResource(imageResourceId);

//        holder.nameView.setTextColor(Color.parseColor(myPreferences.nameColor()));
//        holder.descriptionView.setTextColor(Color.parseColor(myPreferences.descriptionColor()));

        GradientDrawable drawable = new GradientDrawable();
//        drawable.setColor(Color.parseColor(myPreferences.buttonColor()));
//        drawable.setCornerRadius(5);
//        drawable.setStroke(20);
//        holder.itemView.setBackground(drawable);
//        holder.nameView.setTypeface(myPreferences.typeface());
//        holder.descriptionView.setTypeface(myPreferences.typeface());

        holder.itemView.setOnClickListener(v -> {
            myActivity.buttonUsage = item.getUsage();
            mainUtils.buttonUsageIncrease(item.getName());
            new Thread(() -> mainUtils.run_cmd(item.getCmd())).start();
        });

        holder.itemView.setOnLongClickListener(view -> {
            myActivity.buttonCategory = item.getCategory();
            myActivity.buttonName = item.getName();
            myActivity.buttonDescription = item.getDescription();
            myActivity.buttonCmd = item.getCmd();
            myActivity.registerForContextMenu(view);
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
}
