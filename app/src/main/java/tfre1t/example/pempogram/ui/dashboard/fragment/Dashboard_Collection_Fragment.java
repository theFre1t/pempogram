package tfre1t.example.pempogram.ui.dashboard.fragment;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.dialog.Dialog_Add_Collection;
import tfre1t.example.pempogram.myadapter.CollectionAdater;

public class Dashboard_Collection_Fragment extends Fragment {

    static final int DATA_NONE = 0; // Данных нет
    static final int DATA_TRUE = 1; // Данные есть
    static final int DATA_DOWNLOAD = 2; // Данные в загрузке
    static int CURRENT_DATA;

    DB db;
    Cursor cursor;
    String[] from;
    int[] to;
    int lay;

    Toolbar tbColl;
    AppCompatActivity activity;
    ActionBar actionBar;

    Handler h;
    ProgressBar pbColl;

    CollectionAdater cAdater;
    RecyclerView rcVColl;
    RecyclerView.LayoutManager lm;
    ImageButton btnListCard, btnListClassic, btnlistGrid;
    FloatingActionButton floatBtnAddColl;

    public static int type_ListCollection;
    Dashboard_SetSoundsCollection_Fragment DashSetSounCollFrag;
    FragmentTransaction fragTrans;
    Dialog_Add_Collection Dialog_AC;
    Fragment currentDialog = null;

    private static View v;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_dashboard_collectionlist, null);

        findViewById();

        db = new DB(v.getContext());
        db.open();

        h = new MyHandler(this);

        setToolbar();
        btnSetOnClick(v);
        loadData();
        return v;
    }

    private void findViewById() {
        btnListCard = v.findViewById(R.id.btnlistCard);
        btnListClassic = v.findViewById(R.id.btnlistClassic);
        btnlistGrid = v.findViewById(R.id.btnlistGrid);
        floatBtnAddColl= v.findViewById(R.id.floatBtnAddColl);
        rcVColl = v.findViewById(R.id.rcViewColl);
        pbColl = v.findViewById(R.id.pbColl);
        tbColl = v.findViewById(R.id.tbColl);
    }

    private void setToolbar() {
        activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(tbColl);
        actionBar = activity.getSupportActionBar();
        setHasOptionsMenu(true);
        if(actionBar!= null) {
            actionBar.setTitle("Collection");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.collection_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            /*case :
                return true*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setAdapter(View v) {
        switch (CURRENT_DATA){
            case DATA_DOWNLOAD:
                pbColl.setVisibility(View.VISIBLE);
                break;
            case DATA_NONE:
                pbColl.setVisibility(View.GONE);
                break;
            case DATA_TRUE:
                pbColl.setVisibility(View.GONE);
                if(type_ListCollection == 0){
                    //Отображение по классическому типу
                    setColorBtn(btnListClassic);
                    lay = R.layout.fragment_dashboard_collection_classiclist;
                    lm = new LinearLayoutManager(v.getContext());
                }
                else if(type_ListCollection == 1){
                    //Отображение карточого типа
                    setColorBtn(btnListCard);
                    lay = R.layout.fragment_dashboard_collection_cardlist;
                    lm = new LinearLayoutManager(v.getContext());
                }
                else if(type_ListCollection == 2){
                    //Отображение карточого типа в 2 колонки
                    setColorBtn(btnlistGrid);
                    lay = R.layout.fragment_dashboard_collection_cardgrid;
                    lm = new GridLayoutManager(v.getContext(), 2);
                }

                cAdater = new CollectionAdater(v.getContext(), lay, cursor, from, to);
                cAdater.setItemClickListener(onItemClickListener);
                rcVColl.setLayoutManager(lm);
                rcVColl.setAdapter(cAdater);
                    break;
        }
    }

    static ImageButton oldBtn;
    private void setColorBtn(ImageButton btn) {
        if(oldBtn != null){
            oldBtn.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.SRC_ATOP);
            oldBtn.setColorFilter(getResources().getColor(R.color.colorTextSecondary), PorterDuff.Mode.SRC_ATOP);
        }
        oldBtn = btn;
        btn.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
        btn.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
    }

    public void loadData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                h.sendEmptyMessage(DATA_DOWNLOAD);
                cursor = db.getAllDataCollection();
                if(cursor == null){
                    h.sendEmptyMessage(DATA_NONE);
                }
                else {
                    h.sendEmptyMessage(DATA_TRUE);
                }
            }
        }).start();
        from = new String[]{DB.COLUMN_ID_COLLECTION, DB.COLUMN_NAME_COLLECTION, DB.COLUMN_AUTHOR_COLLECTION, DB.COLUMN_IMG_COLLECTION};
        to = new int[]{R.id.tvCollection, R.id.tvAuthor, R.id.imgCollection};
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            long id = view.getId();
            DashSetSounCollFrag = new Dashboard_SetSoundsCollection_Fragment(id);
            fragTrans = getFragmentManager().beginTransaction();
            fragTrans.replace(R.id.frmLayoutDashFrag, DashSetSounCollFrag);
            fragTrans.addToBackStack(null);
            fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragTrans.commit();
        }
    };

    private void btnSetOnClick(View v) {
        btnListClassic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type_ListCollection != 0) {
                    type_ListCollection = 0;
                    setAdapter(v);
                }
            }
        });
        btnListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type_ListCollection != 1) {
                    type_ListCollection = 1;
                    setAdapter(v);
                }
            }
        });
        btnlistGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type_ListCollection != 2) {
                    type_ListCollection = 2;
                    setAdapter(v);
                }
            }
        });

        floatBtnAddColl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragTrans = getFragmentManager().beginTransaction();
                Dialog_AC = new Dialog_Add_Collection(db, Dashboard_Collection_Fragment.this);
                currentDialog = Dialog_AC;
                fragTrans.add(R.id.frmLayoutDashFrag, Dialog_AC).disallowAddToBackStack().commit();
            }
        });
    }

    static class MyHandler extends Handler {
        WeakReference<Dashboard_Collection_Fragment> wrDashcollfrag;
        Dashboard_Collection_Fragment newDashcollfrag;

        public MyHandler(Dashboard_Collection_Fragment dashcollfrag) {
            wrDashcollfrag = new WeakReference<Dashboard_Collection_Fragment>(dashcollfrag);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            newDashcollfrag = wrDashcollfrag.get();
            if(newDashcollfrag != null){
                switch (msg.what){
                    case DATA_DOWNLOAD:
                        CURRENT_DATA = DATA_DOWNLOAD;
                        newDashcollfrag.setAdapter(newDashcollfrag.getView());
                        break;
                    case DATA_NONE:
                        CURRENT_DATA = DATA_NONE;
                        newDashcollfrag.setAdapter(newDashcollfrag.getView());
                        break;
                    case DATA_TRUE:
                        CURRENT_DATA = DATA_TRUE;
                        newDashcollfrag.setAdapter(newDashcollfrag.getView());
                        break;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1){
            loadData();
        }
    }

    @Override
    public void onDestroy() {
        db.close();
        if (h != null)
            h.removeCallbacksAndMessages(null);
        if(currentDialog != null) {
            getFragmentManager().beginTransaction().remove(currentDialog).commitAllowingStateLoss();
        }
        super.onDestroy();
    }
}
