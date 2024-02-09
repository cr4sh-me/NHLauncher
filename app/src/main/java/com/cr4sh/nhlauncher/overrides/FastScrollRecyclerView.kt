package com.cr4sh.nhlauncher.overrides

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class FastScrollRecyclerView : RecyclerView {
    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        // Attach custom SnapHelper on init
        val snapHelper = NHLSnapHelper()
        snapHelper.attachToRecyclerView(this)
    }
}
