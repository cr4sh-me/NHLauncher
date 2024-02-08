package com.cr4sh.nhlauncher.overrides

import android.view.View
import android.view.View.OnAttachStateChangeListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate

class NHLSnapHelper : LinearSnapHelper() {
    private val mainActivity: MainActivity = NHLManager.instance.mainActivity
    private var mVerticalHelper: OrientationHelper? = null

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        assert(recyclerView != null)
        recyclerView!!.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {}
            override fun onViewDetachedFromWindow(view: View) {}
        })
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val out = IntArray(2)
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager))
        }
        return out
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return if (layoutManager is LinearLayoutManager) {
            getStartView(layoutManager, getVerticalHelper(layoutManager))
        } else super.findSnapView(layoutManager)
    }

    private fun distanceToStart(targetView: View, helper: OrientationHelper?): Int {
        vibrate(mainActivity, 10)
        return helper!!.getDecoratedStart(targetView) - helper.startAfterPadding
    }

    private fun getStartView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper?
    ): View? {
        if (layoutManager is LinearLayoutManager) {
            val firstChild = layoutManager.findFirstVisibleItemPosition()
            val isLastItem = layoutManager
                .findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1
            if (firstChild == RecyclerView.NO_POSITION || isLastItem) {
                return null
            }
            val child = layoutManager.findViewByPosition(firstChild)
            return if (helper!!.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2
                && helper.getDecoratedEnd(child) > 0
            ) {
                child
            } else {
                if (layoutManager.findLastCompletelyVisibleItemPosition()
                    == layoutManager.getItemCount() - 1
                ) {
                    null
                } else {
                    layoutManager.findViewByPosition(firstChild + 1)
                }
            }
        }
        return super.findSnapView(layoutManager)
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
        if (mVerticalHelper == null) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        vibrate(mainActivity, 10)
        return RecyclerView.NO_POSITION
    }
}
