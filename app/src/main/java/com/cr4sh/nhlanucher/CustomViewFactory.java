package com.cr4sh.nhlanucher;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Toast;

import java.lang.reflect.Field;

public class CustomViewFactory {
    private static final String TAG = "FontUtils";

    public static void setDefaultFont(Context context, String staticTypefaceFieldName, Typeface typeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, typeface);
        } catch (NoSuchFieldException | IllegalAccessException | RuntimeException e) {
            Toast.makeText(context, "E: " + e, Toast.LENGTH_SHORT).show();
        }
    }
}
