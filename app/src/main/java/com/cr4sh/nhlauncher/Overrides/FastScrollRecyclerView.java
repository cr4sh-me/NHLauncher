package com.cr4sh.nhlauncher.Overrides;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class FastScrollRecyclerView extends RecyclerView {

    public FastScrollRecyclerView(Context context) {
        super(context);
        init();
    }

    public FastScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FastScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Attach custom SnapHelper on init
        NHLSnapHelper snapHelper = new NHLSnapHelper();
        snapHelper.attachToRecyclerView(this);
    }
}
