package com.cr4sh.nhlanucher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
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
        if (newData.isEmpty()) {
            items.clear();
            notifyDataSetChanged();
            return;
        }
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallback(items, newData));
        items.clear();
        items.addAll(newData);
        diffResult.dispatchUpdatesTo(this);
        Log.d("MyAdapter", "Updating data");
        Log.d("MyAdapter", "New item list size: " + newData.size());

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
        holder.emailView.setText(item.getDescription().toUpperCase());

        holder.imageView.setImageResource(item.getImage());
        holder.nameView.setTextColor(Color.parseColor(MainUtils.nameColor));
        holder.emailView.setTextColor(Color.parseColor(MainUtils.descriptionColor));

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(MainUtils.buttonColor));
        drawable.setCornerRadius(5);
        drawable.setStroke(2, Color.parseColor(MainUtils.strokeColor));
        holder.itemView.setBackground(drawable);
        holder.nameView.setTypeface(MainUtils.typeface);
        holder.emailView.setTypeface(MainUtils.typeface);

        holder.itemView.setOnClickListener(v -> {
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
