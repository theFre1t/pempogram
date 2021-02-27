package tfre1t.example.pempogram.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class AddSoundFragmentPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> mFragmentList;

    public AddSoundFragmentPagerAdapter(FragmentActivity fa) {
        super(fa);
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
