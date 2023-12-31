package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.skydoves.colorpickerview.ColorPickerView;

public class ColorPickerMod extends ColorPickerView {
    public ColorPickerMod(Context context) {
        super(context);
    }

    public ColorPickerMod(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPickerMod(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Override touch event of ColorPickerView so we can use it inside ViewPager
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }

}
