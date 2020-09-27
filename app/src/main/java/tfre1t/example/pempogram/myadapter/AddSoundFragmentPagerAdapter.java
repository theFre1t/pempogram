package tfre1t.example.pempogram.myadapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import tfre1t.example.pempogram.database.DB;

public class AddSoundFragmentPagerAdapter extends FragmentStateAdapter {

    private Context ctx;
    DB db;
    long id;

    ArrayList<Fragment> mFragmentList;

    public AddSoundFragmentPagerAdapter(FragmentActivity fa, DB db, long i) {
        super(fa);
        this.db = db;
        id = i;

        mFragmentList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);
    }
}
