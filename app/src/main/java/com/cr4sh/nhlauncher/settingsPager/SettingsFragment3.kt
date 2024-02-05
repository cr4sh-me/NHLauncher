package com.cr4sh.nhlauncher.settingsPager

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.statsRecycler.StatsAdapter
import com.cr4sh.nhlauncher.statsRecycler.StatsItem
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener
import com.skydoves.powerspinner.PowerSpinnerView
import java.util.concurrent.ExecutionException

class SettingsFragment3 : Fragment() {
    private val mainActivity = NHLManager.getInstance().mainActivity
    private val executor = NHLManager.getInstance().executorService
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
        mDatabase = mainActivity.mDatabase
        val spinnerBg1 = view.findViewById<LinearLayout>(R.id.spinnerBg1)
        val title = view.findViewById<TextView>(R.id.bt_info2)
        val powerSpinnerView = view.findViewById<PowerSpinnerView>(R.id.categories_spinner)
        noToolsText = view.findViewById(R.id.no_tools_text)
        recyclerView = view.findViewById(R.id.stats_recycler)
        adapter = StatsAdapter()
        recyclerView.adapter = adapter
        noToolsText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        powerSpinnerView.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        powerSpinnerView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        powerSpinnerView.setHintTextColor(Color.parseColor(nhlPreferences!!.color50()))
        powerSpinnerView.dividerColor = Color.parseColor(nhlPreferences!!.color80())
        title.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        powerSpinnerView.selectItemByIndex(0)
        spinnerChanger(0) // Display all tools by default
        powerSpinnerView.setOnSpinnerItemSelectedListener(
            OnSpinnerItemSelectedListener { _: Int, _: String?, newIndex: Int, _: String? ->
                spinnerChanger(
                    newIndex
                )
            })
        val gd = GradientDrawable()
        gd.setStroke(8, Color.parseColor(nhlPreferences!!.color50())) // Stroke width and color
        gd.cornerRadius = 20f
        spinnerBg1.background = gd
        powerSpinnerView.spinnerOutsideTouchListener =
            OnSpinnerOutsideTouchListener { _: View?, _: MotionEvent? ->
                powerSpinnerView.selectItemByIndex(powerSpinnerView.selectedIndex)
            }
        return view
    }

    @SuppressLint("SetTextI18n", "Recycle")
    fun spinnerChanger(category: Int) {
        val future = executor.submit {
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
                nhlPreferences!!.sortingMode(),
                null
            )
            if (cursor.count == 0) {
                mainActivity.runOnUiThread {
                    noToolsText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            } else {
                mainActivity.runOnUiThread {
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
                mainActivity.runOnUiThread { adapter!!.updateData(newItemList) }
            }
            cursor.close()
        }
        try {
            future.get() // This will wait for the background task to complete
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }
}
