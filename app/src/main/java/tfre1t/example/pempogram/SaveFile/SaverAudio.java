package tfre1t.example.pempogram.SaveFile;

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

    private static final int _FILE = 1;
    private static final int _URL = 2;

    private static final int COPY_BYTES = 524288;

    private Context ctx;
    private static Uri savefileaudio;
    private static String saveurlaudio;
    private static String mimeType;

    public String saveFileAudio(Context context, Uri audio) {
        ctx = context;
        savefileaudio = audio;

        return writeFileAudio(_FILE);
    }

    public String saveUrlAudio(Context context, String audio, String mimeType) {
        ctx = context;
        saveurlaudio = audio;
        this.mimeType = mimeType;

        return writeFileAudio(_URL);
    }

    private String writeFileAudio(int type) {
        if(type != _URL){
            mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(ctx.getApplicationContext().getContentResolver().getType(savefileaudio));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEEMMMdyyyyHHmmssSSS", Locale.ENGLISH);
        String datetime = sdf.format(new Date(System.currentTimeMillis()));
        String filename = "Audiofile_" + datetime + "." + mimeType;

        if(type == _FILE) {
            onFileSaverAudio(filename);
        }else if(type == _URL){
            onURLSaverAudio(filename);
        }

        return filename;
    }

    private void onFileSaverAudio(final String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                            fOut.write(buf, 0 ,len);
                        }
                        fOut.flush();
                        fOut.close();
                    } catch (IOException | NullPointerException ex) {
                        Log.d(TAG, "Exception caught: " + ex.getMessage());
                    }

                }
            }
        }).start();
    }

    private void onURLSaverAudio(final String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                            fOut.write(buf, 0 ,len);
                        }
                        fOut.flush();
                        fOut.close();
                    } catch (IOException | NullPointerException ex) {
                        Log.d(TAG, "Exception caught: " + ex.getMessage());
                    }

                }
            }
        }).start();
    }
}
