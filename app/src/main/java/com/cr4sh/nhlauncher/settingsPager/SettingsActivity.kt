package com.cr4sh.nhlauncher.settingsPager

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.cr4sh.nhlauncher.MainActivity
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SettingsActivity : AppCompatActivity() {
    private val mainActivity: MainActivity = NHLManager.instance.mainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear);
        setContentView(R.layout.settings_layout)
        val nhlPreferences = NHLPreferences(this)
        val rootView = findViewById<View>(android.R.id.content)
        rootView.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
        val window = this.window
        window.statusBarColor = Color.parseColor(nhlPreferences.color20())
        window.navigationBarColor = Color.parseColor(nhlPreferences.color20())
        val title = findViewById<TextView>(R.id.title)
        title.setTextColor(Color.parseColor(nhlPreferences.color80()))
        val cancelButton = findViewById<Button>(R.id.cancel_button)
        cancelButton.setBackgroundColor(Color.parseColor(nhlPreferences.color80()))
        cancelButton.setTextColor(Color.parseColor(nhlPreferences.color50()))
        cancelButton.setOnClickListener {
            vibrate(mainActivity, 10)
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val animationBundle = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.cat_appear,  // Enter animation
                R.anim.cat_disappear // Exit animation
            ).toBundle()
            startActivity(intent, animationBundle)
            finish()
        }
        val viewPager2 = findViewById<ViewPager2>(R.id.pager)
        val adapter = SettingsPager(this)
        viewPager2.adapter = adapter
        viewPager2.offscreenPageLimit = 4
        val tabs = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabs, viewPager2) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.setText(
                    resources.getString(R.string.general)
                )

                1 -> tab.setText(resources.getString(R.string.themes_settings))
                2 -> tab.setText(resources.getString(R.string.statistics))
                3 -> tab.setText(resources.getString(R.string.about))
            }
        }.attach()

        // Set tab background color
        tabs.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))

        // Set tab text color (unselected and selected)
        tabs.setTabTextColors(
            Color.parseColor(nhlPreferences.color80()),
            Color.parseColor(nhlPreferences.color80())
        )
        tabs.setSelectedTabIndicatorColor(Color.parseColor(nhlPreferences.color80()))
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
