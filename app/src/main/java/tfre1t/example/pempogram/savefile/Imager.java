package tfre1t.example.pempogram.savefile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.customviewers.RoundedImageView;

import static android.content.Context.MODE_PRIVATE;

public class Imager{

    static final int ADD = 1;
    static final int EDIT = 2;
    static Thread t;

    static Context ctx;
    static RoundedImageView rImageView;
    static Bitmap bitmap;
    static String oldName;

    public String saveImage(Context context,RoundedImageView imageView){
        ctx = context;
        rImageView = imageView;

        String namefile = writeFileIMG(ADD);
        return namefile;
    }
    public String saveImage(Context context,RoundedImageView imageView, Bitmap bitmap, String name){
        ctx = context;
        rImageView = imageView;
        this.bitmap = bitmap;
        oldName = name;
        String namefile = writeFileIMG(EDIT);
        return namefile;
    }

    private String writeFileIMG(int act) {
        String filename = null;
        Bitmap savebitmap = null;
        try {
            savebitmap = ((BitmapDrawable) rImageView.getDrawable()).getBitmap();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEEMMMdyyyyHHmmss");
        String datetime = sdf.format(new Date(System.currentTimeMillis()));
        filename = "ImageCollection_" + datetime + ".png";

        if (act == ADD) {
            if (savebitmap == null) {
                filename = null;
            } else {
                onSaverImage(filename, savebitmap);
            }
        } else if (act == EDIT) {
            if (bitmap == savebitmap) {
                filename = oldName;
            } else {
                onSaverImage(filename, savebitmap);
            }
        }
        return filename;
    }

    private void onSaverImage(final String filename, final Bitmap savebitmap) {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    FileOutputStream fOut = ctx.openFileOutput(filename, MODE_PRIVATE);
                    savebitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    Bitmap img;
    public Bitmap setImageView(Context ctx, String path) {
        img = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.defaultimg);
        if(path != null) {
            try {
                FileInputStream fis = ctx.openFileInput(path);
                img = BitmapFactory.decodeStream(fis);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }
}
