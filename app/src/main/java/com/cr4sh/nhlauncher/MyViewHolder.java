package com.cr4sh.nhlauncher;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nameView, descriptionView;
    LinearLayout buttonView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        buttonView = itemView.findViewById(R.id.button_view);
        imageView = itemView.findViewById(R.id.button_icon);
        nameView = itemView.findViewById(R.id.button_name);
        descriptionView = itemView.findViewById(R.id.button_description);
    }
}
