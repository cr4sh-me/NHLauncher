package com.cr4sh.nhlauncher.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.util.Log
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.database.DBHandler.Companion.insertTool
import com.cr4sh.nhlauncher.database.DBHandler.Companion.updateTool
import com.cr4sh.nhlauncher.utils.DialogUtils
import com.cr4sh.nhlauncher.utils.MainUtils
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

// This class is backing up and restoring buttons
class DBBackup {
    private val mainActivity = NHLManager.getInstance().mainActivity
    private val mainUtils: MainUtils = MainUtils(mainActivity)
    private val dialogUtils: DialogUtils = DialogUtils(mainActivity.supportFragmentManager)

    fun createBackup(context: Context) {
        try {
            // Create a new instance of your DBHandler class
            val dbHelper = DBHandler(context)

            // Get a readable instance of the database
            val db = dbHelper.readableDatabase

            // Get path to the database file
            val dbPath = db.path

            // Check if external storage is available and writable
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                // Create a file object for the backup directory
                val backupDir = File(Environment.getExternalStorageDirectory(), "NHLauncher")

                // Check if the directory exists and create it if it doesn't
                if (!backupDir.exists()) {
                    if (!backupDir.mkdirs()) {
                        // Show an error message if the directory couldn't be created
                        showToastOnMainThread(context.resources.getString(R.string.err_backup_dir))
                        return
                    }
                }

                // Create a file object for the backup file
                val backupFile = File(backupDir, "backup.db")

                // Open the backup file for writing
                val out = FileOutputStream(backupFile)

                // Open the database file for reading
                val `in` = FileInputStream(dbPath)

                // Create a byte buffer to hold the database contents
                val buffer = ByteArray(1024)

                // Loop through the database file and write its contents to the backup file
                var len: Int
                while (`in`.read(buffer).also { len = it } > 0) {
                    out.write(buffer, 0, len)
                }

                // Close the streams and the database
                out.flush()
                out.close()
                `in`.close()
                db.close()

                // Show a message indicating the backup was successful
                showToastOnMainThread(context.resources.getString(R.string.saved_to) + backupDir.absolutePath)
            } else {
                // External storage not available, show an error message
                showToastOnMainThread(context.resources.getString(R.string.ex_storage))
            }
        } catch (e: FileNotFoundException) {
            showToastOnMainThread(context.resources.getString(R.string.backup_failed))
            mainActivity.runOnUiThread { dialogUtils.openPermissionsDialog() }
        } catch (e: Exception) {
            showToastOnMainThread("E: $e")
        }
    }

    @SuppressLint("Range")
    fun restoreBackup(context: Context) {
        try {
            @SuppressLint("SdCardPath") val file = File("/sdcard/NHLauncher/backup.db")
            if (!file.exists()) {
                showToastOnMainThread(context.resources.getString(R.string.bf_not))
                return
            }

            // Open the backup database
            @SuppressLint("SdCardPath") val backupDB = SQLiteDatabase.openDatabase(
                "/sdcard/NHLauncher/backup.db",
                null,
                SQLiteDatabase.OPEN_READONLY
            )

            // Open the existing database
            val dbHelper = DBHandler(context)
            val existingDB = dbHelper.writableDatabase

            // Query the backup database for all tools
            val backupCursor = backupDB.query(
                "TOOLS",
                arrayOf(
                    "SYSTEM",
                    "CATEGORY",
                    "FAVOURITE",
                    "NAME",
                    "DESCRIPTION_EN",
                    "DESCRIPTION_PL",
                    "CMD",
                    "ICON",
                    "USAGE"
                ),
                null,
                null,
                null,
                null,
                null
            )

            // Iterate over each tool in the backup database
            while (backupCursor.moveToNext()) {
                // Get the values for this tool
                val system = backupCursor.getInt(0)
                val category = backupCursor.getString(1)
                val favourite = backupCursor.getInt(2)
                val name = backupCursor.getString(3)
                val description_en = backupCursor.getString(4)
                val description_pl = backupCursor.getString(5)
                val cmd = backupCursor.getString(6)
                val icon = backupCursor.getString(7)

                // Not checking FAVOURITE, CMD and USAGE, because these values can be changed and it can cause duplicated buttons!!!
                val existingCursor = existingDB.query(
                    "TOOLS",
                    null,
                    "SYSTEM = ? AND CATEGORY = ? AND NAME = ? AND DESCRIPTION_EN = ? AND DESCRIPTION_PL = ? AND ICON = ?",
                    arrayOf(
                        system.toString(),
                        category,
                        name,
                        description_en,
                        description_pl,
                        icon
                    ),
                    null,
                    null,
                    null
                )
                var existingUsage = 0
                if (existingCursor.count > 0) {
                    // Get the existing favorite value for the tool
                    existingCursor.moveToFirst()
                    val existingFavorite =
                        existingCursor.getInt(existingCursor.getColumnIndex("FAVOURITE"))
                    existingUsage = existingCursor.getInt(existingCursor.getColumnIndex("USAGE"))
                    existingCursor.close()
                    // Only update the favorite value if it is different from the existing value
                    if (existingFavorite != 1) {
                        Log.d("CRS", "$name Updated\n")
                        updateTool(
                            existingDB,
                            system,
                            category,
                            favourite,
                            name,
                            description_en,
                            description_pl,
                            cmd,
                            icon,
                            existingUsage
                        )
                    } else {
                        Log.d("CRS", "$name Not updated favourite (already has favorite status)\n")
                        updateTool(
                            existingDB,
                            system,
                            category,
                            1,
                            name,
                            description_en,
                            description_pl,
                            cmd,
                            icon,
                            existingUsage
                        )
                    }
                } else {
                    Log.d("CRS", name + "Inserted\n")
                    insertTool(
                        existingDB,
                        system,
                        category,
                        favourite,
                        name,
                        description_en,
                        description_pl,
                        cmd,
                        icon,
                        existingUsage
                    )
                }
                existingCursor.close()
            }
            backupCursor.close()
            backupDB.close()
            existingDB.close()
            showToastOnMainThread(context.resources.getString(R.string.bp_restored))
            mainActivity.runOnUiThread { mainUtils.restartSpinner() }
        } catch (e: SQLiteCantOpenDatabaseException) {
            showToastOnMainThread(context.resources.getString(R.string.restore_fail))
            mainActivity.runOnUiThread { dialogUtils.openPermissionsDialog() }
        } catch (e: Exception) {
            showToastOnMainThread("E: $e")
            Log.d("DBBACKUPERR", "E: $e")
        }
    }

    private fun showToastOnMainThread(message: String) {
        // Using the main thread's handler to post a runnable
        mainActivity.runOnUiThread { showCustomToast(mainActivity, message) }
    }
}
