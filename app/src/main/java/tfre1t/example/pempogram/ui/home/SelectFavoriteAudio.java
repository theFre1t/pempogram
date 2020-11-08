package tfre1t.example.pempogram.ui.home;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(0);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
