package com.tfre1t.pempogram.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.lang.ref.WeakReference;
import java.util.List;

import com.tfre1t.pempogram.R;
import com.tfre1t.pempogram.database.Tables;
import com.tfre1t.pempogram.MediaPlayer.MyMediaPlayer;
import com.tfre1t.pempogram.adapter.FavoriteAudioAdater;
import com.tfre1t.pempogram.TrashcanClasses.GetHeightClass;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "myLog";

    private static int CURRENT_DATA; //Текущее состояние данных
    private static final int DATA_NONE = 0; // Данных нет
    private static final int GET_DATA_TRUE = 1; // Данные есть
    private static final int GET_DATA_DOWNLOAD = 2; // Данные в загрузке

    private int FAVAU_CURRENT = 0; //Текущее состояние
    private static final int FAVAU_DEFAULT = 0; // Состояние воиспроизведения
    private static final int FAVAU_REMOVE = 1; // Состояние удаления
    private static final int CODE_SELECT_AUDIO = 1; // Код перехода на добавления фразы

    private HomeViewModel homeViewModel;
    private MyMediaPlayer myMediaPlayer;
    private FavoriteAudioAdater favAuAdapter;

    private View v;
    private Context ctx;
    private Handler h;

    private List<Tables.AudiofileWithImg> oldListFavAu, listFavAu;

    private ImageButton btnDellFavAu;
    private ProgressBar pbLoader;
    private TextView tvEmpty;
    private RecyclerView rcVFavAu;
    private AdView adHomeBanner;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        v = inflater.inflate(R.layout.fragment_home, container, false);
        ctx = v.getContext();

        findViewById();
        adMod();
        loadData();
        return v;
    }

    private void findViewById() {
        rcVFavAu = v.findViewById(R.id.rcViewFavorAudio);
        btnDellFavAu = v.findViewById(R.id.btnDellFavAu);
        btnDellFavAu.setOnClickListener(this);
        pbLoader = v.findViewById(R.id.pbLoader);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        adHomeBanner = v.findViewById(R.id.adHomeBanner);

        View vStatusBar = v.findViewById(R.id.vStatusBar);
        vStatusBar.getLayoutParams().height = new GetHeightClass().getStatusBarHeight(getActivity());
        h = new MyHandler(this);
    }

    private void adMod() {
        MobileAds.initialize(ctx, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adHomeBanner.loadAd(new AdRequest.Builder().build());
    }

    //Получение и установка данных
    private void loadData() {
        h.sendEmptyMessage(GET_DATA_DOWNLOAD);
        //Получаем данные
        homeViewModel.getDataFavAu().observe(getViewLifecycleOwner(), new Observer<List<Tables.AudiofileWithImg>>() {
            @Override
            public void onChanged(List<Tables.AudiofileWithImg> list) {
                if (listFavAu != null) {
                    oldListFavAu = listFavAu; //Запоминаем старые данные
                }
                listFavAu = list;
                //Отправляем сообщение о наличие данных
                h.sendEmptyMessage(GET_DATA_TRUE);
            }
        });
    }

    static class MyHandler extends Handler {
        WeakReference<HomeFragment> wr;
        HomeFragment newCurrClass;

        public MyHandler(HomeFragment currClass) {
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
        switch (CURRENT_DATA) {
            case GET_DATA_DOWNLOAD:
                tvEmpty.setVisibility(View.GONE);
                pbLoader.setVisibility(View.VISIBLE);
                break;
            case GET_DATA_TRUE:
                pbLoader.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.GONE);
                if(favAuAdapter == null){
                    favAuAdapter = new FavoriteAudioAdater(ctx, listFavAu);
                    favAuAdapter.setItemClickListener(onItemClickListener);
                    rcVFavAu.setLayoutManager(new GridLayoutManager(ctx, 3));
                    rcVFavAu.setAdapter(favAuAdapter);
                }else {
                    FavAuDiffUtilCallback FavAuDiffUtil = new FavAuDiffUtilCallback(oldListFavAu, listFavAu);
                    DiffUtil.DiffResult FavAuDiffResult = DiffUtil.calculateDiff(FavAuDiffUtil);
                    favAuAdapter.swipeCursor(listFavAu);
                    FavAuDiffResult.dispatchUpdatesTo(favAuAdapter);
                }
                break;
        }
    }

    //Обработчик нажатий Adapter-a
    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (FAVAU_CURRENT) {
                case FAVAU_DEFAULT:
                    //Если нажали на "+", то перебрасывааем
                    //Если любой другой то проигрываем запись
                    if (id == -1) {
                        Intent intent = new Intent("android.intent.action.favoriteaudio.selectaudio");
                        startActivity(intent);
                    } else {
                        if (myMediaPlayer == null) {
                            myMediaPlayer = new MyMediaPlayer();
                        }
                        homeViewModel.playFavAu(myMediaPlayer, id);
                    }
                    break;
                case FAVAU_REMOVE:
                    TextView tvRemove = view.findViewById(R.id.tvRemove);
                    tvRemove.setVisibility(View.GONE);
                    //Убираем запись из Фаворитных
                    homeViewModel.removeFavAu(id);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnDellFavAu){
            switch (FAVAU_CURRENT){
                case FAVAU_DEFAULT:
                    FAVAU_CURRENT = FAVAU_REMOVE; //Сообщаем что мы в режиме удаления
                    btnDellFavAu.setImageResource(R.drawable.baseline_clear_24);
                    itemVisibility(View.GONE, View.VISIBLE);
                    break;
                case FAVAU_REMOVE:
                    FAVAU_CURRENT = FAVAU_DEFAULT; //Сообщаем что мы в обычном режиме
                    btnDellFavAu.setImageResource(R.drawable.baseline_delete_24);
                    itemVisibility(View.VISIBLE, View.GONE);
                    break;
            }
        }
    }

    //Отображаем/скрываем надпись Убрать
    private void itemVisibility(int VISIBILITY_ADD, int VISIBILITY_ALL) {
        //Проходимся по всем элементам.
        int i = 0;
        do{
            View vAdd = rcVFavAu.getChildAt(i);
            //Добавление скрываем
            //На всех остальных отображаем надпись "Убрать"
            if (vAdd.getId() == -1){
                vAdd.setVisibility(VISIBILITY_ADD);
            }
            else {
                TextView tvRemove = vAdd.findViewById(R.id.tvRemove);
                tvRemove.setVisibility(VISIBILITY_ALL);
            }
            i++;
        }while (favAuAdapter.getItemCount() > i);
    }

    //Обновляем RecyclerView
    public static class FavAuDiffUtilCallback extends DiffUtil.Callback{

        List<Tables.AudiofileWithImg> oldList;
        List<Tables.AudiofileWithImg> newList;

        FavAuDiffUtilCallback(List<Tables.AudiofileWithImg> oldList, List<Tables.AudiofileWithImg> newList){
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            int newId = newList.get(newItemPosition).id_audiofile;
            int oldId = oldList.get(oldItemPosition).id_audiofile;
            return newId == oldId;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            String newName = newList.get(newItemPosition).name_audiofile;
            String oldName = oldList.get(oldItemPosition).name_audiofile;
            return oldName.equals(newName);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Cleaner();
    }

    @Override
    public void onDestroy() {
        Cleaner();
        super.onDestroy();
    }

    private void Cleaner(){
        if (h != null) h.removeCallbacksAndMessages(null);
        if(myMediaPlayer != null) {
            myMediaPlayer.release();
            myMediaPlayer = null;
        }
        if(rcVFavAu != null) rcVFavAu.setAdapter(null);
        if(favAuAdapter != null) favAuAdapter = null;
        oldListFavAu = null;
        listFavAu = null;
    }
}