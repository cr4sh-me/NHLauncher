package com.cr4sh.nhlauncher.utils

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.activities.wpsAttacks.WPSAttack
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
import com.cr4sh.nhlauncher.dialogs.SdpToolDialog
import com.cr4sh.nhlauncher.dialogs.ThrottlingDialog
import com.cr4sh.nhlauncher.dialogs.WpsCustomPinDialog
import com.cr4sh.nhlauncher.pagers.bluetoothPager.BluetoothFragment1
import kotlinx.coroutines.launch

// This class creates and opens Dialogs
class DialogUtils(private val fragmentManager: FragmentManager) {
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    private val lifecycleScope = mainActivity?.lifecycleScope
    fun openEditableDialog(name: String?, cmd: String?) {
        lifecycleScope?.launch {
            val editableDialog = EditableDialog()
            val args = Bundle()
            args.putString("name", name)
            args.putString("cmd", cmd)
            editableDialog.arguments = args
            editableDialog.show(fragmentManager, "EditableDialog")
        }
    }

    fun openNewToolDialog(category: String?) {
        lifecycleScope?.launch {
            val ntDialog = NewToolDialog()
            val args = Bundle()
            args.putString("category", category)
            ntDialog.arguments = args
            ntDialog.show(fragmentManager, "NewToolDialog")
        }
    }

    fun openDeleteToolDialog(name: String?) {
        lifecycleScope?.launch {
            val dtDialog = DeleteToolDialog()
            val args = Bundle()
            args.putString("name", name)
            dtDialog.arguments = args
            dtDialog.show(fragmentManager, "DeleteToolDialog")
        }
    }

    fun openAppsDialog() {
        lifecycleScope?.launch {
            val apDialog = AppsDialog()
            apDialog.show(fragmentManager, "AppsDialog")
        }
    }

    fun openRootDialog() {
        lifecycleScope?.launch {
            val rootDialog = RootDialog()
            rootDialog.show(fragmentManager, "RootDialog")
        }
    }

    fun openFirstSetupDialog() {
        lifecycleScope?.launch {
            val fsDialog = FirstSetupDialog()
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
        lifecycleScope?.launch {
            val pmDialog = PermissionDialog()
            pmDialog.show(fragmentManager, "PermissionsDialog")
        }
    }

    fun openMissingActivityDialog() {
        lifecycleScope?.launch {
            val maDialog = MissingActivityDialog()
            maDialog.show(fragmentManager, "MissingActivityDialog")
        }
    }

    fun openButtonMenuDialog(myActivity: MainActivity?) {
        lifecycleScope?.launch {
            myActivity?.let { ButtonMenuDialog(it) }?.show(fragmentManager, "ButtonMenuDialog")
        }
    }

    fun openNhlColorPickerDialog(button: Button, alpha: ImageView, colorShade: String?) {
        lifecycleScope?.launch {
            colorShade?.let { NhlColorPickerDialog(button, alpha, it) }
                ?.show(fragmentManager, "NhlColorPickerDialog")
        }
    }

    fun openThrottlingDialog() {
        lifecycleScope?.launch {
            val thDialog = ThrottlingDialog()
            thDialog.show(fragmentManager, "PermissionsDialog")
        }
    }

    fun openWpsCustomSetting(number: Int, activity: WPSAttack?) {
        lifecycleScope?.launch {
            val pinDialog = activity?.let { WpsCustomPinDialog(it) }
            val args = Bundle()
            args.putInt("option", number)
            pinDialog?.arguments = args
            pinDialog?.show(fragmentManager, "PermissionsDialog")
        }
    }

    fun openScanTimeDialog(number: Int, activity: BluetoothFragment1?) {
        lifecycleScope?.launch {
            val pinDialog = activity?.let { ScanTimeDialog(it) }
            val args = Bundle()
            args.putInt("option", number)
            pinDialog?.arguments = args
            pinDialog?.show(fragmentManager, "PermissionsDialog")
        }
    }

    fun openSdpToolDialog() {
        lifecycleScope?.launch {
            val sdpDialog = SdpToolDialog()
            sdpDialog.show(fragmentManager, "SdpToolDialog")
        }
    }

}