package tfre1t.example.pempogram.BottomSheet.DialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
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
import tfre1t.example.pempogram.adapter.OnlineLibraryAdapter;
import tfre1t.example.pempogram.adapter.OnlineLibrary_SetSoundAdapter;
import tfre1t.example.pempogram.adapter.SetSoundAdapter;
import tfre1t.example.pempogram.database.Tables;
import tfre1t.example.pempogram.fragment.dashboard.Dashboard_SetSoundsCollection_Fragment;
import tfre1t.example.pempogram.ui.dashboard.DashboardViewModel;
import tfre1t.example.pempogram.ui.dashboard.OnlineLibrary;

public class bsOnlineLibrary extends BottomSheetDialogFragment {
    private static final String TAG = "myLog";

    private static int CURRENT_DATA; //Текущее состояние данных
    private static final int DATA_NONE = 0; // Данных нет
    private static final int DATA_TRUE = 1; // Данные есть
    private static final int DATA_DOWNLOAD = 2; // Данные в загрузке

    private MyMediaPlayer myMediaPlayer;
    private DashboardViewModel dashboardViewModel;
    private OnlineLibrary_SetSoundAdapter ssAdapter;

    private Handler h;
    private View v;
    private Context ctx;

    private RoundedImageView imgColl;
    private TextView tvCollection, tvAuthor, tvEmpty;
    private ProgressBar pbLoader;
    private RecyclerView rvSetSound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bs_dialog_onlinelibrary_setsound, null);
        ctx = v.getContext();

        findViewById();
        setContent();
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
    }

    private void setContent() {
        /*imgColl.setImageBitmap(new Imager().setImageView(ctx, ));
        tvCollection.setText("");
        tvAuthor.setText("");*/
    }

    //Получение и установка данных
    private void loadData() {
        h = new MyHandler(this);
        h.sendEmptyMessage(DATA_DOWNLOAD);
        //Получаем данные
        dashboardViewModel.getAudiofilesSelectedColl().observe(getViewLifecycleOwner(), new Observer<List<Tables.AudiofileFull>>() {
            @Override
            public void onChanged(List<Tables.AudiofileFull> list) {
                /*if (listAudiofiles != null) {
                    oldListAudiofiles = listAudiofiles; //Запоминаем старые данные
                }
                listAudiofiles = list;
                //Отправляем сообщение о наличие данных
                if (listAudiofiles == null) {
                    h.sendEmptyMessage(DATA_NONE);
                } else {
                    h.sendEmptyMessage(DATA_TRUE);
                }*/
            }
        });
    }

    static class MyHandler extends Handler {
        WeakReference<bsOnlineLibrary> wr;
        bsOnlineLibrary newCurrClass;

        public MyHandler(bsOnlineLibrary currClass) {
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
                    //ssAdapter = new OnlineLibrary_SetSoundAdapter(ctx, listAudiofiles);
                    ssAdapter.setItemClickListener(onItemClickListener);
                    rvSetSound.setLayoutManager(new LinearLayoutManager(ctx));
                    rvSetSound.setAdapter(ssAdapter);
                }/**else {
                    Dashboard_SetSoundsCollection_Fragment.AudiofilesDiffUtilCallback AudDiffUtil = new Dashboard_SetSoundsCollection_Fragment.AudiofilesDiffUtilCallback(oldListAudiofiles, listAudiofiles);
                    DiffUtil.DiffResult AudDiffResult = DiffUtil.calculateDiff(AudDiffUtil);
                    scAdapter.swipeList(listAudiofiles);
                    AudDiffResult.dispatchUpdatesTo(scAdapter);
                }**/
                break;
        }
    }

    //Обновляем RecyclerView
    /*public static class AudiofilesDiffUtilCallback extends DiffUtil.Callback{

        List<Tables.AudiofileFull> oldList;
        List<Tables.AudiofileFull> newList;

        AudiofilesDiffUtilCallback(List<Tables.AudiofileFull> oldList, List<Tables.AudiofileFull> newList){
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
            String newExecutor = newList.get(newItemPosition).executor_audiofile;
            String oldExecutor = oldList.get(oldItemPosition).executor_audiofile;
            return oldName.equals(newName) && oldExecutor.equals(newExecutor);
        }
    }*/

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Воиспроизведение
            if (myMediaPlayer == null) {
                myMediaPlayer = new MyMediaPlayer();
            }
            dashboardViewModel.playAudio(myMediaPlayer ,v.getId());
        }
    };
}
