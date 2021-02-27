package tfre1t.example.pempogram.SaveFile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import tfre1t.example.pempogram.R;

import static android.content.Context.MODE_PRIVATE;

public class Imager{
    private static final String TAG = "myLog";

    private static final int ADD = 1;
    private static final int EDIT = 2;

    private Context ctx;
    private Thread thread;

    private static Bitmap oldBitmap;
    private static Bitmap bitmap;
    private static String oldName;

    /**Сохраняем картинку*/
    public String saveImage(Context context, Bitmap bitmap){
        ctx = context;
        Imager.bitmap = bitmap;

        return writeFileIMG(ADD);
    }

    /**Сохраняем новую картинку*/
    public String saveImage(Context context, Bitmap bitmap, Bitmap oldBitmap, String oldName){
        ctx = context;
        Imager.bitmap = bitmap;
        Imager.oldBitmap = oldBitmap;
        Imager.oldName = oldName;

        return writeFileIMG(EDIT);
    }

    private String writeFileIMG(int act) {
        if (act == ADD) {
            if (bitmap == null) {
                return null;
            }
        } else if (act == EDIT) {
            if (oldBitmap == bitmap || bitmap == null) {
                return oldName;
            }
            else deleteImage(ctx, oldName);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEEMMMdyyyyHHmmss", Locale.ENGLISH);
        String datetime = sdf.format(new Date(System.currentTimeMillis()));
        String filename = "ImageCollection_" + datetime + ".jpg";

        onSaverImage(filename, bitmap);
        while (thread.isAlive());
        return filename;
    }

    private void onSaverImage(final String filename, final Bitmap savebitmap) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream fOut = ctx.openFileOutput(filename, MODE_PRIVATE);
                    savebitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public Bitmap setImageView(Context ctx, String path) {
        bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.default_img);
        if(path != null) {
            try {
                FileInputStream fis = ctx.openFileInput(path);
                bitmap = BitmapFactory.decodeStream(fis);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void deleteImage(Context ctx, String nameImg){
        if (!nameImg.equals("default.png")) {
            ctx.deleteFile(nameImg);
        }
    }
}
