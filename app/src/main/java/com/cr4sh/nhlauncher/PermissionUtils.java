package com.cr4sh.nhlauncher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    private final MainActivity myActivity;

    public PermissionUtils(MainActivity activity) {
        myActivity = activity;
    }

    public boolean isPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readExtStorage = ContextCompat.checkSelfPermission(myActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
            return readExtStorage == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void takePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Permission is not granted, request for it
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", myActivity.getPackageName(), null);
            intent.setData(uri);
            myActivity.requestPermissionLauncher.launch(intent);
        } else {
            ActivityCompat.requestPermissions(myActivity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);

        }
    }
}