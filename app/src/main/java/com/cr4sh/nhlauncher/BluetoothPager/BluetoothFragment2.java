package com.cr4sh.nhlauncher.BluetoothPager;

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

import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;

public class BluetoothFragment2 extends Fragment {

    MyPreferences myPreferences;

    public BluetoothFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bt_layout2, container, false);

        myPreferences = new MyPreferences(requireActivity());

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

        l2pingText.setTextColor(Color.parseColor(myPreferences.color80()));
        redfangText.setTextColor(Color.parseColor(myPreferences.color80()));
        bluerangerText.setTextColor(Color.parseColor(myPreferences.color80()));
        sdpText.setTextColor(Color.parseColor(myPreferences.color80()));
        rfcommText.setTextColor(Color.parseColor(myPreferences.color80()));

        setContainerBackground(l2pingContainer, myPreferences.color50());
        setContainerBackground(redfangContainer, myPreferences.color50());
        setContainerBackground(bluerangerContainer, myPreferences.color50());
        setContainerBackground(sdpContainer, myPreferences.color50());
        setContainerBackground(rfcommContainer, myPreferences.color50());

        TextView l2pingInfo = view.findViewById(R.id.l2pingInfo);
        TextView redfangInfo = view.findViewById(R.id.redfangInfo);
        TextView bluerangerInfo = view.findViewById(R.id.bluerangerInfo);
        TextView sdpInfo = view.findViewById(R.id.sdpInfo);
//        TextView rfcommInfo = view.findViewById(R.id.rfcomm)

        l2pingInfo.setTextColor(Color.parseColor(myPreferences.color80()));
        redfangInfo.setTextColor(Color.parseColor(myPreferences.color80()));
        bluerangerInfo.setTextColor(Color.parseColor(myPreferences.color80()));
        sdpInfo.setTextColor(Color.parseColor(myPreferences.color80()));
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

        sizeText.setTextColor(Color.parseColor(myPreferences.color80()));
        sizeEdit.setTextColor(Color.parseColor(myPreferences.color80()));

        countText.setTextColor(Color.parseColor(myPreferences.color80()));
        countEdit.setTextColor(Color.parseColor(myPreferences.color80()));
//
        sizeEdit.setHintTextColor(Color.parseColor(myPreferences.color50()));
        countEdit.setHintTextColor(Color.parseColor(myPreferences.color50()));

        sizeEdit.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));
        countEdit.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));

        CheckBox floodPingCheckbox = view.findViewById(R.id.flood);
        CheckBox reversePingCheckBox = view.findViewById(R.id.reversePing);

        floodPingCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));
        reversePingCheckBox.setTextColor(Color.parseColor(myPreferences.color80()));

        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {Color.parseColor(myPreferences.color80()), Color.parseColor(myPreferences.color80())};
        CompoundButtonCompat.setButtonTintList(floodPingCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(reversePingCheckBox, new ColorStateList(states, colors));

        TextView rangeText = view.findViewById(R.id.redfang_range);
        EditText rangeEdit = view.findViewById(R.id.redfang_range_edit);

        rangeText.setTextColor(Color.parseColor(myPreferences.color80()));
        rangeEdit.setTextColor(Color.parseColor(myPreferences.color80()));
        rangeEdit.setHintTextColor(Color.parseColor(myPreferences.color50()));
        rangeEdit.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));


        return view;
    }

    private void setContainerBackground(LinearLayout container, String color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(60);
        drawable.setStroke(8, Color.parseColor(color));
        container.setBackground(drawable);
    }

    private void setButtonColors(Button button) {
        button.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        button.setTextColor(Color.parseColor(myPreferences.color80()));
    }

}
