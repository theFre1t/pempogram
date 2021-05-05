package com.tfre1t.pempogram.SaveFile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    /**
     * Сохраняем картинку
     */
    public String saveBitmapImage(Context context, Bitmap bitmap) {
        ctx = context;

        String filename = createName(ADD, bitmap);
        if(filename != null) {
            onSaverImage(filename, bitmap, ADD);
        }
        return filename;
    }

    /**
     * Сохраняем новую картинку
     */
    public String replaceImage(Context context, Bitmap bitmap, Bitmap oldBitmap, String oldName) {
        this.ctx = context;

        String filename = createName(EDIT, bitmap, oldName, oldBitmap);
        if(filename != null) {
            onSaverImage(filename, bitmap, EDIT);
        }
        return filename;
    }

    /**
     * Сохраняем URL картинку
     */
    public String saveURLImage(Context context, String URLpath) {
        this.ctx = context;
        Bitmap bitmap = null;

        //Получаем bitmap по URL
        try {
            URL inpS = new URL(URLpath);
            bitmap = BitmapFactory.decodeStream(inpS.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String filename = createName(ADD, bitmap);
        if(filename != null) {
            onSaverImage(filename, bitmap, ADD);
        }
        return filename;
    }

    /**
     * Сохраняем URL кэш картинку
     */
    public String saveURLCacheImage(Context context, long revision, String URLpath, String oldFileName) {
        this.ctx = context;
        Bitmap bitmap = null;

        //Получаем bitmap по URL
        try {
            URL inpS = new URL(URLpath);
            bitmap = BitmapFactory.decodeStream(inpS.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //получаем уникальное имя(не всегда получается уникальным)
        String filename = createName(CACHE, bitmap);

        if(filename != null) {
            //делаем имя полностью уникальным
            filename = "cache_" + revision + "_" + filename;

            //Сравневанием имена старого и нового файла
            if (!filename.equals(oldFileName)) {
                onSaverImage(filename, bitmap, CACHE); //сохраняем на устройство
            }
        }
        return filename;
    }

    private String createName(int act, Bitmap bitmap) {
        if (act == ADD || act == CACHE) {
            if (bitmap == null) {
                return null;
            }
        }
        return generateName();
    }

    private String createName(int act, Bitmap bitmap, String oldName, Bitmap oldBitmap) {
        if (act == EDIT) {
            if (oldBitmap == bitmap || bitmap == null) {
                return oldName;
            } else if (oldName != null) {
                deleteImage(ctx, oldName);
            }
        }
        return generateName();
    }

    private String generateName(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEMMMdyyyyHHmmssSSS", Locale.ENGLISH);
        String datetime = sdf.format(new Date(System.currentTimeMillis()));
        return "Image" + datetime + ".jpg";
    }

    private void onSaverImage(String filename, Bitmap bitmap, int act) {
        thread = new Thread(() -> {
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
        });
        thread.start();
        while (thread.isAlive()) ; //ждем пока поток закончит
    }

    public Bitmap setImageView(Context ctx, String name, boolean fullsize) {
        Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.default_img);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public int getHashBitmap(String URLpath){
        try {
            URL inpS = new URL(URLpath);
            Bitmap bitmapToHash = BitmapFactory.decodeStream(inpS.openConnection().getInputStream());
            int[] buffer = new int[bitmapToHash.getWidth() * bitmapToHash.getHeight()];
            bitmapToHash.getPixels(buffer, 0, bitmapToHash.getWidth(), 0, 0, bitmapToHash.getWidth(), bitmapToHash.getHeight());
            return Arrays.hashCode(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void deleteImage(Context ctx, String nameImg) {
        if (!nameImg.equals("default.png")) {
            ctx.deleteFile(nameImg);
        }
    }
}
