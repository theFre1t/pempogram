package com.tfre1t.pempogram.fragment.dashboard.addsound;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import com.tfre1t.pempogram.R;
import com.tfre1t.pempogram.SaveFile.SaverAudio;
import com.tfre1t.pempogram.TrashcanClasses.FillingCheck;
import com.tfre1t.pempogram.ui.dashboard.DashboardViewModel;

import static android.app.Activity.RESULT_OK;

public class Fragment_InternalStorage extends Fragment implements View.OnClickListener{
    private static final String TAG = "myLog";

    private static final int RQS_OPEN_AUDIO = 2;

    private DashboardViewModel dashboardViewModel;
    private InterstitialAd mInterstitialAd;

    private View v;
    private Context ctx;
    private Uri selectedAudio;

    private TextView tvTitle, tvNameAudiofile;
    private EditText etExecutorSound, etNameSound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);
        v = inflater.inflate(R.layout.fragment_addsound_internalstorage, null);
        ctx = v.getContext();

        findViewById();
        adMod();

        tvTitle.setText(R.string.title_upload_phrase);
        return v;
    }

    private void findViewById() {
        tvTitle = v.findViewById(R.id.tvTitle);
        tvNameAudiofile = v.findViewById(R.id.tvNameAudiofile);
        etExecutorSound =  v.findViewById(R.id.etExecutorSound);
        etNameSound = v.findViewById(R.id.etNameSound);
        v.findViewById(R.id.btnSelectAudiofile).setOnClickListener(this);
        v.findViewById(R.id.btnAdd).setOnClickListener(this);
    }

    private void adMod() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(ctx, v.getResources().getString(R.string.ad_unit_id_Collection_Interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int vId = v.getId();
        if (vId == R.id.btnSelectAudiofile) {
            intent = new Intent(Intent.ACTION_PICK);
            intent.setType("audio/*");
            startActivityForResult(intent, RQS_OPEN_AUDIO);
        } else if (vId == R.id.btnAdd) {
            String nameSound = etNameSound.getText().toString();
            String executorSound = etExecutorSound.getText().toString();
            if (fillingCheck(nameSound, executorSound, selectedAudio)) {
                String audiofile = new SaverAudio().saveFileAudio(v.getContext(), selectedAudio);
                dashboardViewModel.addNewAudiofile(nameSound, executorSound, audiofile);

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(requireActivity());
                } else {
                    Log.d(TAG, "The interstitial ad wasn't ready yet.");
                }

                Toast.makeText(v.getContext(), R.string.message_phrase_loaded, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    private boolean fillingCheck(String nameSound, String executorSound, Uri audiofile) {
        FillingCheck fillCheck = new FillingCheck();
        if (fillCheck.fillingCheckEditText(ctx, nameSound, etNameSound)) return false;
        if (fillCheck.fillingCheckEditText(ctx, executorSound, etExecutorSound)) return false;
        return fillCheck.fillingCheckFile(ctx, audiofile, tvNameAudiofile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQS_OPEN_AUDIO) {
            if (resultCode == RESULT_OK) {
                selectedAudio = data.getData();
                tvNameAudiofile.setText(selectedAudio.getLastPathSegment());
            }
        }
    }
}
