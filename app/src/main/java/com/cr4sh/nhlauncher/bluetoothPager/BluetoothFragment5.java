package com.cr4sh.nhlauncher.bluetoothPager;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;

import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.NHLPreferences;

import java.util.List;

public class BluetoothFragment5 extends Fragment {

    NHLPreferences NHLPreferences;

    public BluetoothFragment5() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bt_layout5, container, false);

        NHLPreferences = new NHLPreferences(requireActivity());

        TextView juiceInfo = view.findViewById(R.id.juiceInfo);
//        Spinner customAdvertise = view.findViewById(R.id.advertise);
        CheckBox randomCheckbox = view.findViewById(R.id.random);
        Button customAdvertise = view.findViewById(R.id.custom);
        Button startButton = view.findViewById(R.id.startButton);
        Button intervalButton = view.findViewById(R.id.interval);


        startButton.setTextColor(Color.parseColor(NHLPreferences.color80()));
        startButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));

        customAdvertise.setTextColor(Color.parseColor(NHLPreferences.color80()));
        customAdvertise.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));

        intervalButton.setTextColor(Color.parseColor(NHLPreferences.color80()));
        intervalButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));

        juiceInfo.setTextColor(Color.parseColor(NHLPreferences.color80()));

        randomCheckbox.setTextColor(Color.parseColor(NHLPreferences.color80()));

        List<Integer> imageList = List.of();

        List<String> valueList = List.of(
                "Airpods",
                "Airpods Pro",
                "Airpods Max",
                "Airpods Gen 2",
                "Airpods Gen 3",
                "Airpods Pro Gen 2",
                "PowerBeats",
                "PowerBeats Pro",
                "Beats Solo Pro",
                "Beats Studio Buds",
                "Beats Flex",
                "BeatsX",
                "Beats Solo3",
                "Beats Studio3",
                "Beats Studio Pro",
                "Beats Fit Pro",
                "Beats Studio Buds+",
                "AppleTV Setup",
                "AppleTV Pair",
                "AppleTV New User",
                "AppleTV AppleID Setup",
                "AppleTV Wireless Audio Sync",
                "AppleTV Homekit Setup",
                "AppleTV Keyboard",
                "AppleTV 'Connecting to Network'",
                "Homepod Setup",
                "Setup New Phone",
                "Transfer Number to New Phone",
                "TV Color Balance"
        );

        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {Color.parseColor(NHLPreferences.color80()), Color.parseColor(NHLPreferences.color80())};
        CompoundButtonCompat.setButtonTintList(randomCheckbox, new ColorStateList(states, colors));

        return view;
    }

    private void setContainerBackground(LinearLayout container) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(60);
        drawable.setStroke(8, Color.parseColor(NHLPreferences.color50()));
        container.setBackground(drawable);
    }

    private void setButtonColors(Button button) {
        button.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        button.setTextColor(Color.parseColor(NHLPreferences.color80()));
    }

}
