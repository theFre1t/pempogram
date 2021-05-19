package com.tfre1t.pempogram.MediaPlayer;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyMediaPlayer implements MediaPlayer.OnCompletionListener {

    //////////////////////Воспроизведение///////////////////////////////////////////////////////////

    public boolean mediaPlayerResume = false;
    private static MediaPlayer mediaPlayer;
    private View actionView;

    public void setActionView(View view){
        if(actionView != null && actionView != view){
            deactivatedView(); //Говорим View что он не активен
        }
        actionView = view;
    }

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
            activatedView(); //Говорим View что он активен
            mediaPlayerResume = true;
            mediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
            deactivatedView();
        }
    }

    public void playURL(String audiofile){
        if (mediaPlayerResume) {
            mediaPlayer.release();
        }
        try {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(audiofile);
            mediaPlayer.prepare();
            mediaPlayer.start();
            activatedView(); //Говорим View что он активен
            mediaPlayerResume = true;
            mediaPlayer.setOnCompletionListener(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayerResume = false;
        deactivatedView(); //Говорим View что он не активен
    }

    private void deactivatedView(){
        if(actionView != null && actionView.isActivated()){
            actionView.setActivated(false);
        }
    }

    private void activatedView(){
        if(actionView != null && !actionView.isActivated()){
            actionView.setActivated(true);
        }
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
        deactivatedView(); //Говорим View что он не активен
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
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        //    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.OGG);
        //    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.VORBIS);
        //}else {
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //}
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
        SimpleDateFormat sdf = new SimpleDateFormat("EEEMMMdyyyyHHmmss", Locale.ENGLISH);
        String datetime = sdf.format(new Date(System.currentTimeMillis()));
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? "/Audiofile_" + datetime + ".ogg" : "/Audiofile_" + datetime + ".3gp";
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
