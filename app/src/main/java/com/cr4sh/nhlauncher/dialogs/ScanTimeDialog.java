package com.cr4sh.nhlauncher.dialogs;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.BluetoothPager.BluetoothFragment1;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;

import java.util.Objects;

public class ScanTimeDialog extends AppCompatDialogFragment {

    BluetoothFragment1 wpsAttack;

    public ScanTimeDialog(BluetoothFragment1 activity) {
        this.wpsAttack = activity;
    }


    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wps_custom_pin_dialog, container, false);

//        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        MyPreferences myPreferences = new MyPreferences(requireActivity());

        assert getArguments() != null;
        int option = getArguments().getInt("option");

        setCancelable(false);

        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        TextView title = view.findViewById(R.id.dialog_title);
        Button setupButton = view.findViewById(R.id.setup_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        EditText customPin = view.findViewById(R.id.customPin);

        if (option == 1){
            cancelButton.setText("Use default (10s)");
            title.setText("Custom Scan Time");
            customPin.setHint("Scan Time");
        }

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
            if(customPin.getText().toString().isEmpty()){
                Toast.makeText(requireActivity(), "Empty input!", Toast.LENGTH_SHORT).show();
            } else {
                if (option == 1){
                    int number = Integer.parseInt(customPin.getText().toString());
                    if(number > 0){
                        wpsAttack.scanTime = customPin.getText().toString();
                        Objects.requireNonNull(getDialog()).cancel();
                    } else {
                        Toast.makeText(requireActivity(), "Are you dumb?", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancelButton.setOnClickListener(view12 -> {
            Objects.requireNonNull(getDialog()).cancel();
            if (option == 1){
                wpsAttack.scanTime = "15";
            }
        });

        return view;

    }
}
