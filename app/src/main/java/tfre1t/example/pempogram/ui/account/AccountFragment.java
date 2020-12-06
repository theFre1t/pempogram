package tfre1t.example.pempogram.ui.account;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.trashсanclasses.StatusBarHeight;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private AccountViewModel accountViewModel;

    private View v;
    private Context ctx;

    private CardView cardV_VK;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        v = inflater.inflate(R.layout.fragment_account, container, false);
        ctx = v.getContext();

        findViewById();
        return v;
    }

    private void findViewById() {
        v.findViewById(R.id.cardV_VK).setOnClickListener(this);

        View vStatusBar = v.findViewById(R.id.vStatusBar);
        vStatusBar.getLayoutParams().height = new StatusBarHeight().getStatusBarHeight(getActivity());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.cardV_VK){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/paizogram")));
        }
    }
}