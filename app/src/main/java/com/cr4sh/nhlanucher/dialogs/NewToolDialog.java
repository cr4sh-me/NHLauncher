package com.cr4sh.nhlanucher.dialogs;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlanucher.DBHandler;
import com.cr4sh.nhlanucher.MainUtils;
import com.cr4sh.nhlanucher.R;

import java.util.Objects;

public class NewToolDialog extends AppCompatDialogFragment {

    @SuppressLint("Recycle")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_tool_dialog, container, false);

        assert getArguments() != null;
        String category = getArguments().getString("category");

        EditText myName = view.findViewById(R.id.textview1);
        EditText myDescription = view.findViewById(R.id.textview2);
        EditText myCmd = view.findViewById(R.id.textview3);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button saveButton = view.findViewById(R.id.save_button);

        String frameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("frameColor", "frame6");
        String nameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("nameColor", "#FFFFFF");
        @SuppressLint("DiscouragedApi") int frame = requireActivity().getResources().getIdentifier(frameColor, "drawable", requireActivity().getPackageName());

        view.setBackgroundResource(frame);
        myName.setTextColor(Color.parseColor(nameColor));
        myDescription.setTextColor(Color.parseColor(nameColor));
        myCmd.setTextColor(Color.parseColor(nameColor));
        cancelButton.setTextColor(Color.parseColor(nameColor));
        saveButton.setTextColor(Color.parseColor(nameColor));

        saveButton.setOnClickListener(view12 -> {
            // Idiot protection...
            if (myName.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.name_empty), Toast.LENGTH_SHORT).show();
            } else if (myDescription.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.desc_empty), Toast.LENGTH_SHORT).show();
            } else if (myCmd.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.cmd_empty), Toast.LENGTH_SHORT).show();
            } else {
                try (SQLiteOpenHelper dbHandler = new DBHandler(requireActivity());
                     SQLiteDatabase db = dbHandler.getReadableDatabase()) {

                    Cursor cursor;
                    String[] selectionArgs = {myName.getText().toString()};
                    cursor = db.query("TOOLS", new String[]{ "NAME"}, "NAME LIKE ?", selectionArgs, null, null, null, null);

                    if (cursor.getCount() == 0) {
                        // What did you expect here ??
                        DBHandler.insertTool(db, 0, category, 0, myName.getText().toString().trim(), myDescription.getText().toString().trim(), myDescription.getText().toString().trim(),  myCmd.getText().toString().trim(), "kali_menu", 0);
//                        MainUtils.refreshRecyclerViewData("13");
                        MainUtils.restartSpinner();
                        Objects.requireNonNull(getDialog()).cancel();
                        Toast.makeText(requireActivity(), getResources().getString(R.string.added), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireActivity(), getResources().getString(R.string.name_exist), Toast.LENGTH_SHORT).show();
                    }

                } catch (SQLException e) {
                    // Handle the exception here, for example:
                    Toast.makeText(getActivity(), "E: " + e, Toast.LENGTH_SHORT).show();
                }
            }

        });

        cancelButton.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).cancel());

        return view;

    }
}
