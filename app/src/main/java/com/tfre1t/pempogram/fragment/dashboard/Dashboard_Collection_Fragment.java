package com.tfre1t.pempogram.fragment.dashboard;

import android.app.SearchManager;
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
import android.widget.SearchView;
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
import java.util.Objects;

import com.tfre1t.pempogram.R;
import com.tfre1t.pempogram.database.Room_DB;
import com.tfre1t.pempogram.dialog.Dialog_Add_Collection;
import com.tfre1t.pempogram.adapter.CollectionAdater;
import com.tfre1t.pempogram.preferences.Preferenceser;
import com.tfre1t.pempogram.TrashcanClasses.HeightClass;
import com.tfre1t.pempogram.ui.dashboard.DashboardViewModel;

public class Dashboard_Collection_Fragment extends Fragment implements View.OnClickListener /*onStartDragListener*/ {

    private static final String TAG = "myLog";

    private static final int DATA_NONE = 0; // Данных нет
    private static final int GET_DATA_TRUE = 1; // Данные есть
    private static final int GET_DATA_DOWNLOAD = 2; // Данные в загрузке
    private static int CURRENT_DATA; //Текущее состояние данных

    private DashboardViewModel dashboardViewModel;
    private CollectionAdater cAdapter;

    private Handler h;
    private FragmentTransaction fragTrans;
    private Preferenceser pref;
    private Context ctx;
    private View v;

    private static int type_ListCollection;
    private List<Room_DB.Collection> listColl, oldListColl;
    private int lay;

    private Toolbar tbColl;
    private ProgressBar pbLoader;
    private RecyclerView rcVColl;
    private RecyclerView.LayoutManager lmOld, lm;
    private ImageButton btnListCard, btnListClassic, btnListGrid;
    private TextView tvEmpty;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
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

        h = new MyHandler(this);
    }

    private void setToolbar() {
        new HeightClass().setPadding(requireActivity(), tbColl);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Objects.requireNonNull(activity).setSupportActionBar(tbColl);
        ActionBar actionBar = activity.getSupportActionBar();
        setHasOptionsMenu(true);
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_sets);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_collection_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    h.sendEmptyMessage(GET_DATA_DOWNLOAD);
                    //Получаем данные
                    dashboardViewModel.getDataColl(newText).observe(getViewLifecycleOwner(), new Observer<List<Room_DB.Collection>>() {
                        @Override
                        public void onChanged(List<Room_DB.Collection> list) {
                            if (listColl != null) {
                                //Запоминаем старые данные
                                oldListColl = listColl;
                            }
                            listColl = list;
                            //Отправляем сообщение о наличие данных
                            h.sendEmptyMessage(GET_DATA_TRUE);
                        }
                    });
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.btn_menu_positionSettingColl:
                ImageView imgV = v.findViewById(R.id.imgVHandle);
                imgV.setVisibility(View.VISIBLE);
                return true;*/
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    //Получение и установка данных
    private void loadData() {
        h.sendEmptyMessage(GET_DATA_DOWNLOAD);
        //Получаем данные
        dashboardViewModel.getDataCollList().observe(getViewLifecycleOwner(), new Observer<List<Room_DB.Collection>>() {
            @Override
            public void onChanged(List<Room_DB.Collection> list) {
                if (listColl != null) {
                    //Запоминаем старые данные
                    oldListColl = listColl;
                }
                listColl = list;
                //Отправляем сообщение о наличие данных
                h.sendEmptyMessage(GET_DATA_TRUE);
            }
        });
    }

    static class MyHandler extends Handler {
        WeakReference<Dashboard_Collection_Fragment> wr;
        Dashboard_Collection_Fragment newCurrClass;

        public MyHandler(Dashboard_Collection_Fragment currClass) {
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

    private void setData() {
        switch (CURRENT_DATA) {
            case GET_DATA_DOWNLOAD:
                tvEmpty.setVisibility(View.GONE);
                pbLoader.setVisibility(View.VISIBLE);
                break;
            case GET_DATA_TRUE:
                pbLoader.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.GONE);
                if (lm != null) {
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
                if (lmOld == lm) {
                    CollDiffUtilCallback CollDiffUtil = new CollDiffUtilCallback(oldListColl, listColl);
                    DiffUtil.DiffResult CollDiffResult = DiffUtil.calculateDiff(CollDiffUtil);
                    cAdapter.swipeCursor(listColl);
                    CollDiffResult.dispatchUpdatesTo(cAdapter);
                } else {
                    cAdapter = new CollectionAdater(ctx, lay, listColl/*,this*/);
                    cAdapter.setItemClickListener(onItemClickListener);
                    rcVColl.setLayoutManager(lm);
                    rcVColl.setAdapter(cAdapter);
                }
                /*ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(cAdater);
                mItemTouchHelper = new ItemTouchHelper(callback);
                mItemTouchHelper.attachToRecyclerView(rcVColl);*/

                if(listColl.size() == 0){
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    //Обновляем RecyclerView
    public static class CollDiffUtilCallback extends DiffUtil.Callback {

        List<Room_DB.Collection> oldList;
        List<Room_DB.Collection> newList;

        CollDiffUtilCallback(List<Room_DB.Collection> oldList, List<Room_DB.Collection> newList) {
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
            fragTrans = requireActivity().getSupportFragmentManager().beginTransaction();
            Dialog_Add_Collection dialog_AC = new Dialog_Add_Collection(ctx);
            dialog_AC.show(fragTrans, "addColl");
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
            oldBtn.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
            oldBtn.setColorFilter(getResources().getColor(R.color.colorTextSecondary), PorterDuff.Mode.SRC_ATOP);
        }
        oldBtn = btn;
        btn.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
        btn.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onStop() {
        pref.saveTypeViewCollection(type_ListCollection);
        super.onStop();
    }



    @Override
    public void onDestroy() {
        Cleaner();
        super.onDestroy();
    }

    private void Cleaner() {
        if (h != null) h.removeCallbacksAndMessages(null);
        if (rcVColl != null) rcVColl.setAdapter(null);
        if (cAdapter != null) cAdapter = null;
        oldListColl = null;
        listColl = null;
    }
}
