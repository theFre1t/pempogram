package tfre1t.example.pempogram.ui.dashboard;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.fragment.dashboard.addsound.Fragment_InternalStorage;
import tfre1t.example.pempogram.fragment.dashboard.addsound.Fragment_LibrarySound;
import tfre1t.example.pempogram.fragment.dashboard.addsound.Fragment_RecordSound;
import tfre1t.example.pempogram.myadapter.AddSoundFragmentPagerAdapter;

public class Dashboard_Add_Sound extends AppCompatActivity {

    ViewPager2 vPagerAddSound;
    TabLayout tLayoAddSound;
    Toolbar tbAddSound;

    AddSoundFragmentPagerAdapter adapter;

    DB db;
    long id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sound);
        id = getIntent().getLongExtra("idColl", 0);
        onFindViewById();
        setToolbar();
        connectDB();
        onSetAdapter();
        onTabMediator();
        onSelected();
    }

    private void onFindViewById() {
        tLayoAddSound = findViewById(R.id.tLayoAddSound);
        vPagerAddSound = findViewById(R.id.vPagerAddSound);
        tbAddSound = findViewById(R.id.tbAddSound);
    }

    private void setToolbar() {
        setSupportActionBar(tbAddSound);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!= null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Добавление записи");
        }
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

    private void connectDB() {
        db = new DB(this);
        db.open();
    }

    private void onSetAdapter() {
        adapter = new AddSoundFragmentPagerAdapter(this, db, id);
        adapter.addFragment(new Fragment_LibrarySound(db, id));
        adapter.addFragment(new Fragment_RecordSound(db, id));
        adapter.addFragment(new Fragment_InternalStorage(db, id));
        vPagerAddSound.setAdapter(adapter);
    }

    private void onTabMediator()  {
        new TabLayoutMediator(tLayoAddSound, vPagerAddSound, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setIcon(R.drawable.baseline_playlist_add_24);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.baseline_mic_24);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.baseline_fiber_new_24);
                        break;
                }
            }
        }).attach();
    }

    private void onSelected() {
        TabLayout.Tab tab = tLayoAddSound.getTabAt(tLayoAddSound.getSelectedTabPosition());
        tab.getIcon().setColorFilter(getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
        tLayoAddSound.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
