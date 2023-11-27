package com.cr4sh.nhlauncher;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private final OnSwipeListener listener;

    public SwipeGestureDetector(Context context, OnSwipeListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        assert e1 != null;
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffX) > Math.abs(diffY) &&
                Math.abs(diffX) > SWIPE_THRESHOLD &&
                Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            // Swipe right detected
            if (listener != null) {
                listener.onSwipeRight();
            }
            return true;
        }

        return false;
    }

    public interface OnSwipeListener {
        void onSwipeRight();
    }
}
