package tfre1t.example.pempogram.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.fragment.dashboard.Dashboard_Collection_Fragment;


public class DashboardFragment extends Fragment{

    private DashboardViewModel dashboardViewModel;

    public Dashboard_Collection_Fragment DashCollFrg;
    FragmentTransaction fragmentTransaction;
    boolean DashFragState;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        DashCollFrg = new Dashboard_Collection_Fragment();
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frmLayoutDashFrag, DashCollFrg).commit();


        return v;
    }

    @Override
    public void onDestroy() {
        getFragmentManager().beginTransaction().remove(DashCollFrg).commitAllowingStateLoss();
        super.onDestroy();
    }
}