package com.cr4sh.nhlauncher.utils

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.cr4sh.nhlauncher.MainActivity
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.bridge.Bridge
import com.cr4sh.nhlauncher.buttonsRecycler.NHLAdapter
import com.cr4sh.nhlauncher.buttonsRecycler.NHLItem
import com.cr4sh.nhlauncher.database.DBHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainUtils(private val mainActivity: MainActivity) : AppCompatActivity() {
    private val mDatabase: SQLiteDatabase = mainActivity.mDatabase
    private val nhlPreferences: NHLPreferences = NHLPreferences(mainActivity)
    // NetHunter bridge function
    fun runCmd(cmd: String?) {
        @SuppressLint("SdCardPath") val intent =
            cmd?.let {
                Bridge.createExecuteIntent(
                    "/data/data/com.offsec.nhterm/files/usr/bin/kali", it
                )
            }
        //        intent.putExtra(EXTRA_FOREGOUND, true)
        mainActivity.startActivity(intent)
    }

    // Increase button usage by 1
    fun buttonUsageIncrease(name: String?) {
        if (name != null) {
            DBHandler.updateToolUsage(mDatabase, name, mainActivity.buttonUsage + 1)
        }
        restartSpinner()
    }

    // Queries db for buttons with given categories and display them!
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n", "Recycle")
    fun spinnerChanger(category: Int) {

        mainActivity.changeCategoryPreview(category) // Set category preview

        // Obtain references to app resources and button layout
        val resources = mainActivity.resources
        val layout = mainActivity.recyclerView
        val noToolsText = mainActivity.findViewById<TextView>(R.id.messagebox)
        noToolsText.text = null
        val projection = arrayOf(
            "CATEGORY",
            "FAVOURITE",
            "NAME",
            nhlPreferences.language(),
            "CMD",
            "ICON",
            "USAGE"
        )
        val selection: String
        val selectionArgs: Array<String>
        if (category == 0) {
            selection = "FAVOURITE = ?"
            selectionArgs = arrayOf("1")
            // Disable creating new buttons in fav category
            MainActivity.disableMenu = true
        } else {
            val categoryNumber = category.toString()
            selection = "CATEGORY = ?"
            selectionArgs = arrayOf(categoryNumber)
            // Enable creating new buttons in normal categories
            MainActivity.disableMenu = false
        }

        layout.itemAnimator = DefaultItemAnimator()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val newItemList = withContext(Dispatchers.Default) {
                    val cursor = mDatabase.query(
                        "TOOLS",
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        nhlPreferences.sortingMode(),
                        null
                    )
                    val itemList: MutableList<NHLItem> = ArrayList()
                    if (cursor.count > 0) {
                        // Create a new itemList from the cursor data
                        while (cursor.moveToNext()) {
                            val toolCategory = cursor.getString(0)
                            val toolName = cursor.getString(2)
                            val toolDescription = cursor.getString(3)
                            val toolCmd = cursor.getString(4)
                            val toolIcon = cursor.getString(5)
                            val toolUsage = cursor.getInt(6)

                            val item = NHLItem(
                                toolCategory,
                                toolName,
                                toolDescription,
                                toolCmd,
                                toolIcon,
                                toolUsage
                            )
                            itemList.add(item)
                        }
                    }
                    cursor.close()
                    itemList
                }

                if (newItemList.isEmpty()) {
                    val adapter = layout.adapter
                    if (adapter is NHLAdapter) {
                        // Call the suspend function within a coroutine
                        adapter.updateData(ArrayList()) // Empty list to clear existing data
                    }
                    mainActivity.enableAfterAnimation(noToolsText)
                    noToolsText.text = resources.getString(R.string.no_fav_tools)
                } else {
                    noToolsText.text = null
                    mainActivity.disableWhileAnimation(noToolsText)
                    val adapter = layout.adapter
                    if (adapter is NHLAdapter) {
                        // Call the suspend function within a coroutine
                        adapter.updateData(newItemList)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    // Fills our spinner with text and images
    fun restartSpinner() {
        spinnerChanger(mainActivity.currentCategoryNumber) // Just set to category chosen before
    }

    // Refreshes our TextView that is responsible for app background
    // Adds button to favourites bu updating FAVOURITE value
    fun addFavourite() {
        val cursor = mDatabase.query(
            "TOOLS", arrayOf("FAVOURITE", "NAME"), "NAME = ?", arrayOf(
                mainActivity.buttonName
            ), null, null, null, null
        )
        // Start iteration!
        var isFavourite: String? = null
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            isFavourite = cursor.getString(0)
            cursor.moveToNext()
        }
        assert(isFavourite != null)
        if (isFavourite == "1") {
            ToastUtils.showCustomToast(
                mainActivity,
                mainActivity.resources.getString(R.string.removed_favourite)
            )
            mainActivity.buttonName?.let { DBHandler.updateToolFavorite(mDatabase, it, 0) }
        } else {
            ToastUtils.showCustomToast(
                mainActivity,
                mainActivity.resources.getString(R.string.added_favourite)
            )
            mainActivity.buttonName?.let { DBHandler.updateToolFavorite(mDatabase, it, 1) }
        }
        restartSpinner()

        // Close cursor
        cursor.close()
    }

    // Changes app language
    fun changeLanguage(languageCode: String?) {
        val resources = mainActivity.resources
        val configuration = resources.configuration
        val newLocale = languageCode?.let { Locale(it) }
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}
