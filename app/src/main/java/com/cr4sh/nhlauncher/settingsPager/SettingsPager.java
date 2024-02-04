package com.cr4sh.nhlauncher.settingsPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SettingsPager extends FragmentStateAdapter {

    public SettingsPager(FragmentActivity fragment) {
        super(fragment);
    }

    @Override
    public int getItemCount() {
        // Return the number of fragments
        return 4;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the fragment for the given position
        if (position == 0) {
            return new SettingsFragment1();
        } else if (position == 1) {
            return new SettingsFragment2();
        } else if (position == 2) {
            return new SettingsFragment3();
        } else {
            return new SettingsFragment4();
        }
    }
}
