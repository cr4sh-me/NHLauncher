package com.cr4sh.nhlauncher;

import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class NHLSnapHelper extends LinearSnapHelper {

    private static final float MILLISECONDS_PER_INCH = 100f; // Adjust this value as needed

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider scrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        View currentView = findSnapView(layoutManager);
        if (currentView == null) {
            return RecyclerView.NO_POSITION;
        }

        int currentPosition = layoutManager.getPosition(currentView);
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        PointF vectorForEnd = scrollVectorProvider.computeScrollVectorForPosition(itemCount - 1);
        if (vectorForEnd == null) {
            return RecyclerView.NO_POSITION;
        }

        int deltaJump;
        int deltaSign = (velocityX > 0 || velocityY > 0) ? 1 : -1;
        if (layoutManager.canScrollHorizontally()) {
            deltaJump = (int) Math.signum(vectorForEnd.x);
        } else {
            deltaJump = 0;
        }

        // Adjust the SmoothScroller duration based on MILLISECONDS_PER_INCH
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(currentView.getContext()) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };

        smoothScroller.setTargetPosition(currentPosition + deltaJump * deltaSign);
        layoutManager.startSmoothScroll(smoothScroller);

        return RecyclerView.NO_POSITION; // Return NO_POSITION to indicate that the snap has been initiated
    }
}
