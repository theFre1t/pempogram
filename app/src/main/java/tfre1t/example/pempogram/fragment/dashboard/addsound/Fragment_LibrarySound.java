package tfre1t.example.pempogram.fragment.dashboard.addsound;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.myadapter.LibrarySoundAdapter;

public class Fragment_LibrarySound extends Fragment implements View.OnClickListener {

    TextView tvTitle;
    RecyclerView rvLibSound;
    RecyclerView.LayoutManager lm;

    View v;
    DB db;
    long id;

    String[] from;
    int[] to;
    public LibrarySoundAdapter lsAdapter;
    Cursor cursor, cursorSC;

    Uri selectedAudio;

    public Fragment_LibrarySound(DB db, long i) {
        this.db = db;
        id = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_addsound_librarysound, null);
        onFindViewById();
        loadData();
        return v;
    }

    private void onFindViewById() {
        tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setText("Выберите аудиозаписи");
        rvLibSound = v.findViewById(R.id.rvLibSound);
        v.findViewById(R.id.btnAdd).setOnClickListener(this);
    }

    private void loadData() {
        new loadDataTask().execute();
    }

    class loadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            from = new String[]{DB.COLUMN_ID_AUDIOFILE, DB.COLUMN_IMG_COLLECTION, DB.COLUMN_NAME_AUDIOFILE, DB.COLUMN_EXECUTOR_AUDIOFILE};
            to = new int[]{R.id.imgAudiofile, R.id.tvNameAudio, R.id.tvExecutorAudio, R.id.chbSound};
            lm = new LinearLayoutManager(v.getContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            cursor = db.getAllDataAudiofile();
            cursorSC = db.getDataAudiofileByIdCollection(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            lsAdapter = new LibrarySoundAdapter(v.getContext(), R.layout.fragment_addsound_librarysound_classiclist, cursor, from, to, cursorSC);
            rvLibSound.setLayoutManager(lm);
            rvLibSound.setAdapter(lsAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                editCollection();
                Toast.makeText(v.getContext(), "Сохранено", Toast.LENGTH_SHORT).show();
                requireActivity().setResult(1);
                requireActivity().finish();
                break;
        }
    }

    boolean tied;

    private void editCollection() {
        for (LibrarySoundAdapter.Sound s : lsAdapter.getSounds()) {
            Cursor cur = db.getAudiofilesByIdAudifile(s.id);
            if (cur != null) {
                cur.moveToFirst();
                do {
                    try{
                        if (cur.getInt(cur.getColumnIndex(DB.COLUMN_IDCOLLECTION_COLLECTION_LEFT_IN)) == id) {
                            tied = true;
                            break;
                        }
                    } catch (IndexOutOfBoundsException ignored){ }
                    tied = false;
                } while (cur.moveToNext());
                if (s.check) {
                    if (!tied) {
                        db.addAudiofileInCollection_Left_In(id, s.id);
                    }
                } else {
                    if (tied) {
                        db.removeAudiofileInCollection_Left_In(id, s.id);
                    }
                }
            }
        }
    }


}
