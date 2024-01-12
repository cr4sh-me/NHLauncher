package com.cr4sh.nhlauncher.dialogs;

import android.annotation.SuppressLint;
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
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.DialogUtils;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.ToastUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtils;

import java.util.Objects;


public class ButtonMenuDialog extends AppCompatDialogFragment {

    MainActivity myActivity;

    public ButtonMenuDialog(MainActivity activity) {
        this.myActivity = activity;
    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.button_menu_dialog, container, false);

        MainUtils mainUtils = new MainUtils(myActivity);
        NHLPreferences NHLPreferences = new NHLPreferences(myActivity);

        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        TextView title = view.findViewById(R.id.dialog_title);
        Button option1 = view.findViewById(R.id.option1);
        Button option2 = view.findViewById(R.id.option2);
        Button option3 = view.findViewById(R.id.option3);
        Button option4 = view.findViewById(R.id.option4);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        DialogUtils dialogUtils = new DialogUtils(requireActivity().getSupportFragmentManager());

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        title.setTextColor(Color.parseColor(NHLPreferences.color80()));

        option1.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        option1.setTextColor(Color.parseColor(NHLPreferences.color80()));
        option1.setTextColor(Color.parseColor(NHLPreferences.color80()));
        option2.setTextColor(Color.parseColor(NHLPreferences.color80()));
        option2.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        option3.setTextColor(Color.parseColor(NHLPreferences.color80()));
        option3.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        option4.setTextColor(Color.parseColor(NHLPreferences.color80()));
        option4.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));

        cancelButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(NHLPreferences.color50()));


        title.setText(myActivity.buttonName.toUpperCase() + " " + requireActivity().getResources().getString(R.string.options).toUpperCase());

        option1.setOnClickListener(view1 -> {
            VibrationUtils.vibrate(myActivity, 10);
            dialogUtils.openEditableDialog(myActivity.buttonName, myActivity.buttonCmd);
            Objects.requireNonNull(getDialog()).cancel();
        });

        option2.setOnClickListener(view12 -> {
            VibrationUtils.vibrate(myActivity, 10);
            mainUtils.addFavourite();
            Objects.requireNonNull(getDialog()).cancel();
        });

        option3.setOnClickListener(view1 -> {
            VibrationUtils.vibrate(myActivity, 10);
            if (!MainActivity.disableMenu) {
                dialogUtils.openNewToolDialog(myActivity.buttonCategory);
            } else {
                ToastUtils.showCustomToast(requireActivity(), getResources().getString(R.string.get_out));
            }
            Objects.requireNonNull(getDialog()).cancel();
        });

        option4.setOnClickListener(view1 -> {
            VibrationUtils.vibrate(myActivity, 10);
            dialogUtils.openDeleteToolDialog(myActivity.buttonName);
            Objects.requireNonNull(getDialog()).cancel();
        });

        cancelButton.setOnClickListener(view1 -> {
            VibrationUtils.vibrate(myActivity, 10);
            Objects.requireNonNull(getDialog()).cancel();
        });

        return view;

    }
}
