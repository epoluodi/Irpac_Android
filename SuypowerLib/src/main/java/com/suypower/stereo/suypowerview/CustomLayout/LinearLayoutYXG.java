package com.suypower.stereo.suypowerview.CustomLayout;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by cjw on 16/9/15.
 */
public class LinearLayoutYXG extends LinearLayout {
    private int[] mInsets = new int[4];

    public LinearLayoutYXG(Context context) {
        super(context);
    }

    public LinearLayoutYXG(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutYXG(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public final int[] getInsets() {
        return mInsets;
    }

    @Override
    protected final boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Intentionally do not modify the bottom inset. For some reason,
            // if the bottom inset is modified, window resizing stops working.
            // TODO: Figure out why.

            mInsets[0] = insets.left;
            mInsets[1] = insets.top;
            mInsets[2] = insets.right;

            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }

        return super.fitSystemWindows(insets);
    }
}
