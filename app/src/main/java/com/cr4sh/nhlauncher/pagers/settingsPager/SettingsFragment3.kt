package com.cr4sh.nhlauncher.pagers.settingsPager

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.recyclers.categoriesRecycler.statsRecycler.StatsItem
import com.cr4sh.nhlauncher.recyclers.statsRecycler.StatsAdapter
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsFragment3 : Fragment() {
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    private var mDatabase: SQLiteDatabase? = null
    var adapter: StatsAdapter? = null
    private var nhlPreferences: NHLPreferences? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var noToolsText: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_layout3, container, false)
        nhlPreferences = NHLPreferences(requireActivity())
        mDatabase = mainActivity?.mDatabase
        val spinnerBg1 = view.findViewById<LinearLayout>(R.id.spinnerBg1)
        val title = view.findViewById<TextView>(R.id.bt_info2)
        val powerSpinnerView = view.findViewById<PowerSpinnerView>(R.id.categories_spinner)
        noToolsText = view.findViewById(R.id.no_tools_text)
        recyclerView = view.findViewById(R.id.stats_recycler)
        adapter = StatsAdapter()
        recyclerView.adapter = adapter
        noToolsText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        ColorChanger.setPowerSpinnerColor(powerSpinnerView)
        ColorChanger.setContainerBackground(spinnerBg1, true)

        title.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        powerSpinnerView.selectItemByIndex(0)
        spinnerChanger(0) // Display all tools by default
        powerSpinnerView.setOnSpinnerItemSelectedListener(
            OnSpinnerItemSelectedListener { _: Int, _: String?, newIndex: Int, _: String? ->
                spinnerChanger(
                    newIndex
                )
            })

        return view
    }

    private fun spinnerChanger(category: Int) {
        // Use lifecycleScope to launch a coroutine
        mainActivity?.lifecycleScope?.launch(Dispatchers.IO) {
            val cursor: Cursor
            val projection = arrayOf("CATEGORY", "FAVOURITE", "NAME", "ICON", "USAGE")
            val selection: String
            val selectionArgs: Array<String>

            when (category) {
                0 -> {
                    selection = "USAGE > ?"
                    selectionArgs = arrayOf("0")
                }

                1 -> {
                    selection = "FAVOURITE = ? AND USAGE > ?"
                    selectionArgs = arrayOf("1", "0")
                }

                else -> {
                    selection = "CATEGORY = ? AND USAGE > ?"
                    selectionArgs = arrayOf((category - 1).toString(), "0")
                }
            }

            cursor = mDatabase!!.query(
                "TOOLS",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                "USAGE DESC",
                null
            )

            if (cursor.count == 0) {
                mainActivity.lifecycleScope.launch {
                    noToolsText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            } else {
                mainActivity.lifecycleScope.launch {
                    noToolsText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                // Create a new itemList from the cursor data
                val newItemList: MutableList<StatsItem> = ArrayList()
                while (cursor.moveToNext()) {
                    val toolName = cursor.getString(2)
                    val toolIcon = cursor.getString(3)
                    val toolUsage = cursor.getInt(4)
                    val toolUsageString = toolUsage.toString()
                    val item = StatsItem(toolName, toolIcon, toolUsageString)
                    newItemList.add(item)
                }

                mainActivity.lifecycleScope.launch {
                    adapter!!.updateData(newItemList)
                }
            }

            cursor.close()
        }
    }

}
