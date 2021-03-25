package tfre1t.example.pempogram.TrashcanClasses;

import android.app.Activity;
import android.util.DisplayMetrics;

import androidx.appcompat.widget.Toolbar;

import tfre1t.example.pempogram.R;

public class GetHeightClass{

    private static int statusBarHeight;
    private static int displayHeignt;

    public void setPadding(Activity act, Toolbar toolbar){
        int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = act.getResources().getDimensionPixelSize(resourceId);
        }
        toolbar.setPadding(0, statusBarHeight, 0,0);
    }

    public int getStatusBarHeight(Activity act){
        int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = act.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public static int getDisplayHeignt(Activity act){
        DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
        displayHeignt = displayMetrics.heightPixels;
        return displayHeignt;
    }
}
