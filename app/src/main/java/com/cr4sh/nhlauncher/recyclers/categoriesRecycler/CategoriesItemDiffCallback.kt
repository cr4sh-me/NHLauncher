package com.cr4sh.nhlauncher.recyclers.categoriesRecycler

import androidx.recyclerview.widget.DiffUtil

class CategoriesItemDiffCallback(
    private val oldList: MutableList<String>,
    private val newList: List<String>,
    private val oldList2: MutableList<Int>,
    private val newList2: List<Int>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Assuming both lists have the same size
        return oldList[oldItemPosition] == newList[newItemPosition] &&
                oldList2[oldItemPosition] == newList2[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Assuming both lists have the same size
        return oldList[oldItemPosition] == newList[newItemPosition] &&
                oldList2[oldItemPosition] == newList2[newItemPosition]
    }
}
