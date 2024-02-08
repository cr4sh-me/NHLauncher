package com.cr4sh.nhlauncher.dialogs

import android.annotation.SuppressLint
import android.database.SQLException
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.database.DBHandler
import com.cr4sh.nhlauncher.database.DBHandler.Companion.updateToolCmd
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate

class EditableDialog : AppCompatDialogFragment() {
    private val mainActivity: MainActivity = NHLManager.instance.mainActivity

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.editable_dialog, container, false)
        val mainUtils = NHLUtils((requireActivity() as MainActivity))
        val nhlPreferences = NHLPreferences(requireActivity())
        assert(arguments != null)
        val name = requireArguments().getString("name")
        val cmd = requireArguments().getString("cmd")
        val bkg = view.findViewById<LinearLayout>(R.id.custom_theme_dialog_background)
        val title = view.findViewById<TextView>(R.id.dialog_title)
        val currentCommand = view.findViewById<TextView>(R.id.text_view1)
        val newCmd = view.findViewById<EditText>(R.id.edit_text1)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)
        val saveButton = view.findViewById<Button>(R.id.save_button)
        bkg.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
        title.setTextColor(Color.parseColor(nhlPreferences.color80()))
        currentCommand.setTextColor(Color.parseColor(nhlPreferences.color50()))
        newCmd.background.mutate().setTint(Color.parseColor(nhlPreferences.color50()))
        newCmd.setHintTextColor(Color.parseColor(nhlPreferences.color50()))
        newCmd.setTextColor(Color.parseColor(nhlPreferences.color80()))
        saveButton.setBackgroundColor(Color.parseColor(nhlPreferences.color50()))
        saveButton.setTextColor(Color.parseColor(nhlPreferences.color80()))
        cancelButton.setBackgroundColor(Color.parseColor(nhlPreferences.color80()))
        cancelButton.setTextColor(Color.parseColor(nhlPreferences.color50()))
        currentCommand.text = requireActivity().resources.getString(R.string.current_cmd) + cmd
        saveButton.setOnClickListener {
            vibrate(mainActivity, 10)

            // Idiot protection...
            if (newCmd.text.toString().isEmpty()) {
                showCustomToast(
                    requireActivity(),
                    requireActivity().resources.getString(R.string.empty_input)
                )
            } else {
                try {
                    DBHandler(activity).use { dbHandler ->
                        dbHandler.readableDatabase.use { db ->
                            updateToolCmd(db, name!!, newCmd.text.toString().trim { it <= ' ' })
                            mainUtils.restartSpinner()
                            showCustomToast(
                                requireActivity(),
                                requireActivity().resources.getString(R.string.command_updated)
                            )
                            dialog?.cancel()
                        }
                    }
                } catch (e: SQLException) {
                    // Handle the exception here, for example:
                    showCustomToast(requireActivity(), "E: $e")
                }
            }
        }
        cancelButton.setOnClickListener {
            vibrate(mainActivity, 10)
            dialog?.cancel()
        }
        return view
    }
}
