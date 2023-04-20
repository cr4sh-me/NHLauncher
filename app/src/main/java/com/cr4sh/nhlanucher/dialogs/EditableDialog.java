package com.cr4sh.nhlanucher.dialogs;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlanucher.DBHandler;
import com.cr4sh.nhlanucher.MainUtils;
import com.cr4sh.nhlanucher.R;

import java.util.Objects;

public class EditableDialog extends AppCompatDialogFragment {

    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.editable_dialog, container, false);

        // Get arguments
        assert getArguments() != null;
        String name = getArguments().getString("name");
        String cmd = getArguments().getString("cmd");

        TextView currentCommand = view.findViewById(R.id.text_view1);
        EditText newCmd = view.findViewById(R.id.edit_text1);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button saveButton = view.findViewById(R.id.save_button);

        currentCommand.setText(requireActivity().getResources().getString(R.string.current_cmd) + cmd);

        String frameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("frameColor", "frame6");
        String nameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("nameColor", "#FFFFFF");
        @SuppressLint("DiscouragedApi") int frame = requireActivity().getResources().getIdentifier(frameColor, "drawable", requireActivity().getPackageName());

        view.setBackgroundResource(frame);
        newCmd.setTextColor(Color.parseColor(nameColor));
        cancelButton.setTextColor(Color.parseColor(nameColor));
        saveButton.setTextColor(Color.parseColor(nameColor));

        saveButton.setOnClickListener(view12 -> {
            // Idiot protection...
            if (newCmd.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), requireActivity().getResources().getString(R.string.empty_input), Toast.LENGTH_SHORT).show();
            } else {
                try (SQLiteOpenHelper dbHandler = new DBHandler(getActivity());
                     SQLiteDatabase db = dbHandler.getReadableDatabase()) {
                    DBHandler.updateToolCmd(db, name, newCmd.getText().toString().trim());
                    MainUtils.restartSpinner();
                    Toast.makeText(getActivity(), requireActivity().getResources().getString(R.string.command_updated), Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(getDialog()).cancel();
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
