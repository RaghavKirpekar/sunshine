package com.malmstein.example.sunshine;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.malmstein.example.sunshine.utils.ColorTweaker;
import com.malmstein.example.sunshine.view.InsetAwareToolbar;

public class SunshineActivity extends ActionBarActivity {

    private InsetAwareToolbar appBar;
    private ColorTweaker colorTweaker;

    private boolean setContentViewCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        colorTweaker = new ColorTweaker();

        setTaskDescriptionOnLollipopAndLater();
        setSystemBarsColorOnLollipopAndLater();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setContentViewCalled = true;
        findAndSetAppBarIfAny();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setContentViewCalled = true;
        findAndSetAppBarIfAny();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setContentViewCalled = true;
        findAndSetAppBarIfAny();
    }

    private void findAndSetAppBarIfAny() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setAppBarColor(getResources().getColor(R.color.primary));
        setStatusBarColorMaybe(getResources().getColor(R.color.primary_dark));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBarColorMaybe(int color) {
        if (isAtLeastLollipop()) {
            int statusBarColor = colorTweaker.getStatusBarVariantOf(color);
            getWindow().setStatusBarColor(statusBarColor);
        }
    }

    protected void setAppBarColor(int color) {
        if (appBar != null) {
            appBar.setBackgroundColor(color);
        }
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        this.appBar = (InsetAwareToolbar) toolbar;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setTaskDescriptionOnLollipopAndLater() {
        if (isAtLeastLollipop()) {
            ActivityManager.TaskDescription taskDescription = createTaskDescription();
            setTaskDescription(taskDescription);
        }
    }

    private static boolean isAtLeastLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ActivityManager.TaskDescription createTaskDescription() {
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        int color = getResources().getColor(R.color.primary);
        String title = getResources().getString(R.string.app_name);
        return new ActivityManager.TaskDescription(title, logo, color);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setSystemBarsColorOnLollipopAndLater() {
        if (isAtLeastLollipop()) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            Resources resources = getResources();
            window.setStatusBarColor(resources.getColor(R.color.primary_dark));
            window.setNavigationBarColor(resources.getColor(R.color.navigation_bar_background));
        }
    }
}
