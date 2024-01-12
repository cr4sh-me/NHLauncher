package com.cr4sh.nhlauncher.BluetoothPager;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.NHLPreferences;

public class BluetoothFragment4 extends Fragment {

    NHLPreferences NHLPreferences;

    public BluetoothFragment4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bt_layout4, container, false);

        NHLPreferences = new NHLPreferences(requireActivity());


        return view;
    }

    private void setContainerBackground(LinearLayout container, String color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(60);
        drawable.setStroke(8, Color.parseColor(color));
        container.setBackground(drawable);
    }

    private void setButtonColors(Button button) {
        button.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        button.setTextColor(Color.parseColor(NHLPreferences.color80()));
    }

}
