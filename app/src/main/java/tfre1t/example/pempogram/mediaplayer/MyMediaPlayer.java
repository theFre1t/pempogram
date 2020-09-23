package tfre1t.example.pempogram.mediaplayer;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.FileInputStream;
import java.io.IOException;

import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.ui.home.HomeFragment;

public class MyMediaPlayer implements MediaPlayer.OnCompletionListener {

    boolean mediaPlayerResume = false;
    static MediaPlayer mediaPlayer;
    static FileInputStream fis;

    Cursor cursor;

    public void play(Context ctx, DB db, long id){
        cursor = db.getDataAudiofileByIdAudifile(id);
        cursor.moveToFirst();

        if (mediaPlayerResume) {
            mediaPlayer.release();
        }
        try {
            mediaPlayer = new MediaPlayer();
            fis = ctx.openFileInput(cursor.getString(cursor.getColumnIndex(DB.COLUMN_AUDIOFILE)));
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayerResume = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayerResume = false;
    }

    public void release() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
