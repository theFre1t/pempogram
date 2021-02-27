package tfre1t.example.pempogram.ui.dashboard;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import tfre1t.example.pempogram.BottomSheet.DialogFragment.bsOnlineLibrary;
import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.Tables;
import tfre1t.example.pempogram.adapter.OnlineLibraryAdapter;
import tfre1t.example.pempogram.dialog.Dialog_Delete_Collecton;

public class OnlineLibrary extends AppCompatActivity {
    private static final String TAG = "myLog";

    private static int CURRENT_DATA; //Текущее состояние данных
    private static final int DATA_NONE = 0; // Данных нет
    private static final int DATA_TRUE = 1; // Данные есть
    private static final int DATA_DOWNLOAD = 2; // Данные в загрузке

    private DashboardViewModel dashboardViewModel;
    private OnlineLibraryAdapter olAdapter;

    private Handler h;

    private List<Tables.Online_Collection> oldListColl, listColl;
    private List<Tables.Online_Audiofile> oldListAud, listAud;

    private RecyclerView rvOnlineLibrary;
    private Toolbar tbOnlineLibrary;
    private ProgressBar pbLoader;
    private TextView tvEmpty;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_library);
        //dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        findViewById();
        setToolbar();
        loadData();
    }

    private void findViewById() {
        tbOnlineLibrary = findViewById(R.id.tbOnlineLibrary);
        rvOnlineLibrary = findViewById(R.id.rvOnlineLibrary);
        pbLoader = findViewById(R.id.pbLoader);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setToolbar() {
        setSupportActionBar(tbOnlineLibrary);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_online_library);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_online_library_menu,  menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            /**
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    h.sendEmptyMessage(DATA_DOWNLOAD);
                    //Получаем данные
                    dashboardViewModel.getDataSelAu(newText).observe(this, new Observer<List<Tables.AudiofileWithImg>>() {
                        @Override
                        public void onChanged(List<Tables.AudiofileWithImg> list) {
                            if (listSelAu != null) {
                                oldListSelAu = listSelAu; //Запоминаем старые данные
                            }
                            listSelAu = list;
                            //Отправляем сообщение о наличие данных
                            h.sendEmptyMessage(DATA_TRUE);
                        }
                    });
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            };
             **/
            searchView.setOnQueryTextListener(queryTextListener);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(0);
                finish();
                return true;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    //Получение и установка данных
    private void loadData() {
        //Log.d(TAG, "setData: rvOnlineLibrary "+ rvOnlineLibrary);
        h = new MyHandler(this);
        h.sendEmptyMessage(DATA_DOWNLOAD);
        listColl = new ArrayList<>();
        listAud = new ArrayList<>();

        //Получаем данные
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray json_arr = getJsonContent("https://yadi.sk/d/VXunsH1ZDcsz0Q");
                    for (int i = 0; i < json_arr.length(); i++) {
                        JSONObject obj = json_arr.getJSONObject(i);
                        String type = obj.getString("type");

                        if(type.equals("dir")){
                            String public_url = obj.getString("public_url");
                            String[] name_author = obj.getString("name").split("\\|",2);
                            int coll_revision = obj.getInt("revision");

                            Tables.Online_Collection collection = null;
                            Tables.Online_Audiofile audiofile = null;

                            //Log.d(TAG, "OnlineLibrary:\nname:" + name_author[0] + "\nauthor: "+ name_author[1] + "\ntype: "+ type +"\npublic_url: " + public_url);

                            JSONArray json_items_arr = getJsonContent(public_url);
                            for (int j = 0; j < Objects.requireNonNull(json_items_arr).length(); j++) {
                                JSONObject item_obj = json_items_arr.getJSONObject(j);
                                String item_media_type = item_obj.getString("media_type"); //Получаем тип ресурса

                                if(item_media_type.equals("image")){
                                    String item_img_file = item_obj.getString("file"); //Получаем ссылку на скачивание полной версии
                                    String item_img_preview = item_obj.getString("preview"); //Получаем ссылку на скачивание превью версии
                                    collection = new Tables.Online_Collection(coll_revision, name_author[0], name_author[1], item_img_file, item_img_preview); //Упаковываем
                                    //Log.d(TAG, "OnlineLibrary image:\nitem_img_file: " + item_img_file + "\nitem_img_preview: "+ item_img_preview);
                                }
                                else if(item_media_type.equals("audio")) {
                                    int item_revision = item_obj.getInt("revision"); //Получаем revision
                                    String[] item_name = item_obj.getString("name").split("\\.", 2); //Получаем имя аудио и разбиваем его на имя и формат
                                    String item_file_url = item_obj.getString("file"); //Получаем ссылку на скачивание
                                    audiofile = new Tables.Online_Audiofile(item_revision, item_name[0], item_file_url, coll_revision); //Упаковываем
                                    listAud.add(audiofile); //Добавляем в список аудио
                                    //Log.d(TAG, "OnlineLibrary audio:\nitem_resource_id: " + item_resource_id + "\nitem_name: "+ item_name[0] +"\nitem_file_url: " + item_file_url);
                                }
                            }
                            listColl.add(collection); //Добавляем в список наборов
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Отправляем сообщение о наличие данных
                if (listColl.size() == 0) {
                    h.sendEmptyMessage(DATA_NONE);
                } else {
                    h.sendEmptyMessage(DATA_TRUE);
                }
            }
        }).start();
    }

    private JSONArray getJsonContent(String public_key) {
        String pathYaDisk = "https://cloud-api.yandex.net/v1/disk/public/resources?public_key=";
        HttpsURLConnection connection;
        try {
            connection = (HttpsURLConnection) new URL(pathYaDisk + public_key).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setReadTimeout(10000);
            connection.connect();
            if (Objects.requireNonNull(connection).getResponseCode() == 200) {
                InputStream inStream = connection.getInputStream();
                InputStreamReader inStreamRead = new InputStreamReader(inStream);
                BufferedReader buffRead = new BufferedReader(inStreamRead);
                StringBuilder buff = new StringBuilder();
                String line;
                while ((line = buffRead.readLine()) != null) {
                    buff.append(line).append("\n");
                }
                //Закрываем все потоки
                buffRead.close();
                inStreamRead.close();
                inStream.close();

                JSONObject json_obj = new JSONObject(buff.toString());
                return json_obj.getJSONObject("_embedded").getJSONArray("items");
            } else {
                Log.d(TAG, "OnlineLibrary: ERROR " + Objects.requireNonNull(connection).getResponseCode());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class MyHandler extends Handler {
        WeakReference<OnlineLibrary> wr;
        OnlineLibrary newCurrClass;

        public MyHandler(OnlineLibrary currClass) {
            wr = new WeakReference<>(currClass);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            newCurrClass = wr.get();
            if(newCurrClass != null){
                CURRENT_DATA = msg.what;
                newCurrClass.setData();
            }
        }
    }

    private void setData(){
        pbLoader.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        switch (CURRENT_DATA) {
            case DATA_DOWNLOAD:
                pbLoader.setVisibility(View.VISIBLE);
                break;
            case DATA_NONE:
                tvEmpty.setVisibility(View.VISIBLE);
                break;
            case DATA_TRUE:
                if(olAdapter == null){
                    olAdapter = new OnlineLibraryAdapter(this, listColl);
                    olAdapter.setItemClickListener(onItemClickListener);
                    rvOnlineLibrary.setLayoutManager(new LinearLayoutManager(OnlineLibrary.this));
                    rvOnlineLibrary.setAdapter(olAdapter);
                }/**else {
                    tfre1t.example.pempogram.ui.home.SelectFavoriteAudio.SelectFavAuDiffUtilCallback DiffUtilCallback = new tfre1t.example.pempogram.ui.home.SelectFavoriteAudio.SelectFavAuDiffUtilCallback(oldListSelAu, listSelAu);
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(DiffUtilCallback);
                    scAdapter.swipeData(listSelAu);
                    diffResult.dispatchUpdatesTo(scAdapter);
                }
                 **/
                break;
        }
    }

    //Обновляем RecyclerView
    public static class SelectFavAuDiffUtilCallback extends DiffUtil.Callback{

        List<Tables.AudiofileWithImg> oldList;
        List<Tables.AudiofileWithImg> newList;

        SelectFavAuDiffUtilCallback(List<Tables.AudiofileWithImg> oldList, List<Tables.AudiofileWithImg> newList){
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            int newId = newList.get(newItemPosition).id_audiofile;
            int oldId = oldList.get(oldItemPosition).id_audiofile;
            return newId == oldId;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            String newName = newList.get(newItemPosition).name_audiofile;
            String oldName = oldList.get(oldItemPosition).name_audiofile;
            return oldName.equals(newName);
        }
    }

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new bsOnlineLibrary().show(getSupportFragmentManager().beginTransaction(), "showSetSound");
        }
    };

    @Override
    public void onDestroy() {
        Cleaner();
        super.onDestroy();
    }

    private void Cleaner() {
        if (h != null)
            h.removeCallbacksAndMessages(null);
        rvOnlineLibrary.setAdapter(null);
        FileUtils.deleteQuietly(FileUtils.getFile(getCacheDir().getPath()+"/picasso-cache"));
        /*scAdapter = null;
        oldListSelAu = null;
        listSelAu = null;*/
    }
}
