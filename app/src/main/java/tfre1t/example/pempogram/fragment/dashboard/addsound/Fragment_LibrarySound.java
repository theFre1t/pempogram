package tfre1t.example.pempogram.fragment.dashboard.addsound;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.lang.ref.WeakReference;
import java.util.List;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.Tables;
import tfre1t.example.pempogram.adapter.LibrarySoundAdapter;
import tfre1t.example.pempogram.ui.dashboard.DashboardViewModel;

public class Fragment_LibrarySound extends Fragment implements View.OnClickListener {
    private static final String TAG = "myLog";

    private static int CURRENT_DATA; //Текущее состояние данных
    private static final int DATA_NONE = 0; // Данных нет
    private static final int DATA_TRUE = 1; // Данные есть
    private static final int DATA_DOWNLOAD = 2; // Данные в загрузке

    private DashboardViewModel dashboardViewModel;
    private LibrarySoundAdapter lsAdapter;
    private InterstitialAd mInterstitialAd;

    private Handler h;
    private Context ctx;
    private View v;

    private List<Tables.AudiofileWithImg> listAudiofiles;
    private List<Tables.AudiofileFull> listSelectedAudiofiles;

    private TextView tvTitle, tvEmpty;
    private RecyclerView rvLibSound;
    private ProgressBar pbLoader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);
        v = inflater.inflate(R.layout.fragment_addsound_librarysound, null);
        ctx = v.getContext();

        findViewById();
        adMod();
        loadData();

        tvTitle.setText(R.string.title_select_phrases);
        return v;
    }

    private void findViewById() {
        tvTitle = v.findViewById(R.id.tvTitle);
        rvLibSound = v.findViewById(R.id.rvLibSound);
        v.findViewById(R.id.btnAdd).setOnClickListener(this);
        pbLoader = v.findViewById(R.id.pbLoader);
        tvEmpty = v.findViewById(R.id.tvEmpty);
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

    //Получение и установка данных
    private void loadData() {
        h = new MyHandler(this);
        h.sendEmptyMessage(DATA_DOWNLOAD);
        //Получаем данные
        dashboardViewModel.getAllAudiofiles().observe(getViewLifecycleOwner(), new Observer<List<Tables.AudiofileWithImg>>() {
            @Override
            public void onChanged(List<Tables.AudiofileWithImg> list) {
                listAudiofiles = list;
                //Отправляем сообщение о наличие данных
                if (listAudiofiles == null) {
                    h.sendEmptyMessage(DATA_NONE);
                } else {
                    h.sendEmptyMessage(DATA_TRUE);
                }
            }
        });
        dashboardViewModel.getAudiofilesByIdCollList().observe(getViewLifecycleOwner(), new Observer<List<Tables.AudiofileFull>>() {
            @Override
            public void onChanged(List<Tables.AudiofileFull> list) {
                listSelectedAudiofiles = list;
            }
        });
    }

    static class MyHandler extends Handler {
        WeakReference<Fragment_LibrarySound> wr;
        Fragment_LibrarySound newCurrClass;

        public MyHandler(Fragment_LibrarySound currClass) {
            wr = new WeakReference<>(currClass);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            newCurrClass = wr.get();
            if(newCurrClass != null){
                CURRENT_DATA = msg.what;
                newCurrClass.setData();
            }
        }
    }

    private void setData(){
        pbLoader.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        switch (CURRENT_DATA) {
            case DATA_DOWNLOAD:
                pbLoader.setVisibility(View.VISIBLE);
                break;
            case DATA_NONE:
                tvEmpty.setVisibility(View.VISIBLE);
                break;
            case DATA_TRUE:
                if(lsAdapter == null) {
                    lsAdapter = new LibrarySoundAdapter(ctx, listAudiofiles, listSelectedAudiofiles);
                    rvLibSound.setLayoutManager(new LinearLayoutManager(ctx));
                    rvLibSound.setAdapter(lsAdapter);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnAdd) {
            dashboardViewModel.editCollection(lsAdapter.getSounds());

            //Реклама
            if (mInterstitialAd != null) {
                mInterstitialAd.show(requireActivity());
            } else {
                Log.d(TAG, "The interstitial ad wasn't ready yet.");
            }

            Toast.makeText(ctx, R.string.message_saved, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }
}
