package tfre1t.example.pempogram.ui.search;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import tfre1t.example.pempogram.BottomSheet.DialogFragment.bsSearch;
import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.TrashcanClasses.GetHeightClass;
import tfre1t.example.pempogram.adapter.SearchAdapter;
import tfre1t.example.pempogram.database.Tables;

public class SearchFragment extends Fragment {
    private static final String TAG = "myLog";

    private static int CURRENT_DATA; //Текущее состояние данных
    private static final int DATA_NONE = 0; // Данных нет
    private static final int DATA_TRUE = 1; // Данные есть
    private static final int DATA_DOWNLOAD = 2; // Данные в загрузке

    private SearchViewModel searchViewModel;
    private SearchAdapter olAdapter;

    private Handler h;
    private View v;
    private Context ctx;

    private List<Tables.Online_CollectionView> oldListColl, listColl;

    private RecyclerView rvOnlineLibrary;
    private Toolbar tbOnlineLibrary;
    private ProgressBar pbLoader;
    private TextView tvEmpty;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, null);
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        ctx = v.getContext();

        findViewById();
        setToolbar();
        loadData();
        return v;
    }

    private void findViewById() {
        tbOnlineLibrary = v.findViewById(R.id.tbOnlineLibrary);
        rvOnlineLibrary = v.findViewById(R.id.rvOnlineLibrary);
        pbLoader = v.findViewById(R.id.pbLoader);
        tvEmpty = v.findViewById(R.id.tvEmpty);
    }

    private void setToolbar() {
        new GetHeightClass().setPadding(requireActivity(), tbOnlineLibrary);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Objects.requireNonNull(activity).setSupportActionBar(tbOnlineLibrary);
        ActionBar actionBar = activity.getSupportActionBar();
        setHasOptionsMenu(true);
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_online_search);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        requireActivity().getMenuInflater().inflate(R.menu.toolbar_online_library_menu,  menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    h.sendEmptyMessage(DATA_DOWNLOAD);
                    //Получаем данные
                    searchViewModel.Online_GetDataColl(newText).observe(getViewLifecycleOwner(), new Observer<List<Tables.Online_CollectionView>>() {
                        @Override
                        public void onChanged(List<Tables.Online_CollectionView> list) {
                            if (listColl != null) {
                                oldListColl = listColl; //Запоминаем старые данные
                            }

                            listColl = list;
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
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    //Получение и установка данных
    private void loadData() {
        updateLibrary();

        h = new MyHandler(this);
        h.sendEmptyMessage(DATA_DOWNLOAD);
        //Получаем данные
        searchViewModel.Online_GetDataColl().observe(getViewLifecycleOwner(), new Observer<List<Tables.Online_CollectionView>>() {
            @Override
            public void onChanged(List<Tables.Online_CollectionView> list) {
                if (listColl != null) {
                    oldListColl = listColl; //Запоминаем старые данные
                }
                listColl = list;
                //Отправляем сообщение о наличие данных
                if (listColl.size() == 0) {
                    h.sendEmptyMessage(DATA_NONE);
                } else {
                    h.sendEmptyMessage(DATA_TRUE);
                }
            }
        });
    }

    private void updateLibrary() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray json_arr = getJsonData("https://yadi.sk/d/VXunsH1ZDcsz0Q");
                    for (int i = 0; i < json_arr.length(); i++) {
                        JSONObject obj = json_arr.getJSONObject(i);
                        String type = obj.getString("type");

                        if(type.equals("dir")){
                            String public_url = obj.getString("public_url");
                            String[] name_author = obj.getString("name").split("\\|",2);
                            long coll_revision = obj.getLong("revision");

                            searchViewModel.Online_AddUpdColl(coll_revision, name_author[0], name_author[1]); //Добавляем/Обновляем в БД набор

                            JSONArray json_items_arr = getJsonData(public_url);
                            for (int j = 0; j < Objects.requireNonNull(json_items_arr).length(); j++) {
                                JSONObject item_obj = json_items_arr.getJSONObject(j);
                                String item_media_type = item_obj.getString("media_type"); //Получаем тип ресурса

                                if(item_media_type.equals("image")){
                                    String item_img_file = item_obj.getString("file"); //Получаем ссылку на скачивание полной версии
                                    String item_img_preview = item_obj.getString("preview"); //Получаем ссылку на скачивание превью версии

                                    searchViewModel.Online_AddUpdImgColl(coll_revision, item_img_file, item_img_preview); //Добавляем/Обновляем в БД изображение для набора
                                }
                                else if(item_media_type.equals("audio")) {
                                    long item_revision = item_obj.getLong("revision"); //Получаем revision
                                    String[] item_fullname = item_obj.getString("name").split("\\.", 2); //Получаем имя аудио и разбиваем его на имя и формат
                                    String[] item_name = item_fullname[0].split("\\|",2);
                                    String item_file_url = item_obj.getString("file"); //Получаем ссылку на скачивание

                                    searchViewModel.Online_AddUpdAudiofile(item_revision, item_name[0], item_name[1], item_fullname[1], item_file_url, coll_revision); //Добавляем/Обновляем в БД аудио
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private JSONArray getJsonData(String public_key) {
        String pathYaDisk = "https://cloud-api.yandex.net/v1/disk/public/resources?public_key=";
        HttpsURLConnection connection;
        try {
            connection = (HttpsURLConnection) new URL(pathYaDisk + public_key).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "OAuth ");
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
        WeakReference<SearchFragment> wr;
        SearchFragment newCurrClass;

        public MyHandler(SearchFragment currClass) {
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
                    olAdapter = new SearchAdapter(ctx, listColl);
                    olAdapter.setItemClickListener(onItemClickListener);
                    olAdapter.setAddItemClickListener(onAddItemClickListener);
                    rvOnlineLibrary.setLayoutManager(new LinearLayoutManager(ctx));
                    rvOnlineLibrary.setAdapter(olAdapter);
                }else {
                    SearchFragment.DiffUtilCallback DiffUtilCallback = new SearchFragment.DiffUtilCallback(oldListColl, listColl);
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(DiffUtilCallback);
                    olAdapter.swipeData(listColl);
                    diffResult.dispatchUpdatesTo(olAdapter);
                }
                break;
        }
    }

    //Обновляем RecyclerView
    public static class DiffUtilCallback extends DiffUtil.Callback{

        List<Tables.Online_CollectionView> oldList;
        List<Tables.Online_CollectionView> newList;

        DiffUtilCallback(List<Tables.Online_CollectionView> oldList, List<Tables.Online_CollectionView> newList){
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
            long newRevision = newList.get(newItemPosition).Online_Collection.revision_collection;
            long oldRevision = oldList.get(oldItemPosition).Online_Collection.revision_collection;
            return newRevision == oldRevision;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            boolean newAdd = newList.get(newItemPosition).collectionWithCollection != null;
            boolean oldAdd = oldList.get(newItemPosition).collectionWithCollection != null;
            String newName = newList.get(newItemPosition).Online_Collection.name_collection;
            String oldName = oldList.get(oldItemPosition).Online_Collection.name_collection;
            String newAuthor = newList.get(newItemPosition).Online_Collection.author_collection;
            String oldAuthor = oldList.get(oldItemPosition).Online_Collection.author_collection;
            String newImage = newList.get(newItemPosition).Online_Collection.img_file_preview_collection;
            String oldImage = oldList.get(oldItemPosition).Online_Collection.img_file_preview_collection;
            if(newImage == null && newImage == oldImage){
                return oldName.equals(newName) && oldAuthor.equals(newAuthor) && newAdd == oldAdd;
            }
            return oldName.equals(newName) && oldAuthor.equals(newAuthor) && newImage.equals(oldImage) && newAdd == oldAdd;
        }
    }

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchViewModel.OnlineLibrary_SelectCollById(v.getId());
            new bsSearch().show(requireActivity().getSupportFragmentManager().beginTransaction(), "showSetSound");
        }
    };

    private final View.OnClickListener onAddItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchViewModel.OnlineLibrary_SelectCollById(v.getId());
            searchViewModel.addNewCollFromOnline();
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
        olAdapter = null;
        oldListColl = null;
        listColl = null;
    }
}
