package com.cr4sh.nhlanucher;


import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

// RecyclerView performance improvement
// This class compares items, and leaves these that are same
public class MyDiffUtilCallback extends DiffUtil.Callback {

    private final List<Item> oldList;
    private final List<Item> newList;

    public MyDiffUtilCallback(List<Item> oldList, List<Item> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getName().equals(newList.get(newItemPosition).getName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Item oldItem = oldList.get(oldItemPosition);
        final Item newItem = newList.get(newItemPosition);

        return oldItem.getName().equals(newItem.getName()) &&
                oldItem.getDescription().equals(newItem.getDescription()) &&
                Objects.equals(oldItem.getImage(), newItem.getImage()) &&
                oldItem.getCmd().equals(newItem.getCmd()) &&
                oldItem.getCategory().equals(newItem.getCategory());
    }

    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement this method if you want to use DiffUtil's list update animation.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
