package com.cr4sh.nhlauncher.pagers.bluetoothPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class BluetoothPager(fragment: FragmentActivity?) : FragmentStateAdapter(
    fragment!!
) {
    override fun getItemCount(): Int {
        // Return the number of fragments
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        // Return the fragment for the given position
        return when (position) {
            0 -> {
                BluetoothFragment1()
            }

            1 -> {
                BluetoothFragment2()
            }

            else -> {
                BluetoothFragment5()
            }
        }
    }
}
