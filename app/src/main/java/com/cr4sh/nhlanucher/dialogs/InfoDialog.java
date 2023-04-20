package com.cr4sh.nhlanucher.dialogs;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlanucher.R;

import java.util.Objects;

public class InfoDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_dialog, container, false);

        String frameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("frameColor", "frame6");
        String nameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("nameColor", "#FFFFFF");
        @SuppressLint("DiscouragedApi") int frame = requireActivity().getResources().getIdentifier(frameColor, "drawable", requireActivity().getPackageName());

        Button wowButton = view.findViewById(R.id.wow_button);

        view.setBackgroundResource(frame);
        wowButton.setTextColor(Color.parseColor(nameColor));

        wowButton.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).cancel());

        return view;
    }

}