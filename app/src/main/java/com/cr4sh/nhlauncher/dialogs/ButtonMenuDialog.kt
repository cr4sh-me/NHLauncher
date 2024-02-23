package com.cr4sh.nhlauncher.dialogs

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.DialogUtils
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.VibrationUtils

class ButtonMenuDialog(private var myActivity: MainActivity) : AppCompatDialogFragment() {
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.button_menu_dialog, container, false)
        val mainUtils = NHLUtils(myActivity)
        val nhlPreferences = NHLPreferences(myActivity)
        val bkg = view.findViewById<LinearLayout>(R.id.custom_theme_dialog_background)
        val title = view.findViewById<TextView>(R.id.dialog_title)
        val description = view.findViewById<TextView>(R.id.dialog_description)
        val category = view.findViewById<TextView>(R.id.dialog_category)
        val option1 = view.findViewById<Button>(R.id.option1)
        val option2 = view.findViewById<Button>(R.id.option2)
        val option3 = view.findViewById<Button>(R.id.option3)
        val option4 = view.findViewById<Button>(R.id.option4)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)
        val dialogUtils = DialogUtils(requireActivity().supportFragmentManager)

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
        title.setTextColor(Color.parseColor(nhlPreferences.color80()))
        description.setTextColor(Color.parseColor(nhlPreferences.color50()))
        category.setTextColor(Color.parseColor(nhlPreferences.color50()))

        ColorChanger.setButtonColors(option1)
        ColorChanger.setButtonColors(option2)
        ColorChanger.setButtonColors(option3)
        ColorChanger.setButtonColors(option4)
        ColorChanger.setButtonColors(cancelButton, true)

        title.text = myActivity.buttonName.toString().uppercase()
        category.text = "${resources.getString(R.string.category)} ${
            myActivity.buttonCategory.toString().uppercase()
        }"
        description.text = myActivity.buttonDescription.toString().uppercase()

        option1.setOnClickListener {
            VibrationUtils.vibrate()
            dialogUtils.openEditableDialog(myActivity.buttonName, myActivity.buttonCmd)
            dialog?.cancel()
        }
        option2.setOnClickListener {
            VibrationUtils.vibrate()
            mainUtils.addFavourite()
            dialog?.cancel()
        }
        option3.setOnClickListener {
            VibrationUtils.vibrate()
            if (!MainActivity.disableMenu) {
                dialogUtils.openNewToolDialog(myActivity.buttonCategory)
            } else {
                showCustomToast(requireActivity(), resources.getString(R.string.get_out))
            }
            dialog?.cancel()
        }
        option4.setOnClickListener {
            VibrationUtils.vibrate()
            dialogUtils.openDeleteToolDialog(myActivity.buttonName)
            dialog?.cancel()
        }
        cancelButton.setOnClickListener {
            VibrationUtils.vibrate()
            dialog?.cancel()
        }
        return view
    }
}
