package tfre1t.example.pempogram.fragment;

import android.media.Image;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import tfre1t.example.pempogram.R;

public class InfinityLoadFragment extends Fragment {

    ImageView imgLoad;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_infinityload, null);
        imgLoad = v.findViewById(R.id.imgLoad);
        imgLoad.setImageResource(R.drawable.ic_home_black_24dp);
        return v;
    }
}
