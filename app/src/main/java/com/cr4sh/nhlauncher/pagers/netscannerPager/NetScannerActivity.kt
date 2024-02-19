package com.cr4sh.nhlauncher.pagers.netscannerPager

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.specialFeatures.SpecialFeaturesActivity
import com.cr4sh.nhlauncher.utils.ColorChanger.Companion.activityAnimation
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class NetScannerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityAnimation()

        setContentView(R.layout.netscanner_layout)
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
            val intent = Intent(this@NetScannerActivity, SpecialFeaturesActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
        val viewPager2 = findViewById<ViewPager2>(R.id.pager)
        val adapter = NetScannerPager(this)
        viewPager2.adapter = adapter
        viewPager2.offscreenPageLimit = 5
        val tabs = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabs, viewPager2) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.setText("Nmap")
                1 -> tab.setText("Tools")
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
            activityAnimation()
        }
    }
}
