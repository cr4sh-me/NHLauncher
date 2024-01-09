package com.cr4sh.nhlauncher.SpecialButtonsRecycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.NHLManager;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.WpsAttacks.WPSAttack;
import com.cr4sh.nhlauncher.utils.DialogUtils;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.ToastUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class NHLSpecialAdapter extends RecyclerView.Adapter<NHLSpecialViewHolder>{
    private final MainActivity myActivity = NHLManager.getInstance().getMainActivity();
    private final List<NHLSpecialItem> items = new ArrayList<>();
    private int height;
    private int margin;
    private GradientDrawable drawable;
    private MyPreferences myPreferences;
    private final ExecutorService executor = NHLManager.getInstance().getExecutorService();

    public NHLSpecialAdapter() {
    }

    @SuppressLint("NotifyDataSetChanged")
    // Clear old data and display new!
    public void updateData(List<NHLSpecialItem> newData) {
        items.clear();
        items.addAll(newData);
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
    public NHLSpecialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        myPreferences = new MyPreferences(myActivity);

        int originalHeight;
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


        return new NHLSpecialViewHolder(LayoutInflater.from(myActivity).inflate(R.layout.custom_button, parent, false));
    }

    // Used to create buttons, and set listeners for them
    @Override
    public void onBindViewHolder(@NonNull NHLSpecialViewHolder holder, @SuppressLint("RecyclerView") int position) {

        MainUtils mainUtils = new MainUtils(myActivity);
        DialogUtils dialogUtils = new DialogUtils(myActivity.getSupportFragmentManager());

        NHLSpecialItem item = getItem(position);

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

//        Log.d("MyAdapter", "Parent height: " + originalHeight);
//        Log.d("MyAdapter", "Button height with margin: " + (height + margin));

        holder.itemView.setOnClickListener(v -> {
            VibrationUtil.vibrate(myActivity, 10);

            if (position == 0) {
                Intent intent = new Intent(myActivity, WPSAttack.class);
                myActivity.startActivity(intent);
            } else if (position == 1) {
                ToastUtils.showCustomToast(myActivity, "Coming soon...");
//                Intent intent = new Intent(myActivity, BluetoothActivity.class);
//                myActivity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private NHLSpecialItem getItem(int position) {
        return items.get(position);
    }
}
