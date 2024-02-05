package com.cr4sh.nhlauncher.statsRecycler

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.MainActivity
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import java.util.Locale

class StatsAdapter : RecyclerView.Adapter<StatsHolder>() {
    private val myActivity: MainActivity = NHLManager.instance.mainActivity
    private val items: MutableList<StatsItem> = ArrayList()
    private var originalHeight = 0
    private var height = 0
    private var margin = 0
    private var drawable: GradientDrawable? = null
    private var nhlPreferences: NHLPreferences? = null

    @SuppressLint("NotifyDataSetChanged") // Clear old data and display new!
    fun updateData(newData: List<StatsItem>?) {
        items.clear()
        items.addAll(newData!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsHolder {
        nhlPreferences = NHLPreferences(myActivity)
        originalHeight = parent.measuredHeight
        margin = 20
        height = originalHeight / 8 - margin // Button height without margin
        drawable = GradientDrawable()
        if (nhlPreferences!!.isNewButtonStyleActive) {
            drawable!!.setColor(Color.parseColor(nhlPreferences!!.color50()))
            drawable!!.cornerRadius = 60f
        } else {
            drawable!!.cornerRadius = 60f
            drawable!!.setStroke(8, Color.parseColor(nhlPreferences!!.color80()))
        }
        drawable!!.setBounds(0, 0, 0, height) // Set bounds for the drawable
        return StatsHolder(
            LayoutInflater.from(myActivity).inflate(R.layout.custom_stats_button, parent, false)
        )
    }

    // Used to create buttons, and set listeners for them
    override fun onBindViewHolder(
        holder: StatsHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = getItem(position)
        holder.nameView.text = item.name.uppercase(Locale.getDefault())
        holder.usageText.text = item.usage.uppercase(Locale.getDefault())
        @SuppressLint("DiscouragedApi") val imageResourceId =
            myActivity.resources.getIdentifier(item.image, "drawable", myActivity.packageName)

//        holder.imageView.setColorFilter(Color.parseColor(myPreferences.color80()), PorterDuff.Mode.MULTIPLY);
//        holder.imageView.setColorFilter(Color.parseColor(myPreferences.color80()), PorterDuff.Mode.);
        holder.imageView.setImageResource(imageResourceId)
        holder.nameView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        holder.usageText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        holder.itemView.background = drawable
        val params = RecyclerView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height)
        params.setMargins(margin, margin / 2, margin, margin / 2)
        holder.buttonView.layoutParams = params
        Log.d("MyAdapter", "Parent height: $originalHeight")
        Log.d("MyAdapter", "Button height with margin: " + (height + margin))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): StatsItem {
        return items[position]
    }
}
