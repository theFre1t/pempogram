package tfre1t.example.pempogram.ui.home;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.myadapter.SetSoundAdapter;

public class SelectFavoriteAudio extends AppCompatActivity {

    DB db;

    String[] from;
    int[] to;
    public SetSoundAdapter scAdapter;
    ListView  lvSelectFavAu;
    Cursor cursor_audiofile;

    String title = "";
    Toolbar tbSelectFavAu;
    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_favoriteaudio);
        title = "Select Audio";
        setToolbar();
        connectDB();
        loadData();
        onClickSetter();
    }

    private void setToolbar() {
        tbSelectFavAu = findViewById(R.id.tbSelectFavAu);
        setSupportActionBar(tbSelectFavAu);
        actionBar = getSupportActionBar();
        if(actionBar!= null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_favoriteaudio_menu,  menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(0);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void connectDB() {
        db = new DB(this);
        db.open();
    }

    private void loadData() {
        new  loadDataTask().execute();
    }

    class loadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            from = new String[]{DB.COLUMN_IMG_COLLECTION, DB.COLUMN_NAME_AUDIOFILE, DB.COLUMN_EXECUTOR_AUDIOFILE};
            to = new int[]{R.id.imgAudiofile, R.id.tvNameAudio, R.id.tvExecutorAudio};
            lvSelectFavAu = findViewById(R.id.lvSelectFavAu);
        }

        @Override
        protected Void doInBackground(Void... params) {
            cursor_audiofile = db.getAllDataAudiofileNonFavAu();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            scAdapter = new SetSoundAdapter(SelectFavoriteAudio.this, R.layout.fragment_home_favoriteaudio_select_classiclist, cursor_audiofile, from, to, 0);
            lvSelectFavAu.setAdapter(scAdapter);
        }
    }

    private void onClickSetter() {
        lvSelectFavAu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long idAudiofile = id;
                db.addRecFavoriteaudio(idAudiofile);
                setResult(1);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
