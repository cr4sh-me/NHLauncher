package com.cr4sh.nhlauncher.pagers.netscannerPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class NetScannerPager(fragment: FragmentActivity?) : FragmentStateAdapter(
    fragment!!
) {
    override fun getItemCount(): Int {
        // Return the number of fragments
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        // Return the fragment for the given position
        return when (position) {
            0 -> {
                NetScannerFragment1()
            }

//            1 -> {
//                NetScannerFragment2()
//            }

            else -> {
                NetScannerFragment2()
            }
        }
    }
}

