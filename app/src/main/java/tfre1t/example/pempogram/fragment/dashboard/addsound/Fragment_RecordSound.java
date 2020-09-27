package tfre1t.example.pempogram.fragment.dashboard.addsound;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;

public class Fragment_RecordSound extends Fragment {

    View v;

    DB db;
    long id;

    Uri selectedAudio;

    public Fragment_RecordSound (DB db, long i) {
        this.db = db;
        id = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_addsound_recordsound, null);

        return v;
    }
}
