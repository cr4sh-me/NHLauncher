package com.cr4sh.nhlauncher.Database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.NHLManager;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.DialogUtils;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

// This class is backing up and restoring buttons
public class DBBackup {
    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();
    private final MainUtils mainUtils;
    private final DialogUtils dialogUtils;

    public DBBackup() {
        this.mainUtils = new MainUtils(mainActivity);
        this.dialogUtils = new DialogUtils(mainActivity.getSupportFragmentManager());
    }

    public void createBackup(Context context) {

        try {
            // Create a new instance of your DBHandler class
            DBHandler dbHelper = new DBHandler(context);

            // Get a readable instance of the database
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Get path to the database file
            String dbPath = db.getPath();

            // Check if external storage is available and writable
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                // Create a file object for the backup directory
                File backupDir = new File(Environment.getExternalStorageDirectory(), "NHLauncher");

                // Check if the directory exists and create it if it doesn't
                if (!backupDir.exists()) {
                    if (!backupDir.mkdirs()) {
                        // Show an error message if the directory couldn't be created
                        showToastOnMainThread(context.getResources().getString(R.string.err_backup_dir));
                        return;
                    }
                }

                // Create a file object for the backup file
                File backupFile = new File(backupDir, "backup.db");

                // Open the backup file for writing
                FileOutputStream out = new FileOutputStream(backupFile);

                // Open the database file for reading
                FileInputStream in = new FileInputStream(dbPath);

                // Create a byte buffer to hold the database contents
                byte[] buffer = new byte[1024];

                // Loop through the database file and write its contents to the backup file
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }

                // Close the streams and the database
                out.flush();
                out.close();
                in.close();
                db.close();

                // Show a message indicating the backup was successful
                showToastOnMainThread(context.getResources().getString(R.string.saved_to) + backupDir.getAbsolutePath());
            } else {
                // External storage not available, show an error message
                showToastOnMainThread(context.getResources().getString(R.string.ex_storage));
            }
        } catch (FileNotFoundException e) {
            showToastOnMainThread(context.getResources().getString(R.string.backup_failed));
            dialogUtils.openPermissionsDialog();
        } catch (Exception e) {
            showToastOnMainThread("E: " + e);
        }
    }

    @SuppressLint("Range")
    public void restoreBackup(Context context) {

        try {
            @SuppressLint("SdCardPath") File file = new File("/sdcard/NHLauncher/backup.db");

            if (!file.exists()) {
                showToastOnMainThread(context.getResources().getString(R.string.bf_not));
                return;
            }

            // Open the backup database
            @SuppressLint("SdCardPath") SQLiteDatabase backupDB = SQLiteDatabase.openDatabase("/sdcard/NHLauncher/backup.db", null, SQLiteDatabase.OPEN_READONLY);

            // Open the existing database
            DBHandler dbHelper = new DBHandler(context);
            SQLiteDatabase existingDB = dbHelper.getWritableDatabase();

            // Query the backup database for all tools
            Cursor backupCursor = backupDB.query("TOOLS", new String[]{"SYSTEM", "CATEGORY", "FAVOURITE", "NAME", "DESCRIPTION_EN", "DESCRIPTION_PL", "CMD", "ICON", "USAGE"}, null, null, null, null, null);

            // Iterate over each tool in the backup database
            while (backupCursor.moveToNext()) {
                // Get the values for this tool
                int system = backupCursor.getInt(0);
                String category = backupCursor.getString(1);
                int favourite = backupCursor.getInt(2);
                String name = backupCursor.getString(3);
                String description_en = backupCursor.getString(4);
                String description_pl = backupCursor.getString(5);
                String cmd = backupCursor.getString(6);
                String icon = backupCursor.getString(7);

                // Not checking FAVOURITE, CMD and USAGE, because these values can be changed and it can cause duplicated buttons!!!
                Cursor existingCursor = existingDB.query("TOOLS", null, "SYSTEM = ? AND CATEGORY = ? AND NAME = ? AND DESCRIPTION_EN = ? AND DESCRIPTION_PL = ? AND ICON = ?", new String[]{String.valueOf(system), category, name, description_en, description_pl, icon}, null, null, null);

                int existingUsage = 0;
                if (existingCursor.getCount() > 0) {
                    // Get the existing favorite value for the tool
                    existingCursor.moveToFirst();
                    int existingFavorite = existingCursor.getInt(existingCursor.getColumnIndex("FAVOURITE"));
                    existingUsage = existingCursor.getInt(existingCursor.getColumnIndex("USAGE"));
                    existingCursor.close();
                    // Only update the favorite value if it is different from the existing value
                    if (existingFavorite != 1) {
                        Log.d("CRS", name + " Updated\n");
                        DBHandler.updateTool(existingDB, system, category, favourite, name, description_en, description_pl, cmd, icon, existingUsage);
                    } else {
                        Log.d("CRS", name + " Not updated favourite (already has favorite status)\n");
                        DBHandler.updateTool(existingDB, system, category, 1, name, description_en, description_pl, cmd, icon, existingUsage);
                    }
                } else {
                    Log.d("CRS", name + "Inserted\n");
                    DBHandler.insertTool(existingDB, system, category, favourite, name, description_en, description_pl, cmd, icon, existingUsage);
                }

                existingCursor.close();
            }

            backupCursor.close();
            backupDB.close();
            existingDB.close();
            showToastOnMainThread(context.getResources().getString(R.string.bp_restored));
            mainActivity.runOnUiThread(mainUtils::restartSpinner);

        } catch (SQLiteCantOpenDatabaseException e) {
            showToastOnMainThread(context.getResources().getString(R.string.restore_fail));
            dialogUtils.openPermissionsDialog();
        } catch (Exception e) {
            showToastOnMainThread("E: " + e);
            Log.d("DBBACKUPERR", "E: " + e);
        }

    }
    private void showToastOnMainThread(String message) {
        // Using the main thread's handler to post a runnable
        mainActivity.runOnUiThread(() -> ToastUtils.showCustomToast(mainActivity, message));
    }
}
