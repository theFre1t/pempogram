package tfre1t.example.pempogram.fragment.dashboard.addsound;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import java.io.File;

import tfre1t.example.pempogram.CheckPermission;
import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.mediaplayer.MyMediaPlayer;

public class Fragment_RecordSound extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Chronometer.OnChronometerTickListener {

    static final int START_RECORD = 1;
    static final int STOP_RECORD = 2;
    static final int PLAY_RECORD = 100;
    static final int DELETE_CURRENT_RECORD = 200;
    boolean isSave;

    View v;
    TextView tvTitle;
    EditText etExecutorSound, etNameSound;
    ImageButton imgBtnRecordAudiofile, imgBtnPlayAudiofile;
    SeekBar sbAudiofile;
    Group recordGroup, playGroup;
    private Chronometer tvTime,  tvRecordTime;

    MyMediaPlayer myMediaPlayer;

    DB db;
    long id;
    int timeRecord;

    File recordAudio = null;

    public Fragment_RecordSound(DB db, long i) {
        this.db = db;
        id = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_addsound_recordsound, null);
        onFindViewById();
        isSave = false;
        return v;
    }

    private void onFindViewById() {
        tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setText("Записать микрофон");
        tvTime = v.findViewById(R.id.chTime);
        tvRecordTime = v.findViewById(R.id.chRecordTime);
        tvRecordTime.setOnChronometerTickListener(this);

        sbAudiofile = v.findViewById(R.id.sbAudiofile);
        sbAudiofile.setOnSeekBarChangeListener(this);

        etExecutorSound = v.findViewById(R.id.etExecutorSound);
        etNameSound = v.findViewById(R.id.etNameSound);

        imgBtnRecordAudiofile = v.findViewById(R.id.imgBtnRecordAudiofile);
        imgBtnRecordAudiofile.setOnClickListener(this);
        imgBtnPlayAudiofile = v.findViewById(R.id.imgBtnPlayAudiofile);
        imgBtnPlayAudiofile.setOnClickListener(this);
        v.findViewById(R.id.imgBtnDelRecord).setOnClickListener(this);
        v.findViewById(R.id.btnAdd).setOnClickListener(this);

        recordGroup = v.findViewById(R.id.recordGroup);
        playGroup = v.findViewById(R.id.playGroup);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnRecordAudiofile:
                CheckPermission checkPermission = new CheckPermission();
                //Проверка на разрешение использование микрофона
                if (checkPermission.CheckPermissionRecord(getActivity()) || checkPermission.getCheckRecord()) {
                    //myMediaPlayer == null записываем звук, если myMediaPlayer != null то останавливаем запись
                    if (myMediaPlayer == null) {
                        myMediaPlayer = new MyMediaPlayer();
                        myMediaPlayer.startRecord(v.getContext());
                        groupViewers(START_RECORD);
                    } else {
                        myMediaPlayer.stopRecord();
                        groupViewers(STOP_RECORD);
                        recordAudio = myMediaPlayer.getFileRecord();
                    }
                }
                break;
            //Воспроизводим запись
            case R.id.imgBtnPlayAudiofile:
                myMediaPlayer.playRecord();
                timeRecord = myMediaPlayer.getDuration();
                groupViewers(PLAY_RECORD);
                break;
                //Удаляем запись
            case R.id.imgBtnDelRecord:
                myMediaPlayer.getFileRecord().delete();
                myMediaPlayer = null;
                groupViewers(DELETE_CURRENT_RECORD);
                break;
                //сохраняем запись
            case R.id.btnAdd:
                String nameSound = etNameSound.getText().toString();
                String executorSound = etExecutorSound.getText().toString();
                if (!fillingCheck(nameSound, executorSound, recordAudio)) {
                    break;
                }
                String audiofile = recordAudio.getName();
                db.addRecAudiofile(nameSound, executorSound, audiofile, id);
                isSave = true;
                Toast.makeText(v.getContext(), "Запись добавлена", Toast.LENGTH_SHORT).show();
                requireActivity().setResult(1);
                requireActivity().finish();
                break;
        }
    }

    private void groupViewers(int status){
        switch (status){
            case START_RECORD:
                imgBtnRecordAudiofile.setImageResource(android.R.drawable.picture_frame);
                imgBtnRecordAudiofile.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
                tvTime.setBase(SystemClock.elapsedRealtime());
                tvTime.start();
                break;
            case STOP_RECORD:
                tvTime.stop();
                tvRecordTime.setText(tvTime.getText());
                tvTime.setText("00:00");

                recordGroup.setVisibility(View.GONE);
                playGroup.setVisibility(View.VISIBLE);
                break;
            case PLAY_RECORD:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tvRecordTime.setCountDown(true);
                    tvRecordTime.setBase(SystemClock.elapsedRealtime() + timeRecord);
                }
                else {
                    tvRecordTime.setBase(SystemClock.elapsedRealtime() - timeRecord);
                }
                imgBtnPlayAudiofile.setImageResource(android.R.drawable.picture_frame);
                sbAudiofile.setMax(timeRecord-1);
                tvRecordTime.start();
                break;
            case DELETE_CURRENT_RECORD:
                tvRecordTime.stop();
                imgBtnRecordAudiofile.setImageResource(android.R.drawable.ic_btn_speak_now);
                imgBtnRecordAudiofile.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
                recordGroup.setVisibility(View.VISIBLE);
                playGroup.setVisibility(View.GONE);
                break;
        }
    }

    private boolean fillingCheck(String nameSound, String executorSound, File audiofile) {
        if (!nameSound.equals("")) {
            etNameSound.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
            if (!executorSound.equals("")) {
                etExecutorSound.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
                return audiofile != null;
            } else {
                etExecutorSound.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
                return false;
            }
        } else {
            etNameSound.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
            return false;
        }
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        onProgressChanged(sbAudiofile, myMediaPlayer.getCurrentPosition(), false);
        if(!myMediaPlayer.mediaPlayerResume){
            tvRecordTime.stop();
            tvRecordTime.setText("00:00");
            imgBtnPlayAudiofile.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBar.setProgress(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onDestroy() {
        if(myMediaPlayer != null){
            Log.d("myLog", "onDestroy: isSave=" + isSave);
            if(!isSave){
                myMediaPlayer.getFileRecord().delete();
            }
        }
        super.onDestroy();
    }
}
