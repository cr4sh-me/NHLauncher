package com.cr4sh.nhlauncher.dialogs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MainUtils;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.WPSAttack;

import java.util.Objects;

public class WpsCustomPinDialog extends AppCompatDialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wps_custom_pin_dialog, container, false);

//        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        MyPreferences myPreferences = new MyPreferences(requireActivity());

        setCancelable(false);

        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        TextView title = view.findViewById(R.id.dialog_title);
        Button setupButton = view.findViewById(R.id.setup_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        EditText customPin = view.findViewById(R.id.customPin);

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));

        customPin.setTextColor(Color.parseColor(myPreferences.color80()));
        customPin.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));
        customPin.setHintTextColor(Color.parseColor(myPreferences.color50()));

        setupButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        setupButton.setTextColor(Color.parseColor(myPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));


        setupButton.setOnClickListener(view12 -> {
            WPSAttack wpsAttack = new WPSAttack();
            if(customPin.getText().toString().isEmpty()){
                Toast.makeText(requireActivity(), "Empty input!", Toast.LENGTH_SHORT).show();
            } else {
                Objects.requireNonNull(getDialog()).cancel();
                wpsAttack.customPINCMD = "-p " + customPin.getText();
            }
        });

        cancelButton.setOnClickListener(view12 -> {
            Objects.requireNonNull(getDialog()).cancel();
            WPSAttack wpsAttack = new WPSAttack();
            wpsAttack.customPINCMD = "";
        });

        return view;

    }
}
