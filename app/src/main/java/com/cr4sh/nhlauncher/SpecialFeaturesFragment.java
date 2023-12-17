package com.cr4sh.nhlauncher;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class SpecialFeaturesFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.special_fragment_layout, container, false);
        MyPreferences myPreferences = new MyPreferences(requireActivity());

        CustomButton customButton1 = view.findViewById(R.id.button1);
        customButton1.setImageResource(R.drawable.kali_fern_wifi_cracker);
        customButton1.setName("WPS ATTACK");
        customButton1.setNameColor(Color.parseColor(myPreferences.color80()));
        customButton1.setDescription("USE ONESHOT SCRIPT TO PERFORM WPS ATTACKS!");
        customButton1.setDescriptionColor(Color.parseColor(myPreferences.color80()));

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(60);
        drawable.setStroke(8, Color.parseColor(myPreferences.color80()));
        customButton1.setBackground(drawable);

        customButton1.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), WPSAttack.class);
            startActivity(intent);
        });

        return view;
    }

}
