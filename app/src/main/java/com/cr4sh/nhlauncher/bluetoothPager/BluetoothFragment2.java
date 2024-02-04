package com.cr4sh.nhlauncher.bluetoothPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;

import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.bridge.Bridge;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.ToastUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtils;

public class BluetoothFragment2 extends Fragment {

    NHLPreferences NHLPreferences;

    private String flood = null;
    private String reverse = null;

    public BluetoothFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bt_layout2, container, false);

        NHLPreferences = new NHLPreferences(requireActivity());

        LinearLayout l2pingContainer = view.findViewById(R.id.l2pingContainer);
        LinearLayout redfangContainer = view.findViewById(R.id.redfangContainer);
        LinearLayout bluerangerContainer = view.findViewById(R.id.bluerangerContainer);
        LinearLayout sdpContainer = view.findViewById(R.id.sdpContainer);
        LinearLayout rfcommContainer = view.findViewById(R.id.rfcommContainer);

        TextView l2pingText = view.findViewById(R.id.l2pingText);
        TextView redfangText = view.findViewById(R.id.redfangText);
        TextView bluerangerText = view.findViewById(R.id.bluerangerText);
        TextView sdpText = view.findViewById(R.id.sdpText);
        TextView rfcommText = view.findViewById(R.id.rfcommText);

        l2pingText.setTextColor(Color.parseColor(NHLPreferences.color80()));
        redfangText.setTextColor(Color.parseColor(NHLPreferences.color80()));
        bluerangerText.setTextColor(Color.parseColor(NHLPreferences.color80()));
        sdpText.setTextColor(Color.parseColor(NHLPreferences.color80()));
        rfcommText.setTextColor(Color.parseColor(NHLPreferences.color80()));

        setContainerBackground(l2pingContainer, NHLPreferences.color50());
        setContainerBackground(redfangContainer, NHLPreferences.color50());
        setContainerBackground(bluerangerContainer, NHLPreferences.color50());
        setContainerBackground(sdpContainer, NHLPreferences.color50());
        setContainerBackground(rfcommContainer, NHLPreferences.color50());

        TextView l2pingInfo = view.findViewById(R.id.l2pingInfo);
        TextView redfangInfo = view.findViewById(R.id.redfangInfo);
        TextView bluerangerInfo = view.findViewById(R.id.bluerangerInfo);
        TextView sdpInfo = view.findViewById(R.id.sdpInfo);
//        TextView rfcommInfo = view.findViewById(R.id.rfcomm)

        l2pingInfo.setTextColor(Color.parseColor(NHLPreferences.color80()));
        redfangInfo.setTextColor(Color.parseColor(NHLPreferences.color80()));
        bluerangerInfo.setTextColor(Color.parseColor(NHLPreferences.color80()));
        sdpInfo.setTextColor(Color.parseColor(NHLPreferences.color80()));
//        rfcommInfo.setTextColor(Color.parseColor(myPreferences.color50()));

        // Find Buttons and set background and text colors
        Button l2pingButton = view.findViewById(R.id.startPingButton);
        Button redfangButton = view.findViewById(R.id.startRedfangButton);
        Button bluerangerButton = view.findViewById(R.id.startBlueranger);
        Button sdpButton = view.findViewById(R.id.startSdp);
        Button rfcommButton = view.findViewById(R.id.startRfcomm);

        setButtonColors(l2pingButton);
        setButtonColors(redfangButton);
        setButtonColors(bluerangerButton);
        setButtonColors(sdpButton);
        setButtonColors(rfcommButton);

        TextView sizeText = view.findViewById(R.id.l2ping_size);
        EditText sizeEdit = view.findViewById(R.id.l2ping_size_edit);

        TextView countText = view.findViewById(R.id.l2ping_count);
        TextView countEdit = view.findViewById(R.id.l2ping_count_edit);

        sizeText.setTextColor(Color.parseColor(NHLPreferences.color80()));
        sizeEdit.setTextColor(Color.parseColor(NHLPreferences.color80()));

        countText.setTextColor(Color.parseColor(NHLPreferences.color80()));
        countEdit.setTextColor(Color.parseColor(NHLPreferences.color80()));
//
        sizeEdit.setHintTextColor(Color.parseColor(NHLPreferences.color50()));
        countEdit.setHintTextColor(Color.parseColor(NHLPreferences.color50()));

        sizeEdit.getBackground().mutate().setTint(Color.parseColor(NHLPreferences.color50()));
        countEdit.getBackground().mutate().setTint(Color.parseColor(NHLPreferences.color50()));

        CheckBox floodPingCheckbox = view.findViewById(R.id.flood);
        CheckBox reversePingCheckBox = view.findViewById(R.id.reversePing);

        floodPingCheckbox.setTextColor(Color.parseColor(NHLPreferences.color80()));
        reversePingCheckBox.setTextColor(Color.parseColor(NHLPreferences.color80()));

        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {Color.parseColor(NHLPreferences.color80()), Color.parseColor(NHLPreferences.color80())};
        CompoundButtonCompat.setButtonTintList(floodPingCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(reversePingCheckBox, new ColorStateList(states, colors));

        TextView rangeText = view.findViewById(R.id.redfang_range);
        EditText rangeEdit = view.findViewById(R.id.redfang_range_edit);

        rangeText.setTextColor(Color.parseColor(NHLPreferences.color80()));
        rangeEdit.setTextColor(Color.parseColor(NHLPreferences.color80()));
        rangeEdit.setHintTextColor(Color.parseColor(NHLPreferences.color50()));
        rangeEdit.getBackground().mutate().setTint(Color.parseColor(NHLPreferences.color50()));

        floodPingCheckbox.setOnClickListener(v -> {
            if (floodPingCheckbox.isChecked())
//                flood = " -f ";
                flood = "n";
            else
                flood = "";
        });
        reversePingCheckBox.setOnClickListener(v -> {
            if (reversePingCheckBox.isChecked())
                reverse = " -r ";
            else
                reverse = "";
        });


        l2pingButton.setOnClickListener(v -> {
            VibrationUtils.vibrate(requireActivity(), 10);

            String l2ping_target = BluetoothFragment1.getSelectedTarget();
            if (!l2ping_target.equals("")) {
                String l2ping_size = sizeEdit.getText().toString();
                String l2ping_count = countEdit.getText().toString();
                String l2ping_interface = BluetoothFragment1.getSelectedIface(); // TODO do this
                run_cmd("echo -ne \"\\033]0;Pinging BT device\\007\" && clear;l2ping -i " + l2ping_interface + " -s " + l2ping_size + " -c " + l2ping_count + flood + reverse + " " + l2ping_target + " && echo \"\nPinging done, closing in 3 secs..\";sleep 3 && exit");
            } else {
                ToastUtils.showCustomToast(requireActivity(), "No target address!");
            }


        });

        return view;
    }

    public void run_cmd(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.Companion.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd, false);
        requireActivity().startActivity(intent);
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
