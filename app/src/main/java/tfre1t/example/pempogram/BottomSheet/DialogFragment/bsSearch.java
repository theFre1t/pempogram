package tfre1t.example.pempogram.BottomSheet.DialogFragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.ref.WeakReference;
import java.util.List;

import tfre1t.example.pempogram.CustomViewers.RoundedImageView;
import tfre1t.example.pempogram.MediaPlayer.MyMediaPlayer;
import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.SaveFile.Imager;
import tfre1t.example.pempogram.adapter.Search_SetSoundAdapter;
import tfre1t.example.pempogram.database.Room_DB;
import tfre1t.example.pempogram.database.Tables;
import tfre1t.example.pempogram.ui.search.SearchViewModel;

public class bsSearch extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String TAG = "myLog";

    private static int CURRENT_DATA; //Текущее состояние данных
    private static final int DATA_NONE = 0; // Данных нет
    private static final int DATA_TRUE = 1; // Данные есть
    private static final int DATA_DOWNLOAD = 2; // Данные в загрузке

    private MyMediaPlayer myMediaPlayer;
    private SearchViewModel searchViewModel;
    private Search_SetSoundAdapter ssAdapter;

    private Handler h;
    private View v;
    private Context ctx;

    private List<Room_DB.Online_Audiofile> oldListAudio, listAudio;

    private RoundedImageView imgColl;
    private TextView tvCollection, tvAuthor, tvEmpty;
    private ProgressBar pbLoader;
    private RecyclerView rvSetSound;
    private ImageView imgBtnAddStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        v = inflater.inflate(R.layout.bs_dialog_search_setsound, null);
        ctx = v.getContext();

        findViewById();
        loadPresenColl();
        loadData();
        return v;
    }

    private void findViewById() {
        imgColl = v.findViewById(R.id.imgColl);
        tvCollection = v.findViewById(R.id.tvCollection);
        tvAuthor = v.findViewById(R.id.tvAuthor);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        pbLoader = v.findViewById(R.id.pbLoader);
        rvSetSound = v.findViewById(R.id.rvSetSound);
        imgBtnAddStatus = v.findViewById(R.id.imgBtnAddStatus);

        imgBtnAddStatus.setOnClickListener(this);
    }

    private void loadPresenColl() {
        searchViewModel.Online_GetDataSelectedColl().observe(getViewLifecycleOwner(), new Observer<Tables.Online_CollectionView>() {
            @Override
            public void onChanged(Tables.Online_CollectionView online_collection) {
                imgColl.setImageBitmap(new Imager().setImageView(ctx, online_collection.Online_Collection.img_file_preview_collection));
                tvCollection.setText(online_collection.Online_Collection.name_collection);
                tvAuthor.setText(online_collection.Online_Collection.author_collection);
                if(online_collection.collectionWithCollection != null){
                    imgBtnAddStatus.setEnabled(false);
                    imgBtnAddStatus.setImageResource(R.drawable.baseline_playlist_add_check_24);
                    imgBtnAddStatus.setColorFilter(ctx.getResources().getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });
    }

    //Получение и установка данных
    private void loadData() {
        h = new MyHandler(this);
        h.sendEmptyMessage(DATA_DOWNLOAD);
        //Получаем данные
        searchViewModel.Online_GetAudiofilesSelectedColl().observe(getViewLifecycleOwner(), new Observer<List<Room_DB.Online_Audiofile>>() {
            @Override
            public void onChanged(List<Room_DB.Online_Audiofile> list) {
                if (listAudio != null) {
                    oldListAudio = listAudio; //Запоминаем старые данные
                }
                listAudio = list;
                //Отправляем сообщение о наличие данных
                if (listAudio == null) {
                    h.sendEmptyMessage(DATA_NONE);
                } else {
                    h.sendEmptyMessage(DATA_TRUE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int button = v.getId();
        if (button == R.id.imgBtnAddStatus) {
            imgBtnAddStatus.setEnabled(false);
            searchViewModel.addNewCollFromOnline();
        }
    }

    static class MyHandler extends Handler {
        WeakReference<bsSearch> wr;
        bsSearch newCurrClass;

        public MyHandler(bsSearch currClass) {
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
                if(ssAdapter == null) {
                    ssAdapter = new Search_SetSoundAdapter(ctx, listAudio);
                    ssAdapter.setItemClickListener(onItemClickListener);
                    rvSetSound.setLayoutManager(new LinearLayoutManager(ctx));
                    rvSetSound.setAdapter(ssAdapter);
                }else {
                    AudiofilesDiffUtilCallback AudDiffUtil = new AudiofilesDiffUtilCallback(oldListAudio, listAudio);
                    DiffUtil.DiffResult AudDiffResult = DiffUtil.calculateDiff(AudDiffUtil);
                    ssAdapter.swipeList(listAudio);
                    AudDiffResult.dispatchUpdatesTo(ssAdapter);
                }
                break;
        }
    }

    //Обновляем RecyclerView
    public static class AudiofilesDiffUtilCallback extends DiffUtil.Callback{

        List<Room_DB.Online_Audiofile> oldList;
        List<Room_DB.Online_Audiofile> newList;

        AudiofilesDiffUtilCallback(List<Room_DB.Online_Audiofile> oldList, List<Room_DB.Online_Audiofile> newList){
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
            long newRevision = newList.get(newItemPosition).revision_audiofile;
            long oldRevision = oldList.get(oldItemPosition).revision_audiofile;
            return newRevision == oldRevision;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            String newName = newList.get(newItemPosition).name_audiofile;
            String oldName = oldList.get(oldItemPosition).name_audiofile;
            String newAuthor = newList.get(newItemPosition).author_audiofile;
            String oldAuthor = oldList.get(oldItemPosition).author_audiofile;
            return oldName.equals(newName) && oldAuthor.equals(newAuthor);
        }
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Воиспроизведение
            if (myMediaPlayer == null) {
                myMediaPlayer = new MyMediaPlayer();
            }
            searchViewModel.OnlineLibrary_PlayAudio(myMediaPlayer ,v.getId());
        }
    };

    @Override
    public void onDestroy() {
        Cleaner();
        super.onDestroy();
    }

    private void Cleaner() {
        if (h != null)
            h.removeCallbacksAndMessages(null);
        if(myMediaPlayer != null){
            myMediaPlayer.release();
            myMediaPlayer = null;
        }
        rvSetSound.setAdapter(null);
        ssAdapter = null;
        oldListAudio = null;
        listAudio = null;
    }
}
