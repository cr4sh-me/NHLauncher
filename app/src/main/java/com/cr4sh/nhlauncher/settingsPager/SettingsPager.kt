package com.cr4sh.nhlauncher.settingsPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SettingsPager(fragment: FragmentActivity?) : FragmentStateAdapter(
    fragment!!
) {
    override fun getItemCount(): Int {
        // Return the number of fragments
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        // Return the fragment for the given position
        return when (position) {
            0 -> {
                SettingsFragment1()
            }

            1 -> {
                SettingsFragment2()
            }

            2 -> {
                SettingsFragment3()
            }

            else -> {
                SettingsFragment4()
            }
        }
    }
}
