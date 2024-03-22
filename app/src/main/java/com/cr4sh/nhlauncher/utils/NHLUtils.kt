package com.cr4sh.nhlauncher.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.bridge.Bridge
import com.cr4sh.nhlauncher.database.DBHandler
import com.cr4sh.nhlauncher.recyclers.buttonsRecycler.NHLAdapter
import com.cr4sh.nhlauncher.recyclers.categoriesRecycler.buttonsRecycler.NHLItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NHLUtils(
    private val mainActivity: MainActivity
) : AppCompatActivity() {
    private val mDatabase: SQLiteDatabase = mainActivity.mDatabase
    private val nhlPreferences: NHLPreferences = NHLPreferences(mainActivity)
    private val cache: MutableMap<Int, List<NHLItem>> = HashMap()


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
            lifecycleScope.launch(Dispatchers.IO) {
                DBHandler.updateToolUsage(mDatabase, name, mainActivity.buttonUsage + 1)
            }
        }
        restartSpinner()
    }

    // Queries db for buttons with given categories and display them!
// Function to refresh cache with the latest data from the database
    fun refreshCache(category: Int) {
        // Fetch data from the database
        val newData = queryDatabase(category)
        // Update cache with fetched data
        cache[category] = newData
    }

    suspend fun spinnerChanger(category: Int, scroll: Boolean=false) {
        // UI operations before starting coroutine
        nhlPreferences.language()?.let { Log.d("Yesd", it) }
        nhlPreferences.languageLocale().let { Log.d("Yesd", it) }
        mainActivity.changeCategoryPreview(category) // Set category preview

        MainActivity.disableMenu = false

        try {
            val newItemList = withContext(Dispatchers.Default) {
                // Fetch data from cache or database
                val itemList = getDataFromCacheOrDatabase(category)
                itemList
            }

            withContext(Dispatchers.Main) {
                // UI updates based on cached or fetched data
                updateUI(newItemList, scroll)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle exception appropriately, e.g., display error message
        }
    }

    // Function to fetch data from cache or database
    private fun getDataFromCacheOrDatabase(category: Int): List<NHLItem> {
        return if (cache.containsKey(category)) {
            cache[category] ?: emptyList()
        } else {
            // Data is not cached, fetch from the database
            val newData = queryDatabase(category)
            // Update cache with fetched data
            cache[category] = newData
            newData
        }
    }

    // Function to fetch data from the database
    private fun queryDatabase(category: Int): List<NHLItem> {
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
        } else {
            val categoryNumber = category.toString()
            selection = "CATEGORY = ?"
            selectionArgs = arrayOf(categoryNumber)
        }

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
        cursor?.use { x ->
            if (x.count > 0) {
                while (x.moveToNext()) {
                    val toolCategory = x.getString(0)
                    val toolName = x.getString(2)
                    val toolDescription = x.getString(3)
                    val toolCmd = x.getString(4)
                    val toolIcon = x.getString(5)
                    val toolUsage = x.getInt(6)

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
        }

        return itemList
    }

    // Function to update UI based on the fetched or cached data
    private suspend fun updateUI(newItemList: List<NHLItem>, scroll: Boolean) {
        val resources = mainActivity.resources
        val layout = mainActivity.recyclerView
        val noToolsText = mainActivity.findViewById<TextView>(R.id.messagebox)

        if (newItemList.isEmpty()) {
            val adapter = layout.adapter
            if (adapter is NHLAdapter) {
                adapter.updateData(ArrayList()) // Empty list to clear existing data
            }
            mainActivity.enableAfterAnimation(noToolsText)
            noToolsText.text = resources.getString(R.string.no_fav_tools)
        } else {
            noToolsText.text = null
            mainActivity.disableWhileAnimation(noToolsText)
            val adapter = layout.adapter
            if (adapter is NHLAdapter) {
                adapter.updateData(newItemList)
            }
        }
        if(scroll){
            delay(1)
            layout.scrollToPosition(0)
        }
    }




    // Fills our spinner with text and images
    fun restartSpinner() {
        lifecycleScope.launch(Dispatchers.Default){
            spinnerChanger(mainActivity.currentCategoryNumber) // Just set to category chosen before
        }
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
        cursor?.moveToFirst()
        if (cursor != null) {
            while (!cursor.isAfterLast) {
                isFavourite = cursor.getString(0)
                cursor.moveToNext()
            }
        }
        if (isFavourite == "1") {
            ToastUtils.showCustomToast(
                mainActivity,
                mainActivity.resources.getString(R.string.removed_favourite)
            )
            lifecycleScope.launch(Dispatchers.IO) {
                mainActivity.buttonName?.let {
                    DBHandler.updateToolFavorite(mDatabase, it, 0)
                }
            }
        } else {
            ToastUtils.showCustomToast(
                mainActivity,
                mainActivity.resources.getString(R.string.added_favourite)
            )
            lifecycleScope.launch(Dispatchers.IO) {
                mainActivity.buttonName?.let {
                    DBHandler.updateToolFavorite(mDatabase, it, 1)
                }
            }
        }

        if (!MainActivity.disableMenu) {
            lifecycleScope.launch {
                restartSpinner()
            }
        }

        // Close cursor
        cursor?.close()
    }

    fun copyToClipboard(text: CharSequence) {
        val clipboard = mainActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied!", text)
        clipboard.setPrimaryClip(clip)
    }
}
