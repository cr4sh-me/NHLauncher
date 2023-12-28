package com.cr4sh.nhlauncher.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;


public class NhPaths {
    private static final String TAG = "NhPaths";
    public static String APP;
    public static String APP_PATH;
    public static String APP_DATABASE_PATH;
    public static String APP_INITD_PATH;
    public static String APP_SCRIPTS_PATH;
    public static String APP_SCRIPTS_BIN_PATH;
    public static String NH_SD_FOLDER_NAME;
    public static String SD_PATH;
    public static String APP_SD_FILES_PATH;
    public static String NH_SYSTEM_PATH;
    public static String ARCH_FOLDER;
    public static String CHROOT_SD_PATH;
    public static String CHROOT_SUDO;
    public static String CHROOT_INITD_SCRIPT_PATH;
    public static String CHROOT_SYMLINK_PATH;
    public static String APP_SD_SQLBACKUP_PATH;
    public static String APP_SD_FILES_IMG_PATH;
    public static String BUSYBOX;
    public static String MAGISK_DB_PATH;
    public static int GPS_PORT;
    private static NhPaths instance;
    private static String BASE_PATH;
    private SharedPreferences sharedPreferences;

    private NhPaths() {
        APP = "com.offsec.nethunter";                // Static app name seems to be needed as some weirdness with getting app name is going on ( sometimes we get: androidx.multidex )
        APP_PATH = "/data/data/" + APP;                   // context.getApplicationContext().getFilesDir().getPath();
        APP_DATABASE_PATH = APP_PATH + "/databases";
        APP_INITD_PATH = APP_PATH + "/etc/init.d";
        APP_SCRIPTS_PATH = APP_PATH + "/scripts";
        APP_SCRIPTS_BIN_PATH = APP_SCRIPTS_PATH + "/bin";
        SD_PATH = getSdcardPath();
        NH_SD_FOLDER_NAME = "nh_files";
        APP_SD_FILES_PATH = SD_PATH + "/" + NH_SD_FOLDER_NAME;
        APP_SD_FILES_IMG_PATH = APP_SD_FILES_PATH + "/diskimage";
        APP_SD_SQLBACKUP_PATH = APP_SD_FILES_PATH + "/nh_sql_backups";
        BASE_PATH = "/data/local";
        NH_SYSTEM_PATH = BASE_PATH + "/nhsystem";
        CHROOT_SUDO = "/usr/bin/sudo";
        CHROOT_INITD_SCRIPT_PATH = APP_INITD_PATH + "/80postservices";
        CHROOT_SD_PATH = "/sdcard";
        CHROOT_SYMLINK_PATH = NH_SYSTEM_PATH + "/kalifs";
        BUSYBOX = getBusyboxPath();
        MAGISK_DB_PATH = "/data/adb/magisk.db";

        // This isn't really a path, but this is a convenient place to stick a config variable...
        GPS_PORT = 10110;
    }

    public static String CHROOT_PATH() {
        // TODO check
        return "/data/local/nhsystem" + "/" + "kali-arm64";
    }

    private static String getSdcardPath() {
        return Environment.getExternalStorageDirectory().toString();
    }

    public static String getBusyboxPath() {
        String[] BB_PATHS = {
                "/system/xbin/busybox_nh",
                "/system/bin/busybox_nh",
                APP_SCRIPTS_BIN_PATH + "/busybox_nh"
        };
        for (String BB_PATH : BB_PATHS) {
            File busybox = new File(BB_PATH);
            if (busybox.exists()) {
                return BB_PATH;
            }
        }
        return "";
    }

    public static String makeTermTitle(String title) {
        return "echo -ne \"\\033]0;" + title + "\\007\" && clear;";
    }

    public static void showMessage(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public static void showMessage_long(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
