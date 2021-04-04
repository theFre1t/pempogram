package com.tfre1t.pempogram.TrashcanClasses;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.widget.EditText;
import android.widget.TextView;

import com.tfre1t.pempogram.R;

public class FillingCheck {

    public boolean fillingCheckEditText(Context ctx, String text, EditText editText) {
        if (text.equals("")) {
            editText.getBackground().mutate().setColorFilter(ctx.getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
            return true;
        } else {
            editText.getBackground().mutate().setColorFilter(ctx.getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
            return false;
        }
    }

    public boolean fillingCheckFile(Context ctx, Uri file, TextView textView){
        if(file != null){
            textView.setTextColor(ctx.getResources().getColor(R.color.colorTextPrimary));
            return true;
        }
        else {
            textView.setTextColor(ctx.getResources().getColor(android.R.color.holo_red_light));
            textView.setText(textView.getText()+"!");
            return false;
        }
    }
}
