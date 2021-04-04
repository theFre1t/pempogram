package com.tfre1t.pempogram.SaveFile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.tfre1t.pempogram.R;

import static android.content.Context.MODE_PRIVATE;

public class Imager {
    private static final String TAG = "myLog";

    private static final int ADD = 1;
    private static final int EDIT = 2;
    private static final int CACHE = 10;

    private Context ctx;
    private Thread thread;

    private static Bitmap oldBitmap;
    private static Bitmap bitmap;
    private static String oldName;

    /**
     * Сохраняем картинку
     */
    public String saveBitmapImage(Context context, Bitmap bitmap) {
        ctx = context;
        Imager.bitmap = bitmap;

        String filename = writeFileIMG(ADD);

        onSaverImage(filename, ADD);
        while (thread.isAlive()) ;
        return filename;
    }

    /**
     * Сохраняем новую картинку
     */
    public String replaceImage(Context context, Bitmap bitmap, Bitmap oldBitmap, String oldName) {
        ctx = context;
        Imager.bitmap = bitmap;
        Imager.oldBitmap = oldBitmap;
        Imager.oldName = oldName;

        String filename = writeFileIMG(EDIT);

        onSaverImage(filename, EDIT);
        while (thread.isAlive()) ;
        return filename;
    }

    /**
     * Сохраняем URL картинку
     */
    public String saveURLImage(Context context, String URLpath) {
        this.ctx = context;

        try {
            URL inpS = new URL(URLpath);
            bitmap = BitmapFactory.decodeStream(inpS.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String filename = writeFileIMG(ADD);

        onSaverImage(filename, ADD);
        while (thread.isAlive()) ;
        return filename;
    }

    /**
     * Сохраняем URL кэш картинку
     */
    public String saveURLCacheImage(Context context, String URLpath, String oldFileName) {
        this.ctx = context;
        this.oldName = oldFileName;

        try {
            URL inpS = new URL(URLpath);
            bitmap = BitmapFactory.decodeStream(inpS.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (oldName != null) {
            try {
                oldBitmap = BitmapFactory.decodeStream(ctx.openFileInput(oldName));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String filename = writeFileIMG(CACHE);

        if (filename != oldFileName) {
            onSaverImage(filename, CACHE);
            while (thread.isAlive()) ;
        }
        return filename;
    }

    private String writeFileIMG(int act) {
        if (act == ADD) {
            if (bitmap == null) {
                return null;
            }
        } else if (act == EDIT || act == CACHE) {
            if (oldBitmap == bitmap || bitmap == null) {
                return oldName;
            } else if (oldName != null) {
                deleteImage(ctx, oldName);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEEMMMdyyyyHHmmssSSS", Locale.ENGLISH);
        String datetime = sdf.format(new Date(System.currentTimeMillis()));
        String filename = "ImageCollection" + datetime + ".jpg";

        if (act == CACHE){
            filename = "cache_" + filename;
        }

        return filename;
    }

    private void onSaverImage(String filename, int act) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                        FileOutputStream fOut = ctx.openFileOutput(filename, MODE_PRIVATE);
                        int x, y, Width, Height;
                        if (bitmap.getWidth() > bitmap.getHeight()){
                            x = (bitmap.getWidth() - bitmap.getHeight()) / 2;
                            y = 0;
                            Width = Math.min(bitmap.getHeight(), 1080);
                            Height = Math.min(bitmap.getHeight(), 1080);
                        }
                        else {
                            x = 0;
                            y = (bitmap.getHeight() - bitmap.getWidth()) / 2;
                            Width = Math.min(bitmap.getWidth(), 1080);
                            Height = Math.min(bitmap.getWidth(), 1080);
                        }
                        Bitmap savebitmap = Bitmap.createBitmap(bitmap, x, y , Width, Height);
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


    public Bitmap setImageView(Context ctx, String name, boolean fullsize) {
        bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.default_img);
        if (name != null) {
            try {
                FileInputStream fis = ctx.openFileInput(name);
                bitmap = BitmapFactory.decodeStream(fis);

                int Width, Height;
                if(fullsize) {
                    Width = Math.min(bitmap.getHeight(), bitmap.getWidth());
                    Height = Math.min(bitmap.getHeight(), bitmap.getWidth());
                }
                else {
                    if (bitmap.getWidth() > bitmap.getHeight()){
                        Width = Math.min(bitmap.getHeight(), 256);
                        Height = Math.min(bitmap.getHeight(), 256);
                    }
                    else {
                        Width = Math.min(bitmap.getWidth(), 256);
                        Height = Math.min(bitmap.getWidth(), 256);
                    }
                }
                bitmap = Bitmap.createScaledBitmap(bitmap, Width, Height, false);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void deleteImage(Context ctx, String nameImg) {
        if (!nameImg.equals("default.png")) {
            ctx.deleteFile(nameImg);
        }
    }
}
