package com.cr4sh.nhlauncher.recyclers.buttonsRecycler

import androidx.recyclerview.widget.DiffUtil
import com.cr4sh.nhlauncher.recyclers.categoriesRecycler.buttonsRecycler.NHLItem

class NHLItemDiffCallback(
    private val oldList: MutableList<NHLItem>,
    private val newList: List<NHLItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem == newItem
    }
}
