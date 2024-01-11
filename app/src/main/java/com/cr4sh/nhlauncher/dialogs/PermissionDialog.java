package com.cr4sh.nhlauncher.dialogs;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.NHLManager;
import com.cr4sh.nhlauncher.NHLPreferences;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.PermissionUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtil;

import java.util.Objects;

public class PermissionDialog extends AppCompatDialogFragment {

    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permissions_dialog, container, false);

        setCancelable(false);

        NHLPreferences NHLPreferences = new NHLPreferences(requireActivity());

        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        TextView title = view.findViewById(R.id.dialog_title);
        TextView text = view.findViewById(R.id.text_view1);
        Button allowButton = view.findViewById(R.id.allow_button);

        bkg.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        title.setTextColor(Color.parseColor(NHLPreferences.color80()));
        text.setTextColor(Color.parseColor(NHLPreferences.color80()));

        allowButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        allowButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

        allowButton.setOnClickListener(view12 -> {
            VibrationUtil.vibrate(mainActivity, 10);
            Objects.requireNonNull(getDialog()).cancel();
            PermissionUtils permissionUtils = new PermissionUtils((MainActivity) requireActivity());
            permissionUtils.takePermissions();
        });

        return view;

    }
}
