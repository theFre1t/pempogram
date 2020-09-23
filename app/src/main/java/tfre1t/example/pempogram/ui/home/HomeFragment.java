package tfre1t.example.pempogram.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.mediaplayer.MyMediaPlayer;
import tfre1t.example.pempogram.myadapter.FavoriteAudioAdater;

public class HomeFragment extends Fragment {

    private int FAVAU_CURRENT = 0;
    private static final int FAVAU_DEFAULT = 0;
    private static final int FAVAU_REMOVE = 1;
    private static final int CODE_SELECT_AUDIO = 1;

    private HomeViewModel homeViewModel;

    private static View v;

    DB db;
    Cursor cFavorAudio;
    String[] from;
    int[] to;
    int lay;

    RecyclerView.LayoutManager lm;
    FavoriteAudioAdater favAuAdapter;
    ImageButton btnDellFavAu;

    RecyclerView rcVFavAu;

    static MyMediaPlayer myMediaPlayer;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        v = inflater.inflate(R.layout.fragment_home, container, false);
        dbConnect();
        findViewById();
        loadData();
        onClickSetter();
        return v;
    }

    private void dbConnect() {
        db = new DB(v.getContext());
        db.open();
    }

    private void findViewById() {
        rcVFavAu = v.findViewById(R.id.rcViewFavorAudio);
        btnDellFavAu = v.findViewById(R.id.btnDellFavAu);
    }

    private void loadData() {
        new loadDataTask().execute();
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            long id = view.getId();
            switch (FAVAU_CURRENT) {
                case FAVAU_DEFAULT:
                    Intent intent;
                    if (id == -1) {
                        intent = new Intent("android.intent.action.favoriteaudio.selectaudio");
                        startActivityForResult(intent, CODE_SELECT_AUDIO);
                    } else {
                        if (myMediaPlayer == null) {
                            myMediaPlayer = new MyMediaPlayer();
                        }
                        myMediaPlayer.play(v.getContext(), db, id);
                    }
                    break;
                case FAVAU_REMOVE:
                    view.setVisibility(View.GONE);
                    db.delRecFavoriteaudio(id);
                    break;
            }
        }
    };

    class loadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            from = new String[]{DB.COLUMN_ID_AUDIOFILE, DB.COLUMN_IMG_COLLECTION, DB.COLUMN_NAME_AUDIOFILE};
            to = new int[]{R.id.imgFavAu, R.id.tvNameAudio};
            lay = R.layout.fragment_home_favoriteaudio_cardgrid;
            lm = new GridLayoutManager(v.getContext(), 3);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            cFavorAudio = db.getAllDataAudiofileFromFavoriteaudio();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            favAuAdapter = new FavoriteAudioAdater(v.getContext(), lay, cFavorAudio, from, to, db);
            favAuAdapter.setItemClickListener(onItemClickListener);
            rcVFavAu.setLayoutManager(lm);
            rcVFavAu.setAdapter(favAuAdapter);
        }
    }

    private void onClickSetter() {
        btnDellFavAu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View vAdd;
                TextView tvRemove;
                switch (FAVAU_CURRENT){
                    case FAVAU_DEFAULT:
                        FAVAU_CURRENT = FAVAU_REMOVE;
                        btnDellFavAu.setImageResource(R.drawable.baseline_clear_24);
                        for(int i = favAuAdapter.getItemCount()-1; i>=0; i--){
                            vAdd = rcVFavAu.getChildAt(i);
                            if (vAdd.getId() == -1){
                                vAdd.setVisibility(View.GONE);
                            }
                            else {
                                tvRemove = vAdd.findViewById(R.id.tvRemove);
                                tvRemove.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case FAVAU_REMOVE:
                        FAVAU_CURRENT = FAVAU_DEFAULT;
                        btnDellFavAu.setImageResource(R.drawable.baseline_delete_24);
                        loadData();
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_SELECT_AUDIO:
                if (resultCode == 1) {
                    loadData();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        db.close();
        if(myMediaPlayer != null) {
            myMediaPlayer.release();
        }
        super.onDestroy();
    }
}