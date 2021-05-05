package com.tfre1t.pempogram.SaveFile;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.os.FileUtils.copy;

public class SaverAudio {
    private static final String TAG = "myLog";

    private static final int COPY_BYTES = 524288;

    private Context ctx;
    private Thread thread;

    public String saveFileAudio(Context context, Uri audio) {
        ctx = context;
        String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(ctx.getApplicationContext().getContentResolver().getType(audio));

        String filename = writeFileAudio(mimeType);
        onFileSaverAudio(audio, filename);
        return filename;
    }

    public String saveUrlAudio(Context context, String audio, String mimeType) {
        ctx = context;

        String filename = writeFileAudio(mimeType);
        onURLSaverAudio(audio, filename);
        return filename;
    }

    private String writeFileAudio(String mimeType) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEMMMdyyyyHHmmssSSS", Locale.ENGLISH);
        String datetime = sdf.format(new Date(System.currentTimeMillis()));
        return "Audiofile_" + datetime + "." + mimeType;
    }

    private void onFileSaverAudio(Uri savefileaudio, final String filename) {
        thread = new Thread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File tempFile = new File(ctx.getApplicationContext().getFilesDir().getAbsolutePath(), filename);
                try {
                    if (!tempFile.createNewFile()) {
                        Log.e("myLog", "error creating file");
                    }
                    InputStream inputStream = ctx.getApplicationContext().getContentResolver().openInputStream(savefileaudio);
                    if (inputStream != null) {
                        copy(inputStream, new FileOutputStream(tempFile));
                    }
                } catch (IOException | NullPointerException ex) {
                    Log.d(TAG, "Exception caught: " + ex.getMessage());
                }
            } else {
                byte[] buf = new byte[COPY_BYTES];
                int len;
                try {
                    InputStream inputStream = ctx.getApplicationContext().getContentResolver().openInputStream(savefileaudio);
                    FileOutputStream fOut = ctx.openFileOutput(filename, MODE_PRIVATE);
                    while ((len = inputStream.read(buf)) > 0) {
                        fOut.write(buf, 0, len);
                    }
                    fOut.flush();
                    fOut.close();
                } catch (IOException | NullPointerException ex) {
                    Log.d(TAG, "Exception caught: " + ex.getMessage());
                }

            }
        });
        thread.start();
        while (thread.isAlive()); //ждем пока поток закончит
    }

    private void onURLSaverAudio(String saveurlaudio, final String filename) {
        thread = new Thread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File tempFile = new File(ctx.getApplicationContext().getFilesDir().getAbsolutePath(), filename);
                try {
                    if (!tempFile.createNewFile()) {
                        Log.e("myLog", "error creating file");
                    }
                    URL inpS = new URL(saveurlaudio);
                    if (inpS != null) {
                        copy(inpS.openConnection().getInputStream(), new FileOutputStream(tempFile));
                    }
                } catch (IOException | NullPointerException ex) {
                    Log.d(TAG, "Exception caught: " + ex.getMessage());
                }
            } else {
                byte[] buf = new byte[COPY_BYTES];
                int len;
                try {
                    URL inpS = new URL(saveurlaudio);
                    FileOutputStream fOut = ctx.openFileOutput(filename, MODE_PRIVATE);
                    while ((len = inpS.openConnection().getInputStream().read(buf)) > 0) {
                        fOut.write(buf, 0, len);
                    }
                    fOut.flush();
                    fOut.close();
                } catch (IOException | NullPointerException ex) {
                    Log.d(TAG, "Exception caught: " + ex.getMessage());
                }

            }
        });
        thread.start();
        while (thread.isAlive()); //ждем пока поток закончит
    }
}
