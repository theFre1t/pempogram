package tfre1t.example.pempogram.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.fragment.dashboard.Dashboard_Collection_Fragment;


public class DashboardFragment extends Fragment{

    public Dashboard_Collection_Fragment DashCollFrg;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        DashCollFrg = new Dashboard_Collection_Fragment();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frmLayoutDashFrag, DashCollFrg).commit();

        return v;
    }

    @Override
    public void onDestroy() {
        getChildFragmentManager().beginTransaction().remove(DashCollFrg).commitAllowingStateLoss();
        super.onDestroy();
    }
}