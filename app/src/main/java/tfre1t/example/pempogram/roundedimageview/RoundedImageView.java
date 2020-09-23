package tfre1t.example.pempogram.roundedimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

public class RoundedImageView extends androidx.appcompat.widget.AppCompatImageView {

    Context ctx;

    public RoundedImageView(Context context) {
        super(context);
        ctx = context;
        View view = RoundedImageView.this;
        view.setClipToOutline(true);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        View view = RoundedImageView.this;
        view.setClipToOutline(true);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ctx = context;
        View view = RoundedImageView.this;
        view.setClipToOutline(true);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

}
