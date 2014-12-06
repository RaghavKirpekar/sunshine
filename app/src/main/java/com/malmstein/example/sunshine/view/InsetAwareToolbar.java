package com.malmstein.example.sunshine.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.WindowInsets;

public class InsetAwareToolbar extends Toolbar {

    private int mTopInset;

    public InsetAwareToolbar(Context context) {
        super(context);
    }

    public InsetAwareToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InsetAwareToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getTopInset() {
        return mTopInset;
    }

    public void setTopInset(int topInset) {
        int realPaddingTop = getPaddingTop() - mTopInset;
        setPadding(getPaddingLeft(), realPaddingTop + topInset, getPaddingRight(), getPaddingBottom());
        mTopInset = topInset;
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTopInset(insets.top);
            return false;
        }
        // If we're on Lollipop or later, we get insets in onApplyWindowInsets
        return super.fitSystemWindows(insets);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (insets.hasSystemWindowInsets()) {
            Rect systemWindowInsets = getSystemWindowInsetsRectFrom(insets);
            setTopInset(systemWindowInsets.top);
            return insets;
        }
        return super.onApplyWindowInsets(insets);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Rect getSystemWindowInsetsRectFrom(WindowInsets insets) {
        return new Rect(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        mTopInset = 0;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        mTopInset = 0;
    }

}
