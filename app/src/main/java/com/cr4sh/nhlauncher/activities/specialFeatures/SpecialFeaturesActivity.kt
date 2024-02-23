package com.cr4sh.nhlauncher.activities.specialFeatures

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.recyclers.categoriesRecycler.specialButtonsRecycler.NHLSpecialItem
import com.cr4sh.nhlauncher.recyclers.specialButtonsRecycler.NHLSpecialAdapter
import com.cr4sh.nhlauncher.utils.ColorChanger.Companion.activityAnimation
import com.cr4sh.nhlauncher.utils.LanguageChanger
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.VibrationUtils

class SpecialFeaturesActivity : LanguageChanger() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityAnimation()

        setContentView(R.layout.special_fragment_layout)
        val nhlPreferences = NHLPreferences(this)
        val specialRecyclerView = findViewById<RecyclerView>(R.id.special_recycler_view)
        val title = findViewById<TextView>(R.id.textView)
        title.setTextColor(Color.parseColor(nhlPreferences.color80()))
        val cancelButton = findViewById<Button>(R.id.cancel_button)
        cancelButton.setBackgroundColor(Color.parseColor(nhlPreferences.color80()))
        cancelButton.setTextColor(Color.parseColor(nhlPreferences.color50()))
        val rootView = findViewById<View>(android.R.id.content)
        rootView.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
        val window = this.window
        window.statusBarColor = Color.parseColor(nhlPreferences.color20())
        window.navigationBarColor = Color.parseColor(nhlPreferences.color20())
        val drawable = GradientDrawable()
        if (nhlPreferences.isNewButtonStyleActive) {
            drawable.setColor(Color.parseColor(nhlPreferences.color50()))
            drawable.cornerRadius = 60f
        } else {
            drawable.cornerRadius = 60f
            drawable.setStroke(8, Color.parseColor(nhlPreferences.color80()))
        }
        val nhlSpecialAdapter = NHLSpecialAdapter()
        val specialItem = NHLSpecialItem(
            resources.getString(R.string.wps_attack),
            resources.getString(R.string.wps_desc_short),
            "wireless"
        )
        val specialItem2 = NHLSpecialItem(
            resources.getString(R.string.bt_toolkit),
            resources.getString(R.string.bt_short),
            "bluetooth"
        )
        val specialItem3 = NHLSpecialItem(
            "Net Scanner",
            resources.getString(R.string.netscanner2),
            "socat"
        )
        val newItemList: MutableList<NHLSpecialItem> = ArrayList()
        newItemList.add(specialItem)
        newItemList.add(specialItem2)
        newItemList.add(specialItem3)
        nhlSpecialAdapter.updateData(newItemList)
        specialRecyclerView.adapter = nhlSpecialAdapter
        cancelButton.setOnClickListener {
            VibrationUtils.vibrate()
            val intent = Intent(this@SpecialFeaturesActivity, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            activityAnimation()
        }
    }
}
