package com.cr4sh.nhlauncher.utils

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cr4sh.nhlauncher.bluetoothPager.BluetoothFragment1
import com.cr4sh.nhlauncher.MainActivity
import com.cr4sh.nhlauncher.wpsAttacks.WPSAttack
import com.cr4sh.nhlauncher.dialogs.AppsDialog
import com.cr4sh.nhlauncher.dialogs.ButtonMenuDialog
import com.cr4sh.nhlauncher.dialogs.DeleteToolDialog
import com.cr4sh.nhlauncher.dialogs.EditableDialog
import com.cr4sh.nhlauncher.dialogs.FirstSetupDialog
import com.cr4sh.nhlauncher.dialogs.MissingActivityDialog
import com.cr4sh.nhlauncher.dialogs.NewToolDialog
import com.cr4sh.nhlauncher.dialogs.NhlColorPickerDialog
import com.cr4sh.nhlauncher.dialogs.PermissionDialog
import com.cr4sh.nhlauncher.dialogs.RootDialog
import com.cr4sh.nhlauncher.dialogs.ScanTimeDialog
import com.cr4sh.nhlauncher.dialogs.ThrottlingDialog
import com.cr4sh.nhlauncher.dialogs.WpsCustomPinDialog

// This class creates and opens Dialogs
class DialogUtils(private val fragmentManager: FragmentManager) {
    //    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();
    private val executor = NHLManager.getInstance().executorService
    fun openEditableDialog(name: String?, cmd: String?) {
        executor.execute {
            val editableDialog = EditableDialog()
            // Input as an argument.
            val args = Bundle()
            args.putString("name", name)
            args.putString("cmd", cmd)
            editableDialog.arguments = args
            // Display our dialog!
            editableDialog.show(fragmentManager, "EditableDialog")
        }
    }

    fun openNewToolDialog(category: String?) {
        executor.execute {
            val ntDialog = NewToolDialog()
            // ARGS!
            val args = Bundle()
            args.putString("category", category)
            ntDialog.arguments = args
            // Display our dialog!
            ntDialog.show(fragmentManager, "NewToolDialog")
        }
    }

    fun openDeleteToolDialog(name: String?) {
        executor.execute {
            val dtDialog = DeleteToolDialog()
            // ARGS!
            val args = Bundle()
            args.putString("name", name)
            dtDialog.arguments = args
            // Display our dialog!
            dtDialog.show(fragmentManager, "DeleteToolDialog")
        }
    }

    fun openAppsDialog() {
        executor.execute {
            val apDialog = AppsDialog()
            // Display our dialog!
            apDialog.show(fragmentManager, "AppsDialog")
        }
    }

    fun openRootDialog() {
        executor.execute {
            val rootDialog = RootDialog()
            // Display our dialog!
            rootDialog.show(fragmentManager, "RootDialog")
        }
    }

    fun openFirstSetupDialog() {
        executor.execute {
            val fsDialog = FirstSetupDialog()
            // Display our dialog!
            fsDialog.show(fragmentManager, "FirstSetupDialog")
        }
    }

    fun openPermissionsDialog() {
        // Assuming "fragmentManager" is the support fragment manager from an Activity or Fragment
        fragmentManager.executePendingTransactions()
        if (fragmentManager.isStateSaved) {
            // Handle the case where state is already saved.
            return
        }
        fragmentManager.beginTransaction().add(Fragment(), "TempFragment").commitNow()
        fragmentManager.executePendingTransactions()
        executor.execute {
            val pmDialog = PermissionDialog()
            // Display our dialog!
            pmDialog.show(fragmentManager, "PermissionsDialog")
        }
    }

    fun openMissingActivityDialog() {
        executor.execute {
            val maDialog = MissingActivityDialog()
            // Display our dialog!
            maDialog.show(fragmentManager, "MissingActivityDialog")
        }
    }

    fun openButtonMenuDialog(myActivity: MainActivity?) {
        executor.execute {
            // ARGS!
//            Bundle args = new Bundle();
//            args.putString("category", category);
//            bmDialog.setArguments(args);
            // Display our dialog!
            myActivity?.let { ButtonMenuDialog(it) }?.show(fragmentManager, "ButtonMenuDialog")
        }
    }

    fun openNhlColorPickerDialog(button: Button, alpha: ImageView, colorShade: String?) {
        executor.execute {
            // Display our dialog!
            colorShade?.let { NhlColorPickerDialog(button, alpha, it) }
                ?.show(fragmentManager, "NhlColorPickerDialog")
        }
    }

    fun openThrottlingDialog() {
        executor.execute {
            val thDialog = ThrottlingDialog()
            // Display our dialog!
            thDialog.show(fragmentManager, "PermissionsDialog")
        }
    }

    fun openWpsCustomSetting(number: Int, activity: WPSAttack?) {
        executor.execute {
            val pinDialog = activity?.let { WpsCustomPinDialog(it) }
            val args = Bundle()
            args.putInt("option", number)
            if (pinDialog != null) {
                pinDialog.arguments = args
            }
            // Display our dialog!
            pinDialog?.show(fragmentManager, "PermissionsDialog")
        }
    }

    fun openScanTimeDialog(number: Int, activity: BluetoothFragment1?) {
        executor.execute {
            val pinDialog = activity?.let { ScanTimeDialog(it) }
            val args = Bundle()
            args.putInt("option", number)
            if (pinDialog != null) {
                pinDialog.arguments = args
            }
            // Display our dialog!
            pinDialog?.show(fragmentManager, "PermissionsDialog")
        }
    }
}