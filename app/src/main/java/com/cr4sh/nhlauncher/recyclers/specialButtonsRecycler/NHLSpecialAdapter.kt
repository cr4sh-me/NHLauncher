package com.cr4sh.nhlauncher.recyclers.specialButtonsRecycler

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.specialFeatures.SpecialFeaturesActivity
import com.cr4sh.nhlauncher.activities.wpsAttacks.WPSAttack
import com.cr4sh.nhlauncher.pagers.bluetoothPager.BluetoothActivity
import com.cr4sh.nhlauncher.recyclers.categoriesRecycler.specialButtonsRecycler.NHLSpecialItem
import com.cr4sh.nhlauncher.recyclers.categoriesRecycler.specialButtonsRecycler.NHLSpecialViewHolder
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.ToastUtils
import com.cr4sh.nhlauncher.utils.VibrationUtils
import kotlinx.coroutines.launch
import java.util.Locale

class NHLSpecialAdapter(private var activity: SpecialFeaturesActivity) :
    RecyclerView.Adapter<NHLSpecialViewHolder>() {
    private val items: MutableList<NHLSpecialItem> = ArrayList()
    private var height = 0
    private var margin = 0
    private var drawable: GradientDrawable? = null
    private var nhlPreferences: NHLPreferences? = null
    private var overlay = false

    @SuppressLint("NotifyDataSetChanged") // Clear old data and display new!
    fun updateData(newData: List<NHLSpecialItem>?) {
        items.clear()
        items.addAll(newData!!)
        notifyDataSetChanged()
    }

    private fun saveRecyclerHeight(active: Int) {
        val prefs = activity.getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs?.edit()
        editor?.putInt("recyclerHeight", active)
        editor?.apply()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NHLSpecialViewHolder {
        nhlPreferences = NHLPreferences(activity)
        val originalHeight: Int
        if (nhlPreferences!!.recyclerMainHeight == 0) { //TODO fix crash
            originalHeight = parent.measuredHeight
            saveRecyclerHeight(originalHeight)
        } else {
            originalHeight = nhlPreferences!!.recyclerMainHeight
        }
        margin = 20
        height = originalHeight / 8 - margin // Button height without margin
        drawable = GradientDrawable()
        overlay = nhlPreferences!!.isButtonOverlayActive
        if (nhlPreferences!!.isNewButtonStyleActive) {
            drawable!!.setColor(Color.parseColor(nhlPreferences!!.color50()))
            drawable!!.cornerRadius = 60f
        } else {
            drawable!!.cornerRadius = 60f
            drawable!!.setStroke(8, Color.parseColor(nhlPreferences!!.color80()))
        }

        // Update this line to set bounds for the drawable
        drawable!!.setBounds(0, 0, parent.width, height)

        return NHLSpecialViewHolder(
            LayoutInflater.from(activity).inflate(R.layout.custom_button, parent, false)
        )
    }


    // Used to create buttons, and set listeners for them
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(
        holder: NHLSpecialViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = getItem(position)
        holder.nameView.text = item.name.uppercase(Locale.getDefault())
        holder.descriptionView.text = item.description.uppercase(Locale.getDefault())
        @SuppressLint("DiscouragedApi") val imageResourceId =
            activity.resources?.getIdentifier(item.image, "drawable", activity.packageName)
        if (imageResourceId != null) {
//            Glide.with(activity)
//                .load(imageResourceId)
//                .into(holder.imageView)
            holder.imageView.setImageResource(imageResourceId)
        }
        if (overlay) {
            holder.imageView.setColorFilter(
                Color.parseColor(nhlPreferences!!.color80()),
                PorterDuff.Mode.MULTIPLY
            )
        }
        holder.nameView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        holder.descriptionView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        holder.itemView.background = drawable
        val params = RecyclerView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height)
        params.setMargins(margin, margin / 2, margin, margin / 2)
        holder.buttonView.layoutParams = params
        holder.itemView.setOnClickListener {
            VibrationUtils.vibrate()
            when (position) {
                0 -> {
                    val intent = Intent(activity, WPSAttack::class.java)
                    activity.startActivity(intent)
                }

                1 -> {
                    val intent = Intent(activity, BluetoothActivity::class.java)
                    activity.startActivity(intent)
                }

                2 -> {
                    activity.lifecycleScope.launch {
                        ToastUtils.showCustomToast(activity, "Coming soon...")
                    }
//                    val intent = Intent(activity, NetScannerActivity::class.java)
//                    activity.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): NHLSpecialItem {
        return items[position]
    }
}
