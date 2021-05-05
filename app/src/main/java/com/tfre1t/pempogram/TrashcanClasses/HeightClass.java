package com.tfre1t.pempogram.TrashcanClasses;

import android.app.Activity;
import android.util.DisplayMetrics;

import androidx.appcompat.widget.Toolbar;

public class HeightClass {

    public static int getStatusBarHeight(Activity act){
        int statusBarHeight = 0;
        int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = act.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public void setPadding(Activity act, Toolbar toolbar){
        int statusBarHeight = getStatusBarHeight(act);
        toolbar.setPadding(0, statusBarHeight, 0,0);
    }

    public static int getDisplayHeignt(Activity act){
        DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static int getDisplayHeignt_NonStatusBar(Activity act){
        return getDisplayHeignt(act) - getStatusBarHeight(act);
    }
}
