package com.cr4sh.nhlauncher.recyclers.categoriesRecycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.R

class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var categoryLayout: ConstraintLayout
    var imageView: ImageView
    var nameView: TextView

    init {
        categoryLayout = itemView.findViewById(R.id.spinnerBackground)
        imageView = itemView.findViewById(R.id.button_icon)
        nameView = itemView.findViewById(R.id.button_name)
    }
}
