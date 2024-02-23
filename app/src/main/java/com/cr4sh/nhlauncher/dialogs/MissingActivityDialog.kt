package com.cr4sh.nhlauncher.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.cr4sh.nhlauncher.R

class MissingActivityDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceStateExample: Bundle?): Dialog {

        // Set title and message
        val builder = AlertDialog.Builder(requireActivity())
        isCancelable = false
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setMessage(resources.getString(R.string.missing_activity_error))
        builder.setPositiveButton("Ok") { dialog: DialogInterface, _: Int ->
            dialog.cancel()
            requireActivity().finish()
        }
        return builder.create()
    }
}