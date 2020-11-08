package tfre1t.example.pempogram.fragment.dashboard;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.Room_DB;
import tfre1t.example.pempogram.dialog.Dialog_Add_Collection;
import tfre1t.example.pempogram.myadapter.CollectionAdater;
import tfre1t.example.pempogram.preferences.Preferenceser;
import tfre1t.example.pempogram.trashсanclasses.StatusBarHeight;
import tfre1t.example.pempogram.ui.dashboard.DashboardViewModel;

public class Dashboard_Collection_Fragment extends Fragment implements View.OnClickListener /*onStartDragListener*/ {

    private static final String TAG = "myLog";

    private static final int DATA_NONE = 0; // Данных нет
    private static final int DATA_TRUE = 1; // Данные есть
    private static final int DATA_DOWNLOAD = 2; // Данные в загрузке
    private static int CURRENT_DATA; //Текущее состояние данных

    private DashboardViewModel dashboardViewModel;
    private CollectionAdater cAdapter;

    private static Handler h;
    private FragmentTransaction fragTrans;
    private Preferenceser pref;
    private Context ctx;
    private View v;

    private static int type_ListCollection;
    private List<Room_DB.Collection> listColl;
    private List<Room_DB.Collection> oldListColl;
    private int lay;

    private Toolbar tbColl;
    private ProgressBar pbLoader;
    private RecyclerView rcVColl;
    private RecyclerView.LayoutManager lm;
    private RecyclerView.LayoutManager lmOld;
    private ImageButton btnListCard, btnListClassic, btnListGrid;
    private TextView tvEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);
        v = inflater.inflate(R.layout.fragment_dashboard_collectionlist, null);
        ctx = v.getContext();
        type_ListCollection = (pref = new Preferenceser(ctx)).loadTypeViewCollection();
        findViewById();
        setToolbar();
        loadData();
        return v;
    }

    private void findViewById() {
        btnListCard = v.findViewById(R.id.btnlistCard);
        btnListClassic = v.findViewById(R.id.btnlistClassic);
        btnListGrid = v.findViewById(R.id.btnlistGrid);
        rcVColl = v.findViewById(R.id.rcViewColl);
        pbLoader = v.findViewById(R.id.pbLoader);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        tbColl = v.findViewById(R.id.tbColl);

        btnListCard.setOnClickListener(this);
        btnListClassic.setOnClickListener(this);
        btnListGrid.setOnClickListener(this);
        v.findViewById(R.id.floatBtnAddColl).setOnClickListener(this);
    }

    private void setToolbar() {
        new StatusBarHeight().setPadding(getActivity(), tbColl);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(tbColl);
        ActionBar actionBar = activity.getSupportActionBar();
        setHasOptionsMenu(true);
        if(actionBar != null) {
            actionBar.setTitle(R.string.title_sets);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_collection_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            /*case R.id.btn_menu_positionSettingColl:
                ImageView imgV = v.findViewById(R.id.imgVHandle);
                imgV.setVisibility(View.VISIBLE);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Получение и установка данных
    private void loadData() {
        h = new MyHandler(this);
        h.sendEmptyMessage(DATA_DOWNLOAD);
        //Получаем данные
        dashboardViewModel.getDataColl().observe(getViewLifecycleOwner(), new Observer<List<Room_DB.Collection>>() {
            @Override
            public void onChanged(List<Room_DB.Collection> list) {
                if (listColl != null) {
                    //Запоминаем старые данные
                    oldListColl = listColl;
                }
                listColl = list;
                //Отправляем сообщение о наличие данных
                if (listColl == null) {
                    h.sendEmptyMessage(DATA_NONE);
                } else {
                    h.sendEmptyMessage(DATA_TRUE);
                }
            }
        });
    }

    static class MyHandler extends Handler {
        WeakReference<Dashboard_Collection_Fragment> wrDCF;
        Dashboard_Collection_Fragment newDCF;

        public MyHandler(Dashboard_Collection_Fragment dcf) {
            wrDCF = new WeakReference<>(dcf);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            newDCF = wrDCF.get();
            if(newDCF != null){
                switch (msg.what){
                    case DATA_DOWNLOAD:
                        CURRENT_DATA = DATA_DOWNLOAD;
                        newDCF.setData();
                        break;
                    case DATA_NONE:
                        CURRENT_DATA = DATA_NONE;
                        newDCF.setData();
                        break;
                    case DATA_TRUE:
                        CURRENT_DATA = DATA_TRUE;
                        newDCF.setData();
                        break;
                }
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
                if(lm != null){
                    lmOld = lm;
                }
                switch (type_ListCollection) {
                    case 0:
                        //Отображение по классическому типу
                        setColorBtn(btnListClassic);
                        lay = R.layout.card_dashboard_collection_classiclist;
                        lm = new LinearLayoutManager(ctx);
                        break;
                    case 1:
                        //Отображение карточого типа
                        setColorBtn(btnListCard);
                        lay = R.layout.card_dashboard_collection_cardlist;
                        lm = new LinearLayoutManager(ctx);
                        break;
                    case 2:
                        //Отображение карточого типа в 2 колонки
                        setColorBtn(btnListGrid);
                        lay = R.layout.card_dashboard_collection_cardgrid;
                        lm = new GridLayoutManager(ctx, 2);
                        break;
                }
                if(lmOld == lm) {
                    CollDiffUtilCallback CollDiffUtil = new CollDiffUtilCallback(oldListColl, listColl);
                    DiffUtil.DiffResult CollDiffResult = DiffUtil.calculateDiff(CollDiffUtil);
                    cAdapter.swipeCursor(listColl);
                    CollDiffResult.dispatchUpdatesTo(cAdapter);
                }
                else {
                    cAdapter = new CollectionAdater(ctx, lay, listColl/*,this*/);
                    cAdapter.setItemClickListener(onItemClickListener);
                    rcVColl.setLayoutManager(lm);
                    rcVColl.setAdapter(cAdapter);
                }
                /*ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(cAdater);
                mItemTouchHelper = new ItemTouchHelper(callback);
                mItemTouchHelper.attachToRecyclerView(rcVColl);*/
                break;
        }
    }

    //Обновляем RecyclerView
    public static class CollDiffUtilCallback extends DiffUtil.Callback{

        List<Room_DB.Collection> oldList;
        List<Room_DB.Collection> newList;

        CollDiffUtilCallback(List<Room_DB.Collection> oldList, List<Room_DB.Collection> newList){
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
            int newId = newList.get(newItemPosition).id_collection;
            int oldId = oldList.get(oldItemPosition).id_collection;
            return newId == oldId;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            String newName = newList.get(newItemPosition).name_collection;
            String oldName = oldList.get(oldItemPosition).name_collection;
            return oldName.equals(newName);
        }
    }

    /*@Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }*/

    //Выделяем выбранную кнопку
    private ImageButton oldBtn;
    private void setColorBtn(ImageButton btn) {
        if(oldBtn != null){
            oldBtn.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.SRC_ATOP);
            oldBtn.setColorFilter(getResources().getColor(R.color.colorTextSecondary), PorterDuff.Mode.SRC_ATOP);
        }
        oldBtn = btn;
        btn.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
        btn.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
    }

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Переход в Набор
            dashboardViewModel.selectCollById(view.getId());
            Dashboard_SetSoundsCollection_Fragment dashSetSoundCollFrag = new Dashboard_SetSoundsCollection_Fragment();
            fragTrans = getParentFragmentManager().beginTransaction();
            fragTrans.replace(R.id.frmLayoutDashFrag, dashSetSoundCollFrag);
            fragTrans.addToBackStack(null);
            fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragTrans.commit();
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnlistClassic) {
            if (type_ListCollection != 0) {
                type_ListCollection = 0;
                setData();
            }
        } else if (id == R.id.btnlistCard) {
            if (type_ListCollection != 1) {
                type_ListCollection = 1;
                setData();
            }
        } else if (id == R.id.btnlistGrid) {
            if (type_ListCollection != 2) {
                type_ListCollection = 2;
                setData();
            }
        } else if (id == R.id.floatBtnAddColl) {
            fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
            Dialog_Add_Collection dialog_AC = new Dialog_Add_Collection(ctx);
            dialog_AC.show(fragTrans, "addColl");
        }
    }

    @Override
    public void onStop() {
        pref.saveTypeViewCollection(type_ListCollection);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (h != null)
            h.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
