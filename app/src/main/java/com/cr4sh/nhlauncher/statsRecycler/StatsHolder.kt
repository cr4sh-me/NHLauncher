package com.cr4sh.nhlauncher.statsRecycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.R;

public class StatsHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nameView, usageText;
    LinearLayout buttonView;

    public StatsHolder(@NonNull View itemView) {
        super(itemView);
        buttonView = itemView.findViewById(R.id.button_view);
        imageView = itemView.findViewById(R.id.button_icon);
        nameView = itemView.findViewById(R.id.button_name);
        usageText = itemView.findViewById(R.id.button_usage);
    }
}
