package com.cr4sh.nhlauncher.activities.specialFeatures

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.recyclers.categoriesRecycler.specialButtonsRecycler.NHLSpecialItem
import com.cr4sh.nhlauncher.recyclers.specialButtonsRecycler.NHLSpecialAdapter
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate

class SpecialFeaturesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.cat_appear,
                R.anim.cat_appear
            )
        } else {
            overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear)
        }
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
            "kali_fern_wifi_cracker"
        )
        val specialItem2 = NHLSpecialItem(
            resources.getString(R.string.bt_toolkit),
            resources.getString(R.string.bt_short),
            "kali_spooftooph"
        )
        val newItemList: MutableList<NHLSpecialItem> = ArrayList()
        newItemList.add(specialItem)
        newItemList.add(specialItem2)
        nhlSpecialAdapter.updateData(newItemList)
        specialRecyclerView.adapter = nhlSpecialAdapter

//        customButton1.setBackground(drawable);
////        customButton2.setBackground(drawable);
//        customButton3.setBackground(drawable);
//
//
//        customButton1.setOnClickListener(v -> {
//            VibrationUtils.vibrate(this, 10);
//            Intent intent = new Intent(this, WPSAttack.class);
//            startActivity(intent);
//        });

//        customButton2.setOnClickListener(v -> {
//            Intent intent = new Intent(this, MDKDeauth.class);
//            startActivity(intent);
//        });

//        customButton3.setOnClickListener(v -> {
//            VibrationUtils.vibrate(this, 10);
//            ToastUtils.showCustomToast(this, "Coming soon...");
////            Intent intent = new Intent(this, BluetoothAttacks.class);
////            startActivity(intent);
//        });
        cancelButton.setOnClickListener {
            vibrate(this, 10)
            val intent = Intent(this@SpecialFeaturesActivity, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val animationBundle = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.cat_appear,  // Enter animation
                R.anim.cat_disappear // Exit animation
            ).toBundle()
            startActivity(intent, animationBundle)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(
                    OVERRIDE_TRANSITION_CLOSE,
                    R.anim.cat_appear,
                    R.anim.cat_appear
                )
            } else {
                overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear)
            }
        }
    }
}
