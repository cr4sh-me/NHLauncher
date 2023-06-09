package com.cr4sh.nhlauncher.dialogs;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.PermissionUtils;
import com.cr4sh.nhlauncher.R;

import java.util.Objects;

public class PermissionDialog extends AppCompatDialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permissions_dialog, container, false);

        setCancelable(false);

        String frameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("frameColor", "frame6");
        String nameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("nameColor", "#FFFFFF");
        @SuppressLint("DiscouragedApi") int frame = requireActivity().getResources().getIdentifier(frameColor, "drawable", requireActivity().getPackageName());

        Button allowButton = view.findViewById(R.id.allow_button);

        view.setBackgroundResource(frame);
        allowButton.setTextColor(Color.parseColor(nameColor));

        allowButton.setOnClickListener(view12 -> {
            Objects.requireNonNull(getDialog()).cancel();
            PermissionUtils permissionUtils = new PermissionUtils((MainActivity) requireActivity());
            permissionUtils.takePermissions();
        });

        return view;

    }
}
