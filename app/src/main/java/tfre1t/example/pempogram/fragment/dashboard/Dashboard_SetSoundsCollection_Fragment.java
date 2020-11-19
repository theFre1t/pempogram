package tfre1t.example.pempogram.fragment.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB_Table;
import tfre1t.example.pempogram.database.Room_DB;
import tfre1t.example.pempogram.dialog.Dialog_Delete_Collecton;
import tfre1t.example.pempogram.dialog.Dialog_Delete_Sound;
import tfre1t.example.pempogram.dialog.Dialog_Edit_Collection;
import tfre1t.example.pempogram.dialog.Dialog_Edit_Sound;
import tfre1t.example.pempogram.mediaplayer.MyMediaPlayer;
import tfre1t.example.pempogram.myadapter.SetSoundAdapter;
import tfre1t.example.pempogram.savefile.Imager;
import tfre1t.example.pempogram.ui.dashboard.DashboardViewModel;

public class Dashboard_SetSoundsCollection_Fragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "myLog";

    private static int CURRENT_DATA; //Текущее состояние данных
    private static final int DATA_NONE = 0; // Данных нет
    private static final int DATA_TRUE = 1; // Данные есть
    private static final int DATA_DOWNLOAD = 2; // Данные в загрузке

    private MyMediaPlayer myMediaPlayer;
    private DashboardViewModel dashboardViewModel;
    private SetSoundAdapter scAdapter;

    private View v;
    private Context ctx;
    private Handler h;

    private List<DB_Table.AudiofileFull> listAudiofiles, oldListAudiofiles;

    private Toolbar tbSetSound;
    private ProgressBar pbLoader;
    private RecyclerView rvSetSounds;
    private ImageView imgVCollectionBack;
    private TextView tvNameColl, tvAuthorColl, tvEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);
        v = inflater.inflate(R.layout.fragment_dashboard_setsounds_collection, null);
        ctx = v.getContext();

        findViewById();
        setToolbar();
        loadPresenColl();
        loadData();
        return v;
    }

    private void findViewById() {
        tbSetSound = v.findViewById(R.id.tbSetSound);
        imgVCollectionBack = v.findViewById(R.id.imgViewBackImage);
        tvNameColl = v.findViewById(R.id.tvNameColl);
        tvAuthorColl = v.findViewById(R.id.tvAuthorColl);
        v.findViewById(R.id.imgBtnAddSound).setOnClickListener(this);
        pbLoader = v.findViewById(R.id.pbLoader);
        rvSetSounds = v.findViewById(R.id.rvSetSounds);
        tvEmpty = v.findViewById(R.id.tvEmpty);
    }

    private void setToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(tbSetSound);
        ActionBar actionBar = activity.getSupportActionBar();
        setHasOptionsMenu(true);
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_setsound_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_menu_editColl:
                //Вызывает диалог редактирования коллекции
                new Dialog_Edit_Collection().show(getActivity().getSupportFragmentManager().beginTransaction(), "editColl");
                return true;
            case R.id.btn_menu_delColl:
                //Вызывает диалог удаления коллекции
                new Dialog_Delete_Collecton().show(getActivity().getSupportFragmentManager().beginTransaction(), "dellColl");
                return true;
            case android.R.id.home:
                //Возвращаемся назад
                getParentFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadPresenColl() {
        dashboardViewModel.getDataSelectedColl().observe(getViewLifecycleOwner(), new Observer<Room_DB.Collection>() {
            @Override
            public void onChanged(Room_DB.Collection collection) {
                imgVCollectionBack.setImageBitmap(new Imager().setImageView(ctx, collection.img_collection));
                tvNameColl.setText(collection.name_collection);
                tvAuthorColl.setText(collection.author_collection);
            }
        });
    }

    //Получение и установка данных
    private void loadData() {
        h = new MyHandler(this);
        h.sendEmptyMessage(DATA_DOWNLOAD);
        //Получаем данные
        dashboardViewModel.getAudiofilesSelectedColl().observe(getViewLifecycleOwner(), new Observer<List<DB_Table.AudiofileFull>>() {
            @Override
            public void onChanged(List<DB_Table.AudiofileFull> list) {
                if (listAudiofiles != null) {
                    oldListAudiofiles = listAudiofiles; //Запоминаем старые данные
                }
                listAudiofiles = list;
                //Отправляем сообщение о наличие данных
                if (listAudiofiles == null) {
                    h.sendEmptyMessage(DATA_NONE);
                } else {
                    h.sendEmptyMessage(DATA_TRUE);
                }
            }
        });
    }

    static class MyHandler extends Handler {
        WeakReference<Dashboard_SetSoundsCollection_Fragment> wrDSSCF;
        Dashboard_SetSoundsCollection_Fragment newDSSCF;

        public MyHandler(Dashboard_SetSoundsCollection_Fragment dsscf) {
            wrDSSCF = new WeakReference<>(dsscf);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            newDSSCF = wrDSSCF.get();
            if(newDSSCF != null){
                CURRENT_DATA = msg.what;
                newDSSCF.setData();
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
                if(scAdapter == null) {
                    scAdapter = new SetSoundAdapter(ctx, listAudiofiles);
                    scAdapter.setItemClickListener(onItemClickListener);
                    scAdapter.setMenuClickListener(onMenuClickListener);
                    rvSetSounds.setLayoutManager(new LinearLayoutManager(ctx));
                    rvSetSounds.setAdapter(scAdapter);
                }else {
                    AudiofilesDiffUtilCallback AudDiffUtil = new AudiofilesDiffUtilCallback(oldListAudiofiles, listAudiofiles);
                    DiffUtil.DiffResult AudDiffResult = DiffUtil.calculateDiff(AudDiffUtil);
                    scAdapter.swipeList(listAudiofiles);
                    AudDiffResult.dispatchUpdatesTo(scAdapter);
                }
                break;
        }
    }

    //Обновляем RecyclerView
    public static class AudiofilesDiffUtilCallback extends DiffUtil.Callback{

        List<DB_Table.AudiofileFull> oldList;
        List<DB_Table.AudiofileFull> newList;

        AudiofilesDiffUtilCallback(List<DB_Table.AudiofileFull> oldList, List<DB_Table.AudiofileFull> newList){
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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgBtnAddSound) {
            //Добавление новой аудиозаписи в Набор
            Intent intent = new Intent("android.intent.action.setsound.addsound");
            intent.putExtra("idColl", dashboardViewModel.getIdCollection());
            startActivity(intent);
        }
    }

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

    private View.OnClickListener onMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(ctx, v);
            popup.inflate(R.menu.popup_setsound_menu);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    dashboardViewModel.selectAudiofileById(v.getId());
                    int itemId = item.getItemId();
                    if (itemId == R.id.btn_popup_editsound) {
                        //Редактировние аудиозаписи
                        new Dialog_Edit_Sound().show(getActivity().getSupportFragmentManager().beginTransaction(), "editSound");
                        return true;
                    } else if (itemId == R.id.btn_popup_deletesound) {
                        //Удаление аудиозаписи
                        new Dialog_Delete_Sound().show(getActivity().getSupportFragmentManager().beginTransaction(), "dellSound");
                        return true;
                    }
                    return false;
                }
            });
            popup.show();
        }
    };

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
        if (h != null)
            h.removeCallbacksAndMessages(null);
        if(myMediaPlayer != null) {
            myMediaPlayer.release();
            myMediaPlayer = null;
        }
        rvSetSounds.setAdapter(null);
        scAdapter = null;
        oldListAudiofiles = null;
        listAudiofiles = null;
        getParentFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }
}
