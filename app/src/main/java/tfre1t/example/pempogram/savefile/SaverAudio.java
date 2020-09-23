package tfre1t.example.pempogram.savefile;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;

import tfre1t.example.pempogram.roundedimageview.RoundedImageView;

import static android.content.Context.MODE_PRIVATE;
import static android.os.FileUtils.copy;

public class SaverAudio {

    private static final int COPY_BYTES = 524288;

    static Context ctx;
    static Uri saveaudio;

    public String saveAudio(Context context, Uri audio) {
        ctx = context;
        saveaudio = audio;

        String namefile = writeFileAudio();
        return namefile;
    }

    private String writeFileAudio() {
        String filename = null;
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(ctx.getApplicationContext().getContentResolver().getType(saveaudio));
        SimpleDateFormat sdf = new SimpleDateFormat("EEEMMMdyyyyHHmmss");
        String datetime = sdf.format(new Date(System.currentTimeMillis()));
        filename = "Audiofile_" + datetime + "." + extension;

        onSaverAudio(filename);

        return filename;
    }

    private void onSaverAudio(final String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    File tempFile = new File(ctx.getApplicationContext().getFilesDir().getAbsolutePath(), filename);
                    try {
                        boolean fileCreated = tempFile.createNewFile();
                        if (!fileCreated) {
                            Log.e("myLog", "error creating file");
                        }
                        InputStream inputStream = ctx.getApplicationContext().getContentResolver().openInputStream(saveaudio);
                        if (inputStream != null) {
                            copy(inputStream, new FileOutputStream(tempFile));
                        }
                    } catch (IOException | NullPointerException ex) {
                        Log.d("myLog", "Exception caught: " + ex.getMessage());
                    }
                } else {
                byte[] buf = new byte[COPY_BYTES];
                int len;
                    try {
                        InputStream inputStream = ctx.getApplicationContext().getContentResolver().openInputStream(saveaudio);
                        FileOutputStream fOut = ctx.openFileOutput(filename, MODE_PRIVATE);
                        while ((len = inputStream.read(buf)) > 0) {
                            fOut.write(buf, 0 ,len);
                        }
                        fOut.flush();
                        fOut.close();
                    } catch (IOException | NullPointerException ex) {
                        Log.d("myLog", "Exception caught: " + ex.getMessage());
                    }

                }
            }
        }).start();
    }
}
