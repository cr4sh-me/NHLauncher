package com.cr4sh.nhlauncher.dialogs

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.SQLException
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.database.DBHandler
import com.cr4sh.nhlauncher.database.DBHandler.Companion.insertTool
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.VibrationUtils

class NewToolDialog : AppCompatDialogFragment() {
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    @SuppressLint("Recycle")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_tool_dialog, container, false)
        val mainUtils = NHLUtils((requireActivity() as MainActivity))
        val nhlPreferences = NHLPreferences(requireActivity())
        assert(arguments != null)
        val category = requireArguments().getString("category")
        val bkg = view.findViewById<LinearLayout>(R.id.custom_theme_dialog_background)
        val title = view.findViewById<TextView>(R.id.dialog_title)
        val myName = view.findViewById<EditText>(R.id.textview1)
        val myDescription = view.findViewById<EditText>(R.id.textview2)
        val myCmd = view.findViewById<EditText>(R.id.textview3)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)
        val saveButton = view.findViewById<Button>(R.id.save_button)
        bkg.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
        title.setTextColor(Color.parseColor(nhlPreferences.color80()))

        ColorChanger.setEditTextColor(myName)
        ColorChanger.setEditTextColor(myDescription)
        ColorChanger.setEditTextColor(myCmd)

        ColorChanger.setButtonColors(saveButton)
        ColorChanger.setButtonColors(cancelButton, true)

        saveButton.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            // Idiot protection...
            if (myName.text.toString().isEmpty()) {
                showCustomToast(requireActivity(), resources.getString(R.string.name_empty))
            } else if (myDescription.text.toString().isEmpty()) {
                showCustomToast(requireActivity(), resources.getString(R.string.desc_empty))
            } else if (myCmd.text.toString().isEmpty()) {
                showCustomToast(requireActivity(), resources.getString(R.string.cmd_empty))
            } else {
                try {
                    DBHandler(requireActivity()).use { dbHandler ->
                        dbHandler.readableDatabase.use { db ->
                            val cursor: Cursor
                            val selectionArgs = arrayOf(myName.text.toString())
                            cursor = db.query(
                                "TOOLS",
                                arrayOf("NAME"),
                                "NAME LIKE ?",
                                selectionArgs,
                                null,
                                null,
                                null,
                                null
                            )
                            if (cursor.count == 0) {
                                // What did you expect here ??
                                insertTool(
                                    db,
                                    0,
                                    category,
                                    0,
                                    myName.text.toString().trim { it <= ' ' },
                                    myDescription.text.toString().trim { it <= ' ' },
                                    myDescription.text.toString().trim { it <= ' ' },
                                    myCmd.text.toString().trim { it <= ' ' },
                                    "kali_menu",
                                    0
                                )
                                mainUtils.restartSpinner()
                                dialog?.cancel()
                                showCustomToast(
                                    requireActivity(),
                                    resources.getString(R.string.added)
                                )
                            } else {
                                showCustomToast(
                                    requireActivity(),
                                    resources.getString(R.string.name_exist)
                                )
                            }
                        }
                    }
                } catch (e: SQLException) {
                    // Handle the exception here, for example:
                    showCustomToast(requireActivity(), "E: $e")
                }
            }
        }
        cancelButton.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            dialog?.cancel()
        }
        return view
    }
}
