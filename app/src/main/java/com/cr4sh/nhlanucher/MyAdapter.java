package com.cr4sh.nhlanucher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    List<Item> items;

    public MyAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Item> newData) {
        items.clear();
        items.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_button,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Item item = getItem(position);

        holder.nameView.setText(item.getName().toUpperCase());
        holder.descriptionView.setText(item.getDescription().toUpperCase());

        @SuppressLint("DiscouragedApi") int imageResourceId = context.getResources().getIdentifier(item.getImage(), "drawable", context.getPackageName());
        holder.imageView.setImageResource(imageResourceId);

        holder.nameView.setTextColor(Color.parseColor(MainUtils.nameColor));
        holder.descriptionView.setTextColor(Color.parseColor(MainUtils.descriptionColor));

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(MainUtils.buttonColor));
        drawable.setCornerRadius(5);
        drawable.setStroke(2, Color.parseColor(MainUtils.strokeColor));
        holder.itemView.setBackground(drawable);
        holder.nameView.setTypeface(MainUtils.typeface);
        holder.descriptionView.setTypeface(MainUtils.typeface);

        holder.itemView.setOnClickListener(v -> {
            MainUtils.mainActivity.buttonUsage = item.getUsage();
            MainUtils.buttonUsageIncrease(item.getName());
            new Thread(() -> MainUtils.run_cmd(item.getCmd())).start();
        });

        holder.itemView.setOnLongClickListener(view -> {
            MainUtils.mainActivity.buttonCategory = item.getCategory();
            MainUtils.mainActivity.buttonName = item.getName();
            MainUtils.mainActivity.buttonDescription = item.getDescription();
            MainUtils.mainActivity.buttonCmd = item.getCmd();
            MainUtils.mainActivity.registerForContextMenu(view);
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
