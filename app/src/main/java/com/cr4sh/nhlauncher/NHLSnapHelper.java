package com.cr4sh.nhlauncher;

import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// modification of https://amitshekhar.me/blog/snaphelper
public class NHLSnapHelper extends LinearSnapHelper {
    private static final float MILLISECONDS_PER_INCH = 10f;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private OrientationHelper mVerticalHelper;

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);

        assert recyclerView != null;
        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View view) {
                // Do nothing when attached
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View view) {
                if (!executorService.isShutdown()) {
                    executorService.shutdown();
                }
            }
        });
    }

    @Override
    public int[] calculateScrollDistance(int velocityX, int velocityY) {
        // Adjust the sensitivity of the scroll here
        return super.calculateScrollDistance(velocityX, velocityY); // You can experiment with the multiplier
    }

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
        int[] out = new int[2];

        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager));
        }
        return out;
    }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {

        if (layoutManager instanceof LinearLayoutManager) {
            return getStartView(layoutManager, getVerticalHelper(layoutManager));
        }

        return super.findSnapView(layoutManager);
    }

    private int distanceToStart(View targetView, OrientationHelper helper) {
        return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
    }

    private View getStartView(RecyclerView.LayoutManager layoutManager,
                              OrientationHelper helper) {

        if (layoutManager instanceof LinearLayoutManager) {
            int firstChild = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

            boolean isLastItem = ((LinearLayoutManager) layoutManager)
                    .findLastCompletelyVisibleItemPosition()
                    == layoutManager.getItemCount() - 1;

            if (firstChild == RecyclerView.NO_POSITION || isLastItem) {
                return null;
            }

            View child = layoutManager.findViewByPosition(firstChild);

            if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2
                    && helper.getDecoratedEnd(child) > 0) {
                return child;
            } else {
                if (((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition()
                        == layoutManager.getItemCount() - 1) {
                    return null;
                } else {
                    return layoutManager.findViewByPosition(firstChild + 1);
                }
            }
        }

        return super.findSnapView(layoutManager);
    }

    private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

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

        // Check if the RecyclerView is currently scrolling
        if (layoutManager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition() !=
                    layoutManager.getItemCount() - 1) {
                // The RecyclerView is not at the end, allow scrolling
                return RecyclerView.NO_POSITION;
            }
        }

        executorService.submit(() -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(300); // Adjust the delay time as needed (e.g., 300 milliseconds)
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            // Adjust the SmoothScroller duration based on MILLISECONDS_PER_INCH
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(currentView.getContext()) {
                @Override
                protected int calculateTimeForScrolling(int dx) {
                    return super.calculateTimeForScrolling(dx) * 2; // Increase the multiplier as needed
                }

                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi * 0.5f; // Adjust multiplier
                }
            };

            smoothScroller.setTargetPosition(currentPosition + deltaJump * deltaSign);
            layoutManager.startSmoothScroll(smoothScroller);
        });

        return RecyclerView.NO_POSITION; // Return NO_POSITION to indicate that the snap has been initiated
    }
}
