package com.cr4sh.nhlauncher.buttonsRecycler

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.R

class NHLViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageView: ImageView
    var nameView: TextView
    var descriptionView: TextView
    var buttonView: LinearLayout

    init {
        buttonView = itemView.findViewById(R.id.button_view)
        imageView = itemView.findViewById(R.id.button_icon)
        nameView = itemView.findViewById(R.id.button_name)
        descriptionView = itemView.findViewById(R.id.button_description)
    }
}
