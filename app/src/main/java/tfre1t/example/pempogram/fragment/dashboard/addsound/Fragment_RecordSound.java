package tfre1t.example.pempogram.fragment.dashboard.addsound;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.MediaPlayer.MyMediaPlayer;
import tfre1t.example.pempogram.TrashcanClasses.CheckPermission;
import tfre1t.example.pempogram.TrashcanClasses.FillingCheck;
import tfre1t.example.pempogram.ui.dashboard.DashboardViewModel;

public class Fragment_RecordSound extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Chronometer.OnChronometerTickListener {
    private static final String ZERO_TIME = "00:00";

    private static final int START_RECORD = 1;
    private static final int STOP_RECORD = 2;
    private static final int PLAY_RECORD = 100;
    private static final int DELETE_CURRENT_RECORD = 200;

    private DashboardViewModel dashboardViewModel;
    private MyMediaPlayer myMediaPlayer;
    private InterstitialAd mInterstitialAd;

    private Context ctx;
    private View v;
    private File recordAudio;

    private boolean isSave;
    private int timeRecord;

    private TextView tvTitle;
    private EditText etExecutorSound, etNameSound;
    private ImageButton imgBtnRecordAudiofile, imgBtnPlayAudiofile;
    private SeekBar sbAudiofile;
    private Group recordGroup, playGroup;
    private Chronometer tvTime,  tvRecordTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);
        v = inflater.inflate(R.layout.fragment_addsound_recordsound, null);
        ctx = v.getContext();

        findViewById();
        adMod();

        isSave = false;
        tvTitle.setText(R.string.title_dictaphone);
        return v;
    }

    private void findViewById() {
        tvTitle = v.findViewById(R.id.tvTitle);
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

    private void adMod() {
        MobileAds.initialize(ctx, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mInterstitialAd = new InterstitialAd(ctx);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgBtnRecordAudiofile) {
            CheckPermission checkPermission = new CheckPermission();
            //Проверка на разрешение использование микрофона
            if (checkPermission.CheckPermissionRecord(getActivity()) || checkPermission.getCheckRecord()) {
                //myMediaPlayer == null записываем звук, если myMediaPlayer != null то останавливаем запись
                if (myMediaPlayer == null) {
                    myMediaPlayer = new MyMediaPlayer();
                    myMediaPlayer.startRecord(ctx);
                    groupViewers(START_RECORD);
                } else {
                    myMediaPlayer.stopRecord();
                    groupViewers(STOP_RECORD);
                    recordAudio = myMediaPlayer.getFileRecord();
                }
            }
        } //Воспроизводим запись
        else if (id == R.id.imgBtnPlayAudiofile) {
            myMediaPlayer.playRecord();
            timeRecord = myMediaPlayer.getDuration();
            groupViewers(PLAY_RECORD);
        } //Удаляем запись
        else if (id == R.id.imgBtnDelRecord) {
            myMediaPlayer.getFileRecord().delete();
            myMediaPlayer = null;
            groupViewers(DELETE_CURRENT_RECORD);
        } //сохраняем запись
        else if (id == R.id.btnAdd) {
            String nameSound = etNameSound.getText().toString();
            String executorSound = etExecutorSound.getText().toString();
            if (fillingCheck(nameSound, executorSound, recordAudio)) {
                dashboardViewModel.addNewAudiofile(nameSound, executorSound, recordAudio.getName());
                isSave = true;

                if(mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }

                Toast.makeText(ctx, R.string.message_phrase_loaded, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    private void groupViewers(int status){
        recordGroup.setVisibility(View.GONE);
        playGroup.setVisibility(View.GONE);
        switch (status){
            case START_RECORD:
                recordGroup.setVisibility(View.VISIBLE);
                imgBtnRecordAudiofile.setImageResource(R.drawable.baseline_stop_48);
                imgBtnRecordAudiofile.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
                tvTime.setBase(SystemClock.elapsedRealtime());
                tvTime.start();
                break;
            case STOP_RECORD:
                tvTime.stop();
                tvRecordTime.setText(tvTime.getText());
                tvTime.setText(ZERO_TIME);
                playGroup.setVisibility(View.VISIBLE);
                break;
            case PLAY_RECORD:
                playGroup.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tvRecordTime.setCountDown(true);
                    tvRecordTime.setBase(SystemClock.elapsedRealtime() + timeRecord);
                }
                else {
                    tvRecordTime.setBase(SystemClock.elapsedRealtime() - timeRecord);
                }
                imgBtnPlayAudiofile.setImageResource(R.drawable.baseline_stop_24);
                sbAudiofile.setMax(timeRecord-1);
                tvRecordTime.start();
                break;
            case DELETE_CURRENT_RECORD:
                tvRecordTime.stop();
                imgBtnRecordAudiofile.setImageResource(R.drawable.baseline_mic_48);
                imgBtnRecordAudiofile.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
                recordGroup.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean fillingCheck(String nameSound, String executorSound, File audiofile) {
        FillingCheck fillCheck = new FillingCheck();
        if (fillCheck.fillingCheckEditText(ctx, nameSound, etNameSound)) return false;
        if (fillCheck.fillingCheckEditText(ctx, executorSound, etExecutorSound)) return false;
        return audiofile != null;
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        onProgressChanged(sbAudiofile, myMediaPlayer.getCurrentPosition(), false);
        if(!myMediaPlayer.mediaPlayerResume){
            tvRecordTime.stop();
            tvRecordTime.setText(ZERO_TIME);
            imgBtnPlayAudiofile.setImageResource(R.drawable.baseline_play_arrow_24);
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
            if(!isSave){
                myMediaPlayer.getFileRecord().delete();
            }
        }
        super.onDestroy();
    }
}
