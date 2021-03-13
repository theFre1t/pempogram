package tfre1t.example.pempogram.fragment.dashboard.addsound;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.adapter.AddSoundFragmentPagerAdapter;
import tfre1t.example.pempogram.ui.dashboard.DashboardViewModel;

public class Dashboard_Add_Sound extends AppCompatActivity {

    private DashboardViewModel dashboardViewModel;

    private ViewPager2 vPagerAddSound;
    private TabLayout tLayoAddSound;
    private Toolbar tbAddSound;

    private int id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sound);
        dashboardViewModel = new ViewModelProvider(Dashboard_Add_Sound.this).get(DashboardViewModel.class);

        id = getIntent().getIntExtra("idColl", 0);

        findViewById();
        setToolbar();
        onSetAdapter();
        onTabMediator();
        onSelected();
    }

    private void findViewById() {
        tLayoAddSound = findViewById(R.id.tLayoAddSound);
        vPagerAddSound = findViewById(R.id.vPagerAddSound);
        tbAddSound = findViewById(R.id.tbAddSound);
    }

    private void setToolbar() {
        setSupportActionBar(tbAddSound);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!= null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_adding);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSetAdapter() {
        dashboardViewModel.setIdCollection(id);
        AddSoundFragmentPagerAdapter adapter = new AddSoundFragmentPagerAdapter(this);
        adapter.addFragment(new Fragment_LibrarySound());
        adapter.addFragment(new Fragment_RecordSound());
        adapter.addFragment(new Fragment_InternalStorage());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Cleaner();
    }

    private void Cleaner(){
        vPagerAddSound.setAdapter(null);
    }
}
