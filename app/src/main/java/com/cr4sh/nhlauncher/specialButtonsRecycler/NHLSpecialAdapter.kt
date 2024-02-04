package com.cr4sh.nhlauncher.specialButtonsRecycler

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.bluetoothPager.BluetoothActivity
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate
import com.cr4sh.nhlauncher.wpsAttacks.WPSAttack
import java.util.Locale

class NHLSpecialAdapter : RecyclerView.Adapter<NHLSpecialViewHolder>() {
    private val myActivity = NHLManager.getInstance().mainActivity
    private val items: MutableList<NHLSpecialItem> = ArrayList()
    private var height = 0
    private var margin = 0
    private var drawable: GradientDrawable? = null
    private var nhlPreferences: NHLPreferences? = null

    @SuppressLint("NotifyDataSetChanged") // Clear old data and display new!
    fun updateData(newData: List<NHLSpecialItem>?) {
        items.clear()
        items.addAll(newData!!)
        notifyDataSetChanged()
    }

    private fun saveRecyclerHeight(active: Int) {
        val prefs = myActivity.getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("recyclerHeight", active)
        editor.apply()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NHLSpecialViewHolder {
        nhlPreferences = NHLPreferences(myActivity)
        val originalHeight: Int
        if (nhlPreferences!!.recyclerMainHeight == 0) {
            originalHeight = parent.measuredHeight
            saveRecyclerHeight(originalHeight)
        } else {
            originalHeight = nhlPreferences!!.recyclerMainHeight
        }
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
        return NHLSpecialViewHolder(
            LayoutInflater.from(myActivity).inflate(R.layout.custom_button, parent, false)
        )
    }

    // Used to create buttons, and set listeners for them
    override fun onBindViewHolder(
        holder: NHLSpecialViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = getItem(position)
        holder.nameView.text = item.name.uppercase(Locale.getDefault())
        holder.descriptionView.text = item.description.uppercase(Locale.getDefault())
        @SuppressLint("DiscouragedApi") val imageResourceId =
            myActivity.resources.getIdentifier(item.image, "drawable", myActivity.packageName)
        holder.imageView.setImageResource(imageResourceId)
        holder.nameView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        holder.descriptionView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        holder.itemView.background = drawable
        val params = RecyclerView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height)
        params.setMargins(margin, margin / 2, margin, margin / 2)
        holder.buttonView.layoutParams = params
        holder.itemView.setOnClickListener {
            vibrate(myActivity, 10)
            if (position == 0) {
                val intent = Intent(myActivity, WPSAttack::class.java)
                myActivity.startActivity(intent)
            } else if (position == 1) {
//                ToastUtils.showCustomToast(myActivity, "Coming soon...");
                val intent = Intent(myActivity, BluetoothActivity::class.java)
                myActivity.startActivity(intent)
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
