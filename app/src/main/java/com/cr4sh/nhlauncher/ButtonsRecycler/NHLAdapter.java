package com.cr4sh.nhlauncher.ButtonsRecycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.DialogUtils;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.NHLManager;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.VibrationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class NHLAdapter extends RecyclerView.Adapter<NHLViewHolder> {

    private final EditText editText;
    private final MainActivity myActivity = NHLManager.getInstance().getMainActivity();
    private final List<NHLItem> items = new ArrayList<>();
    private final ExecutorService executor = NHLManager.getInstance().getExecutorService();
    private int height;
    private int margin;
    private GradientDrawable drawable;
    private NHLPreferences NHLPreferences;
    private boolean overlay;

    public NHLAdapter(EditText editText) {
        this.editText = editText;
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
        // Replace all item images with a papysz
        for (NHLItem item : items) {
            item.setImage("papysz2");
        }
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
        NHLPreferences = new NHLPreferences(myActivity);

        int originalHeight;
        if (NHLPreferences.getRecyclerMainHeight() == 0) {
            originalHeight = parent.getMeasuredHeight();
            saveRecyclerHeight(originalHeight);
        } else {
            originalHeight = NHLPreferences.getRecyclerMainHeight();
        }

        overlay = NHLPreferences.isButtonOverlayActive();

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


        return new NHLViewHolder(LayoutInflater.from(myActivity).inflate(R.layout.custom_button, parent, false));
    }

    // Used to create buttons, and set listeners for them
    @Override
    public void onBindViewHolder(@NonNull NHLViewHolder holder, @SuppressLint("RecyclerView") int position) {

        MainUtils mainUtils = new MainUtils(myActivity);
        DialogUtils dialogUtils = new DialogUtils(myActivity.getSupportFragmentManager());

        NHLItem item = getItem(position);

        String searchQuery = editText.getText().toString().toUpperCase();

        if (!searchQuery.isEmpty()) {
            // Highlight the search query in the button text for 'nameView'
            String buttonTextName = item.getName().toUpperCase();
            SpannableStringBuilder builderName = new SpannableStringBuilder(buttonTextName);
            int startIndexName = buttonTextName.indexOf(searchQuery);

            if (startIndexName != -1 && (startIndexName + searchQuery.length() <= buttonTextName.length())) {
                if (NHLPreferences.isNewButtonStyleActive()) {
                    builderName.setSpan(new BackgroundColorSpan(Color.parseColor(NHLPreferences.color20())), startIndexName, startIndexName + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    builderName.setSpan(new BackgroundColorSpan(Color.parseColor(NHLPreferences.color50())), startIndexName, startIndexName + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            holder.nameView.setText(builderName);

            // Highlight the search query in the button text for 'descriptionView'
            String buttonTextDescription = item.getDescription().toUpperCase();
            SpannableStringBuilder builderDescription = new SpannableStringBuilder(buttonTextDescription);
            int startIndexDescription = buttonTextDescription.indexOf(searchQuery);

            if (startIndexDescription != -1 && (startIndexName + searchQuery.length() <= buttonTextDescription.length())) {
                if (NHLPreferences.isNewButtonStyleActive()) {
                    builderDescription.setSpan(new BackgroundColorSpan(Color.parseColor(NHLPreferences.color20())), startIndexDescription, startIndexDescription + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    builderDescription.setSpan(new BackgroundColorSpan(Color.parseColor(NHLPreferences.color50())), startIndexDescription, startIndexDescription + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            holder.descriptionView.setText(builderDescription);

        } else {
            // If there is no search query, set the button text without highlighting for 'nameView'
            holder.nameView.setText(item.getName().toUpperCase());

            // If there is no search query, set the button text without highlighting for 'descriptionView'
            holder.descriptionView.setText(item.getDescription().toUpperCase());
        }

        @SuppressLint("DiscouragedApi") int imageResourceId = myActivity.getResources().getIdentifier(item.getImage(), "drawable", myActivity.getPackageName());

        holder.imageView.setImageResource(imageResourceId);

        if (overlay) {
            holder.imageView.setColorFilter(Color.parseColor(NHLPreferences.color80()), PorterDuff.Mode.MULTIPLY);
        }

        holder.nameView.setTextColor(Color.parseColor(NHLPreferences.color80()));
        holder.descriptionView.setTextColor(Color.parseColor(NHLPreferences.color80()));

        holder.itemView.setBackground(drawable);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        params.setMargins(margin, (margin / 2), margin, (margin / 2));
        holder.buttonView.setLayoutParams(params);

//        Log.d("MyAdapter", "Parent height: " + originalHeight);
//        Log.d("MyAdapter", "Button height with margin: " + (height + margin));

        holder.itemView.setOnClickListener(v -> {
            if (!editText.getText().toString().isEmpty()) {
                myActivity.getOnBackPressedDispatcher().onBackPressed(); // close searchbar
            }
            myActivity.buttonUsage = item.getUsage();
            mainUtils.buttonUsageIncrease(item.getName());
            executor.execute(() -> mainUtils.run_cmd(item.getCmd()));
        });

        holder.itemView.setOnLongClickListener(view -> {
            VibrationUtils.vibrate(myActivity, 10);
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
}
