package com.cr4sh.nhlauncher.dialogs;

import android.annotation.SuppressLint;
import android.database.Cursor;
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
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.ToastUtils;

import java.util.Objects;

public class NewToolDialog extends AppCompatDialogFragment {

    @SuppressLint("Recycle")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_tool_dialog, container, false);

        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        MyPreferences myPreferences = new MyPreferences(requireActivity());

        assert getArguments() != null;
        String category = getArguments().getString("category");

        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        TextView title = view.findViewById(R.id.dialog_title);
        EditText myName = view.findViewById(R.id.textview1);
        EditText myDescription = view.findViewById(R.id.textview2);
        EditText myCmd = view.findViewById(R.id.textview3);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button saveButton = view.findViewById(R.id.save_button);

        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));
        myName.setTextColor(Color.parseColor(myPreferences.color80()));
        myDescription.setTextColor(Color.parseColor(myPreferences.color80()));
        myCmd.setTextColor(Color.parseColor(myPreferences.color80()));

        myName.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));
        myName.setHintTextColor(Color.parseColor(myPreferences.color50()));
        myCmd.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));
        myCmd.setHintTextColor(Color.parseColor(myPreferences.color50()));
        myDescription.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));
        myDescription.setHintTextColor(Color.parseColor(myPreferences.color50()));


        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        saveButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        saveButton.setTextColor(Color.parseColor(myPreferences.color80()));

        saveButton.setOnClickListener(view12 -> {
            // Idiot protection...
            if (myName.getText().toString().isEmpty()) {
                ToastUtils.showCustomToast(requireActivity(), getResources().getString(R.string.name_empty));
            } else if (myDescription.getText().toString().isEmpty()) {
                ToastUtils.showCustomToast(requireActivity(), getResources().getString(R.string.desc_empty));
            } else if (myCmd.getText().toString().isEmpty()) {
                ToastUtils.showCustomToast(requireActivity(), getResources().getString(R.string.cmd_empty));
            } else {
                try (SQLiteOpenHelper dbHandler = new DBHandler(requireActivity());
                     SQLiteDatabase db = dbHandler.getReadableDatabase()) {

                    Cursor cursor;
                    String[] selectionArgs = {myName.getText().toString()};
                    cursor = db.query("TOOLS", new String[]{"NAME"}, "NAME LIKE ?", selectionArgs, null, null, null, null);

                    if (cursor.getCount() == 0) {
                        // What did you expect here ??
                        DBHandler.insertTool(db, 0, category, 0, myName.getText().toString().trim(), myDescription.getText().toString().trim(), myDescription.getText().toString().trim(), myCmd.getText().toString().trim(), "kali_menu", 0);
                        mainUtils.restartSpinner();
                        Objects.requireNonNull(getDialog()).cancel();
                        ToastUtils.showCustomToast(requireActivity(), getResources().getString(R.string.added));
                    } else {
                        ToastUtils.showCustomToast(requireActivity(), getResources().getString(R.string.name_exist));
                    }

                } catch (SQLException e) {
                    // Handle the exception here, for example:
                    ToastUtils.showCustomToast(requireActivity(), "E: " + e);
                }
            }

        });

        cancelButton.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).cancel());

        return view;

    }
}
