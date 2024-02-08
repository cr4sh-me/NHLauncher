package com.cr4sh.nhlauncher.recyclers.buttonsRecycler

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.recyclers.categoriesRecycler.buttonsRecycler.NHLItem
import com.cr4sh.nhlauncher.utils.DialogUtils
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class NHLAdapter(private val editText: EditText) : RecyclerView.Adapter<NHLViewHolder>() {
    val myActivity: MainActivity = NHLManager.instance.mainActivity
    private val items: MutableList<NHLItem> = ArrayList()

    //    private val executor = NHLManager.getInstance().executorService
    private var height = 0
    private var margin = 0
    private var drawable: GradientDrawable? = null
    private var nhlPreferences: NHLPreferences? = null
    private var overlay = false

    fun updateData(newData: List<NHLItem>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffResult = withContext(Dispatchers.Default) {
                DiffUtil.calculateDiff(NHLItemDiffCallback(items, newData))
            }

            withContext(Dispatchers.Main) {
                items.clear()
                items.addAll(newData)
                diffResult.dispatchUpdatesTo(this@NHLAdapter)
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun startPapysz() {
        // Replace all item images with a papysz
        for (item in items) {
            item.image = "papysz2"
        }
        notifyDataSetChanged()
    }

    private fun saveRecyclerHeight(active: Int) {
        val prefs = myActivity.getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("recyclerHeight", active)
        editor.apply()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NHLViewHolder {
        nhlPreferences = NHLPreferences(myActivity)
        val originalHeight: Int
        if (nhlPreferences!!.recyclerMainHeight == 0) {
            originalHeight = parent.measuredHeight
            saveRecyclerHeight(originalHeight)
        } else {
            originalHeight = nhlPreferences!!.recyclerMainHeight
        }
        overlay = nhlPreferences!!.isButtonOverlayActive
        margin = 20
        height = originalHeight / 8 - margin // Button height without margin
        drawable = GradientDrawable()
        if (nhlPreferences!!.isNewButtonStyleActive) {
            drawable!!.setColor(Color.parseColor(nhlPreferences!!.color50()))
            drawable!!.cornerRadius = 60f
        } else {
            drawable!!.cornerRadius = 60f
            drawable!!.setStroke(8, Color.parseColor(nhlPreferences!!.color80()))
        }
        drawable!!.setBounds(0, 0, 0, height) // Set bounds for the drawable
        return NHLViewHolder(
            LayoutInflater.from(myActivity).inflate(R.layout.custom_button, parent, false)
        )
    }

    // Used to create buttons, and set listeners for them
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(
        holder: NHLViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val mainUtils = NHLUtils(myActivity)
        val dialogUtils = DialogUtils(myActivity.supportFragmentManager)
        val item = getItem(position)
        val searchQuery = editText.text.toString().uppercase(Locale.getDefault())
        if (searchQuery.isNotEmpty()) {
            // Highlight the search query in the button text for 'nameView'
            val buttonTextName = item.name.uppercase(Locale.getDefault())
            val builderName = SpannableStringBuilder(buttonTextName)
            val startIndexName = buttonTextName.indexOf(searchQuery)
            if (startIndexName != -1 && startIndexName + searchQuery.length <= buttonTextName.length) {
                if (nhlPreferences!!.isNewButtonStyleActive) {
                    builderName.setSpan(
                        BackgroundColorSpan(
                            Color.parseColor(
                                nhlPreferences!!.color20()
                            )
                        ),
                        startIndexName,
                        startIndexName + searchQuery.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    builderName.setSpan(
                        BackgroundColorSpan(
                            Color.parseColor(
                                nhlPreferences!!.color50()
                            )
                        ),
                        startIndexName,
                        startIndexName + searchQuery.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            holder.nameView.text = builderName

            // Highlight the search query in the button text for 'descriptionView'
            val buttonTextDescription = item.description.uppercase(Locale.getDefault())
            val builderDescription = SpannableStringBuilder(buttonTextDescription)
            val startIndexDescription = buttonTextDescription.indexOf(searchQuery)
            if (startIndexDescription != -1 && startIndexName + searchQuery.length <= buttonTextDescription.length) {
                if (nhlPreferences!!.isNewButtonStyleActive) {
                    builderDescription.setSpan(
                        BackgroundColorSpan(
                            Color.parseColor(
                                nhlPreferences!!.color20()
                            )
                        ),
                        startIndexDescription,
                        startIndexDescription + searchQuery.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    builderDescription.setSpan(
                        BackgroundColorSpan(
                            Color.parseColor(
                                nhlPreferences!!.color50()
                            )
                        ),
                        startIndexDescription,
                        startIndexDescription + searchQuery.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            holder.descriptionView.text = builderDescription
        } else {
            // If there is no search query, set the button text without highlighting for 'nameView'
            holder.nameView.text = item.name.uppercase(Locale.getDefault())

            // If there is no search query, set the button text without highlighting for 'descriptionView'
            holder.descriptionView.text = item.description.uppercase(Locale.getDefault())
        }
        @SuppressLint("DiscouragedApi") val imageResourceId =
            myActivity.resources.getIdentifier(item.image, "drawable", myActivity.packageName)
        holder.imageView.setImageResource(imageResourceId)
        if (overlay) {
            holder.imageView.setColorFilter(
                Color.parseColor(nhlPreferences!!.color80()),
                PorterDuff.Mode.MULTIPLY
            )
        }
        holder.nameView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        holder.descriptionView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        holder.itemView.background = drawable
        val params = RecyclerView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height)
        params.setMargins(margin, margin / 2, margin, margin / 2)
        holder.buttonView.layoutParams = params
        holder.itemView.setOnClickListener {
            if (editText.text.toString().isNotEmpty()) {
                myActivity.onBackPressedDispatcher.onBackPressed() // close searchbar
            }
            myActivity.buttonUsage = item.usage
            mainUtils.buttonUsageIncrease(item.name)
            myActivity.lifecycleScope.launch {
                mainUtils.runCmd(item.cmd)
            }
        }
        holder.itemView.setOnLongClickListener {
            vibrate(myActivity, 10)
            myActivity.buttonCategory = item.category
            myActivity.buttonName = item.name
            myActivity.buttonDescription = item.description
            myActivity.buttonCmd = item.cmd
            dialogUtils.openButtonMenuDialog(myActivity)
            false
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): NHLItem {
        return items[position]
    }
}
