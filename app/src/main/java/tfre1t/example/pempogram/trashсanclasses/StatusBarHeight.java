package tfre1t.example.pempogram.trashсanclasses;

import android.app.Activity;

import androidx.appcompat.widget.Toolbar;

public class StatusBarHeight{

    private static int result;

    public void setPadding(Activity act, Toolbar toolbar){
        int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = act.getResources().getDimensionPixelSize(resourceId);
        }
        toolbar.setPadding(0, result, 0,0);
    }

    public int getStatusBarHeight(Activity act){
        int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = act.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
