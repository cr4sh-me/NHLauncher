package com.cr4sh.nhlauncher.categoriesRecycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.R;

public class CategoriesViewHolder extends RecyclerView.ViewHolder {

    public ConstraintLayout categoryLayout;
    public ImageView imageView;
    public TextView nameView;

    public CategoriesViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryLayout = itemView.findViewById(R.id.spinnerBackground);
        imageView = itemView.findViewById(R.id.button_icon);
        nameView = itemView.findViewById(R.id.button_name);
    }
}
