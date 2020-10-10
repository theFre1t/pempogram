package tfre1t.example.pempogram.fragment.dashboard;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.FileInputStream;
import java.io.IOException;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.customviewers.RoundedImageView;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.dialog.Dialog_Delete_Collecton;
import tfre1t.example.pempogram.dialog.Dialog_Delete_Sound;
import tfre1t.example.pempogram.dialog.Dialog_Edit_Collection;
import tfre1t.example.pempogram.dialog.Dialog_Edit_Sound;
import tfre1t.example.pempogram.mediaplayer.MyMediaPlayer;
import tfre1t.example.pempogram.myadapter.SetSoundAdapter;

public class Dashboard_SetSoundsCollection_Fragment extends Fragment{

    private static final int CM_ID_EDIT = 1;
    private static final int CM_ID_DELETE = 2;
    AdapterContextMenuInfo acmi;

    Dialog_Edit_Sound Dialog_ES;
    Dialog_Delete_Collecton Dialog_DC;
    Dialog_Delete_Sound Dialog_DS;
    Dialog_Edit_Collection Dialog_EC;
    Fragment currentDialog = null;

    DB db;
    String[] from;
    int[] to;
    private final long id_collection;

    public SetSoundAdapter scAdapter;
    RecyclerView.LayoutManager lm;

    RecyclerView rvSetSounds;
    RoundedImageView imgVCollectionFront;
    ImageView imgVCollectionBack;
    TextView tvNameColl, tvAuthorColl;
    ImageButton imgBtnAddSound;

    Toolbar tbSetSound;
    CollapsingToolbarLayout collapsToolbarL;
    AppCompatActivity activity;
    ActionBar actionBar;
    FragmentTransaction fragTrans;

    private static View v;

    static MyMediaPlayer myMediaPlayer;

    public Dashboard_SetSoundsCollection_Fragment(long id) {
        id_collection = id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_dashboard_setsounds_collection_v2, null);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

        findViewByIdSetter();
        setToolbar();
        connectDB();
        loadPresenColl();
        loadData();
        setOnClick();
        return v;
    }

    private void findViewByIdSetter() {
        rvSetSounds = v.findViewById(R.id.rvSetSounds);
        imgBtnAddSound = v.findViewById(R.id.imgBtnAddSound);
        tbSetSound = v.findViewById(R.id.tbSetSound);
        collapsToolbarL = v.findViewById(R.id.collapsToolbarL);
    }

    private void setToolbar() {
        activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(tbSetSound);
        actionBar = activity.getSupportActionBar();
        setHasOptionsMenu(true);
        if(actionBar!= null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_setsound_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_menu_editColl:
                fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                Dialog_EC = new Dialog_Edit_Collection(db,Dashboard_SetSoundsCollection_Fragment.this, id_collection);
                Dialog_EC.show(fragTrans, "editColl");
                return true;
            case R.id.btn_menu_delColl:
                fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                Dialog_DC = new Dialog_Delete_Collecton(db,id_collection);
                Dialog_DC.show(fragTrans, "dellColl");
                return true;
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void connectDB() {
        db = new DB(v.getContext());
        db.open();
    }

    public void loadPresenColl() {
        new MyTaskPresenColl().execute();
    }

    public void loadData() {
        new MyTaskSetAdapter().execute();
    }

    private void setOnClick() {
        imgBtnAddSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.setsound.addsound");
                intent.putExtra("idColl", id_collection);
                startActivityForResult(intent, 1);
            }
        });
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (myMediaPlayer == null) {
                myMediaPlayer = new MyMediaPlayer();
            }
            myMediaPlayer.play(v.getContext(), db, v.getId());
        }
    };

    class MyTaskPresenColl extends AsyncTask<Void, Void, Void> {

        Cursor cursor_collection;
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imgVCollectionFront = v.findViewById(R.id.imgViewFrontImage);
            imgVCollectionBack = v.findViewById(R.id.imgViewBackImage);
            tvNameColl = v.findViewById(R.id.tvNameColl);
            tvAuthorColl = v.findViewById(R.id.tvAuthorColl);
        }

        @Override
        protected Void doInBackground(Void... params) {
            cursor_collection = db.getDataCollectionById(id_collection);
            cursor_collection.moveToFirst();
            try {
                FileInputStream fis = v.getContext().openFileInput(cursor_collection.getString(cursor_collection.getColumnIndex(DB.COLUMN_IMG_COLLECTION)));
                bitmap = BitmapFactory.decodeStream(fis);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            imgVCollectionBack.setImageBitmap(bitmap);
            imgVCollectionFront.setImageBitmap(bitmap);
            tvNameColl.setText(cursor_collection.getString(cursor_collection.getColumnIndex(DB.COLUMN_NAME_COLLECTION)));
            tvAuthorColl.setText(cursor_collection.getString(cursor_collection.getColumnIndex(DB.COLUMN_AUTHOR_COLLECTION)));
        }
    }

    class MyTaskSetAdapter extends AsyncTask<Void, Void, Void> {

        Cursor cursor_audiofile;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            from = new String[]{DB.COLUMN_IMG_COLLECTION, DB.COLUMN_NAME_AUDIOFILE, DB.COLUMN_EXECUTOR_AUDIOFILE};
            to = new int[]{R.id.imgAudiofile, R.id.tvAudiofile, R.id.tvAuthor, R.id.imgBtnPupupMenu};
            lm = new LinearLayoutManager(v.getContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            cursor_audiofile = db.getDataAudiofileByIdCollection(id_collection);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            scAdapter = new SetSoundAdapter(v.getContext(), R.layout.fragment_dashboard_setsounds_collection_classiclist, cursor_audiofile, from, to);
            scAdapter.setItemClickListener(onItemClickListener);
            scAdapter.setMenuClickListener(onMenuClickListener);
            rvSetSounds.setLayoutManager(lm);
            rvSetSounds.setAdapter(scAdapter);
            registerForContextMenu(rvSetSounds);
        }
    }

    private View.OnClickListener onMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.popup_setsound_menu);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    switch (item.getItemId()){
                        case R.id.btn_popup_editsound:
                            fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                            Dialog_ES = new Dialog_Edit_Sound(db,Dashboard_SetSoundsCollection_Fragment.this, v.getId());
                            Dialog_ES.show(fragTrans, "editSound");
                            return true;
                        case  R.id.btn_popup_deletesound:
                            fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                            Dialog_DS = new Dialog_Delete_Sound(db, Dashboard_SetSoundsCollection_Fragment.this, v.getId());
                            Dialog_DS.show(fragTrans, "dellSound");
                            return true;
                    }
                    return false;
                }
            });
            popup.show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1){
            loadData();
        }
    }

    @Override
    public void onDestroyView() {
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        db.close();
        if(myMediaPlayer != null) {
            myMediaPlayer.release();
        }
        if(currentDialog != null) {
            getFragmentManager().beginTransaction().remove(currentDialog).commitAllowingStateLoss();
        }
        super.onDestroy();
    }
}
