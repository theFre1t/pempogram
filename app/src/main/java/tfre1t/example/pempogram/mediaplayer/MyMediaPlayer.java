package tfre1t.example.pempogram.mediaplayer;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class MyMediaPlayer implements MediaPlayer.OnCompletionListener {

    //////////////////////Воспроизведение///////////////////////////////////////////////////////////
    public boolean mediaPlayerResume = false;
    private static MediaPlayer mediaPlayer;

    public void play(Context ctx, String audiofile){
        if (mediaPlayerResume) {
            mediaPlayer.release();
        }
        try {
            mediaPlayer = new MediaPlayer();
            FileInputStream fis = ctx.openFileInput(audiofile);
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

    //////////////////////Запись////////////////////////////////////////////////////////////////////

    private static MediaRecorder mediaRecorder;
    private String fileName;
    private File outFile;

    public void startRecord(Context ctx){
        fileName = ctx.getFilesDir().getAbsolutePath() + getFilename();

        outFile = new File(fileName);
        if (outFile.exists()) outFile.delete();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(fileName);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    public void stopRecord() {
        if (mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public File getFileRecord(){
        return outFile;
    }

    public String getFilenameRecord(){
        return fileName;
    }

    private String getFilename(){
        String filename = null;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEMMMdyyyyHHmmss");
        String datetime = sdf.format(new Date(System.currentTimeMillis()));
        filename = "/Audiofile_" + datetime + ".3gp";
        return filename;
    }

    //////////////////////Воспроизведение записи////////////////////////////////////////////////////

    public void playRecord(){
        if (mediaPlayerResume) {
            mediaPlayer.release();
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getFilenameRecord());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayerResume = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(this);
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }
}
