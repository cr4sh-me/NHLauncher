package com.cr4sh.nhlauncher.dialogs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.DialogUtils;
import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;

import java.util.Objects;


public class ToolbarDialog extends AppCompatDialogFragment {
    MainActivity myActivity;
    public ToolbarDialog(MainActivity activity) {
        this.myActivity = activity;
    }
    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.toolbar_dialog, container, false);

        MyPreferences myPreferences = new MyPreferences(myActivity);

        TextView title = view.findViewById(R.id.dialog_title);
        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        Button option1 = view.findViewById(R.id.option1);
        Button option2 = view.findViewById(R.id.option2);
        Button option3 = view.findViewById(R.id.option3);
        Button option4 = view.findViewById(R.id.option4);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        DialogUtils dialogUtils = new DialogUtils(requireActivity().getSupportFragmentManager());

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));

        option1.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        option1.setTextColor(Color.parseColor(myPreferences.color80()));
        option1.setTextColor(Color.parseColor(myPreferences.color80()));
        option2.setTextColor(Color.parseColor(myPreferences.color80()));
        option2.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        option3.setTextColor(Color.parseColor(myPreferences.color80()));
        option3.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        option4.setTextColor(Color.parseColor(myPreferences.color80()));
        option4.setBackgroundColor(Color.parseColor(myPreferences.color50()));

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));


//        text1.setText(myActivity.buttonName.toUpperCase() + " " + requireActivity().getResources().getString(R.string.options).toUpperCase());

        option1.setOnClickListener(view1 -> {
            dialogUtils.openSettingsDialog();
            Objects.requireNonNull(getDialog()).cancel();
        });

        option2.setOnClickListener(view12 -> {
            dialogUtils.openCustomThemesDialog();
            Objects.requireNonNull(getDialog()).cancel();
        });

        option3.setOnClickListener(view1 -> {
            dialogUtils.openStatisticsDialog(myActivity);
            Objects.requireNonNull(getDialog()).cancel();
        });

        option4.setOnClickListener(view1 -> {
            String url = "https://github.com/cr4sh-me/NHLauncher";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            Objects.requireNonNull(getDialog()).cancel();
        });

        cancelButton.setOnClickListener(view1 -> {
            Objects.requireNonNull(getDialog()).cancel();
        });

        Objects.requireNonNull(getDialog()).setOnCancelListener(dialog -> myActivity.toolbar.setEnabled(true));

        return view;

    }
}
