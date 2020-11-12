package tfre1t.example.pempogram.ui.home;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB_Table;
import tfre1t.example.pempogram.myadapter.SelectFavAuAdapter;

public class SelectFavoriteAudio extends AppCompatActivity {

    private HomeViewModel homeViewModel;

    private List<DB_Table.AudiofileWithImg> listSelAu;

    private RecyclerView  rvSelectFavAu;
    private Toolbar tbSelectFavAu;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_favoriteaudio);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        findViewById();
        setToolbar();
        loadData();
    }

    private void findViewById() {
        tbSelectFavAu = findViewById(R.id.tbSelectFavAu);
        rvSelectFavAu = findViewById(R.id.rvSelectFavAu);
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
                    //h.sendEmptyMessage(DATA_DOWNLOAD);
                    //Получаем данные
                    homeViewModel.getDataSelAu(newText).observe(SelectFavoriteAudio.this, new Observer<List<DB_Table.AudiofileWithImg>>() {
                        @Override
                        public void onChanged(List<DB_Table.AudiofileWithImg> list) {
                            listSelAu = list;
                            setAdapter();
                        }
                    });
                    /*dashboardViewModel.getDataColl().observe(getViewLifecycleOwner(), new Observer<List<Room_DB.Collection>>() {
                        @Override
                        public void onChanged(List<Room_DB.Collection> list) {
                            if (listColl != null) {
                                //Запоминаем старые данные
                                oldListColl = listColl;
                            }
                            listColl = list;
                            //Отправляем сообщение о наличие данных
                            h.sendEmptyMessage(DATA_TRUE);
                        }
                    });*/
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

    private void loadData() {
        homeViewModel.getDataSelAu().observe(this, new Observer<List<DB_Table.AudiofileWithImg>>() {
            @Override
            public void onChanged(List<DB_Table.AudiofileWithImg> list) {
                listSelAu = list;
                setAdapter();
            }
        });
    }

    private void setAdapter() {
        RecyclerView.LayoutManager lm = new LinearLayoutManager(SelectFavoriteAudio.this);
        SelectFavAuAdapter scAdapter = new SelectFavAuAdapter(SelectFavoriteAudio.this, listSelAu);
        scAdapter.setItemClickListener(onItemClickListener);
        rvSelectFavAu.setLayoutManager(lm);
        rvSelectFavAu.setAdapter(scAdapter);
    }

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            homeViewModel.addNewFavAu(v.getId());
            setResult(v.getId());
            finish();
        }
    };
}
