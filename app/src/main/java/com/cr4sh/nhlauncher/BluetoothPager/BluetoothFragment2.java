package com.cr4sh.nhlauncher.BluetoothPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cr4sh.nhlauncher.R;

public class BluetoothFragment2 extends Fragment {

    public BluetoothFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bt_layout2, container, false);
    }
}
