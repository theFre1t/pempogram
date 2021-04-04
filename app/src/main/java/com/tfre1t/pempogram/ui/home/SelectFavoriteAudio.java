package com.tfre1t.pempogram.ui.home;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import com.tfre1t.pempogram.R;
import com.tfre1t.pempogram.database.Tables;
import com.tfre1t.pempogram.adapter.SelectFavAuAdapter;

public class SelectFavoriteAudio extends AppCompatActivity {
    private static final String TAG = "myLog";

    private static int CURRENT_DATA; //Текущее состояние данных
    private static final int DATA_NONE = 0; // Данных нет
    private static final int DATA_TRUE = 1; // Данные есть
    private static final int DATA_DOWNLOAD = 2; // Данные в загрузке

    private HomeViewModel homeViewModel;
    private SelectFavAuAdapter scAdapter;

    private Handler h;

    private List<Tables.AudiofileWithImg> oldListSelAu, listSelAu;

    private RecyclerView  rvSelectFavAu;
    private Toolbar tbSelectFavAu;
    private ProgressBar pbLoader;
    private TextView tvEmpty;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_favoriteaudio);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        h = new MyHandler(this);
        findViewById();
        setToolbar();
        loadData();
    }

    private void findViewById() {
        tbSelectFavAu = findViewById(R.id.tbSelectFavAu);
        rvSelectFavAu = findViewById(R.id.rvSelectFavAu);
        pbLoader = findViewById(R.id.pbLoader);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setToolbar() {
        setSupportActionBar(tbSelectFavAu);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_select_favaudio);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_select_favoriteaudio_menu,  menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    h.sendEmptyMessage(DATA_DOWNLOAD);
                    //Получаем данные
                    homeViewModel.getDataSelAu(newText).observe(SelectFavoriteAudio.this, new Observer<List<Tables.AudiofileWithImg>>() {
                        @Override
                        public void onChanged(List<Tables.AudiofileWithImg> list) {
                            if (listSelAu != null) {
                                oldListSelAu = listSelAu; //Запоминаем старые данные
                            }
                            listSelAu = list;
                            //Отправляем сообщение о наличие данных
                            h.sendEmptyMessage(DATA_TRUE);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(0);
                finish();
                return true;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    //Получение и установка данных
    private void loadData() {
        Log.d(TAG, "setData: rvSelectFavAu "+ rvSelectFavAu);
        h.sendEmptyMessage(DATA_DOWNLOAD);
        //Получаем данные
        homeViewModel.getDataSelAu().observe(SelectFavoriteAudio.this, new Observer<List<Tables.AudiofileWithImg>>() {
            @Override
            public void onChanged(List<Tables.AudiofileWithImg> list) {
                if (listSelAu != null) {
                    oldListSelAu = listSelAu; //Запоминаем старые данные
                }
                listSelAu = list;
                //Отправляем сообщение о наличие данных
                if (listSelAu.size() == 0) {
                    h.sendEmptyMessage(DATA_NONE);
                } else {
                    h.sendEmptyMessage(DATA_TRUE);
                }
            }
        });
    }

    static class MyHandler extends Handler {
        WeakReference<SelectFavoriteAudio> wr;
        SelectFavoriteAudio newCurrClass;

        public MyHandler(SelectFavoriteAudio currClass) {
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
                if(scAdapter == null){
                    scAdapter = new SelectFavAuAdapter(SelectFavoriteAudio.this, listSelAu);
                    scAdapter.setItemClickListener(onItemClickListener);
                    rvSelectFavAu.setLayoutManager(new LinearLayoutManager(SelectFavoriteAudio.this));
                    rvSelectFavAu.setAdapter(scAdapter);
                }else {
                    SelectFavAuDiffUtilCallback DiffUtilCallback = new SelectFavAuDiffUtilCallback(oldListSelAu, listSelAu);
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(DiffUtilCallback);
                    scAdapter.swipeData(listSelAu);
                    diffResult.dispatchUpdatesTo(scAdapter);
                }
                break;
        }
    }

    //Обновляем RecyclerView
    public static class SelectFavAuDiffUtilCallback extends DiffUtil.Callback{

        List<Tables.AudiofileWithImg> oldList;
        List<Tables.AudiofileWithImg> newList;

        SelectFavAuDiffUtilCallback(List<Tables.AudiofileWithImg> oldList, List<Tables.AudiofileWithImg> newList){
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

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            homeViewModel.addNewFavAu(v.getId());
            setResult(v.getId());
            finish();
        }
    };

    @Override
    public void onDestroy() {
        Cleaner();
        super.onDestroy();
    }

    private void Cleaner(){
        if (h != null)
            h.removeCallbacksAndMessages(null);
        rvSelectFavAu.setAdapter(null);
        scAdapter = null;
        oldListSelAu = null;
        listSelAu = null;
    }
}
