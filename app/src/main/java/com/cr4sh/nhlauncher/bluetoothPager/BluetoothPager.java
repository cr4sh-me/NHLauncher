package com.cr4sh.nhlauncher.bluetoothPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BluetoothPager extends FragmentStateAdapter {

    public BluetoothPager(FragmentActivity fragment) {
        super(fragment);
    }

    @Override
    public int getItemCount() {
        // Return the number of fragments
        return 3;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the fragment for the given position
        if (position == 0) {
            return new BluetoothFragment1();
        } else if (position == 1) {
            return new BluetoothFragment2();
        }
//        else if (position == 2) {
//            return new BluetoothFragment3();
//        } else if (position == 3) {
//            return new BluetoothFragment4();
//        }
        else {
            return new BluetoothFragment5();
        }
    }
}
