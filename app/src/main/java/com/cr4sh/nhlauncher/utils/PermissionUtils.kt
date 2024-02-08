package com.cr4sh.nhlauncher.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cr4sh.nhlauncher.activities.MainActivity

class PermissionUtils(private val myActivity: MainActivity) {
    val isRoot: Boolean
        get() {
            val exe = ShellExecuter()
            return exe.Executer("su -c 'id'").isNotEmpty()
        }
    val isPermissionsGranted: Boolean
        // Check is permissions for files granted
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readExtStorage = ContextCompat.checkSelfPermission(
                myActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            readExtStorage == PackageManager.PERMISSION_GRANTED
        }

    // Request permissions for files
    fun takePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Permission is not granted, request for it
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", myActivity.packageName, null)
            intent.setData(uri)
            myActivity.requestPermissionLauncher.launch(intent)
        } else {
            ActivityCompat.requestPermissions(
                myActivity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                101
            )
        }
    }
}