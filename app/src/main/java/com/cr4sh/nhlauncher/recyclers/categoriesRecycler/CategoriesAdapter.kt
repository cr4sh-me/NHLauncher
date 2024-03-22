package com.cr4sh.nhlauncher.recyclers.categoriesRecycler

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.VibrationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesAdapter : RecyclerView.Adapter<CategoriesViewHolder>() {
    private val myActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    private val item: MutableList<String> = ArrayList()
    private val itemImg: MutableList<Int> = ArrayList()
    private var height = 0
    private var margin = 0
    private var drawable: GradientDrawable? = null
    private var nhlPreferences: NHLPreferences? = null

    fun updateData(newData: List<String>, newData2: List<Int>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffResult = withContext(Dispatchers.Default) {
                DiffUtil.calculateDiff(CategoriesItemDiffCallback(item, newData, itemImg, newData2))
            }

            myActivity?.lifecycleScope?.launch {
                item.clear()
                itemImg.clear()
                item.addAll(newData)
                itemImg.addAll(newData2)
                diffResult.dispatchUpdatesTo(this@CategoriesAdapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        nhlPreferences = myActivity?.let { NHLPreferences(it) }
        val originalHeight = parent.measuredHeight
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
        return CategoriesViewHolder(
            LayoutInflater.from(myActivity).inflate(R.layout.custom_category_item, parent, false)
        )
    }

    // Used to create buttons, and set listeners for them
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(
        holder: CategoriesViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val mainUtils = myActivity?.let { NHLUtils(it) }
        val categoryName = item[position]
        val categoryImage = itemImg[position].toString()
        holder.nameView.text = categoryName

        // Load image dynamically based on categoryImage
        @SuppressLint("DiscouragedApi") val imageResourceId =
            myActivity?.resources?.getIdentifier(categoryImage, "drawable", myActivity.packageName)
        if (imageResourceId != null) {
            if (myActivity != null) {
                Glide.with(myActivity)
                    .asBitmap()
                    .load(imageResourceId)
                    .into(holder.imageView)
            }
        }
        holder.imageView.setColorFilter(
            Color.parseColor(nhlPreferences!!.color80()),
            PorterDuff.Mode.MULTIPLY
        )
        holder.nameView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        holder.itemView.background = drawable
        val params = RecyclerView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height)
        params.setMargins(margin, margin / 2, margin, margin / 2)
        holder.categoryLayout.layoutParams = params

//        Log.d("CategoriesAdapter", "Parent height: " + originalHeight);
//        Log.d("CategoriesAdapter", "Button height with margin: " + (height + margin));
        holder.itemView.setOnClickListener {
            if (myActivity != null) {
                VibrationUtils.vibrate()
            }
            myActivity?.backButton?.callOnClick()
            myActivity?.lifecycleScope?.launch(Dispatchers.Default){
                mainUtils?.spinnerChanger(position, true)
            }
            myActivity?.currentCategoryNumber = position
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }
}
