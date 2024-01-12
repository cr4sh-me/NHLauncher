package com.cr4sh.nhlauncher.dialogs;

import android.annotation.SuppressLint;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.Database.DBHandler;
import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.NHLManager;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.ToastUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtils;

import java.util.Objects;

public class EditableDialog extends AppCompatDialogFragment {

    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();

    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.editable_dialog, container, false);

        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        NHLPreferences NHLPreferences = new NHLPreferences(requireActivity());

        // Get arguments
        assert getArguments() != null;
        String name = getArguments().getString("name");
        String cmd = getArguments().getString("cmd");

        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        TextView title = view.findViewById(R.id.dialog_title);
        TextView currentCommand = view.findViewById(R.id.text_view1);
        EditText newCmd = view.findViewById(R.id.edit_text1);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button saveButton = view.findViewById(R.id.save_button);

        bkg.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        title.setTextColor(Color.parseColor(NHLPreferences.color80()));
        currentCommand.setTextColor(Color.parseColor(NHLPreferences.color50()));

        newCmd.getBackground().mutate().setTint(Color.parseColor(NHLPreferences.color50()));
        newCmd.setHintTextColor(Color.parseColor(NHLPreferences.color50()));
        newCmd.setTextColor(Color.parseColor(NHLPreferences.color80()));

        saveButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        saveButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(NHLPreferences.color50()));

        currentCommand.setText(requireActivity().getResources().getString(R.string.current_cmd) + cmd);

        saveButton.setOnClickListener(view12 -> {
            VibrationUtils.vibrate(mainActivity, 10);

            // Idiot protection...
            if (newCmd.getText().toString().isEmpty()) {
                ToastUtils.showCustomToast(requireActivity(), requireActivity().getResources().getString(R.string.empty_input));
            } else {
                try (SQLiteOpenHelper dbHandler = new DBHandler(getActivity());
                     SQLiteDatabase db = dbHandler.getReadableDatabase()) {
                    DBHandler.updateToolCmd(db, name, newCmd.getText().toString().trim());
                    mainUtils.restartSpinner();
                    ToastUtils.showCustomToast(requireActivity(), requireActivity().getResources().getString(R.string.command_updated));
                    Objects.requireNonNull(getDialog()).cancel();
                } catch (SQLException e) {
                    // Handle the exception here, for example:
                    ToastUtils.showCustomToast(requireActivity(), "E: " + e);
                }
            }

        });

        cancelButton.setOnClickListener(view1 -> {
            VibrationUtils.vibrate(mainActivity, 10);
            Objects.requireNonNull(getDialog()).cancel();
        });

        return view;

    }
}
