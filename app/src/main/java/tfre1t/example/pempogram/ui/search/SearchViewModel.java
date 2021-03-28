package tfre1t.example.pempogram.ui.search;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import tfre1t.example.pempogram.MediaPlayer.MyMediaPlayer;
import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.App;
import tfre1t.example.pempogram.database.Room_DB;
import tfre1t.example.pempogram.database.Tables;

public class SearchViewModel extends AndroidViewModel {

    private static final String TAG = "myLog";

    private static Room_DB.AppDatabase rdb;
    private static Room_DB.CollectionDao_abstract collectionDao_abstract;
    private static Room_DB.AudiofileDao_abstract audiofileDao_abstract;
    private static Room_DB.Online_CollectionDao onlineCollectionDao;
    private static Room_DB.Online_AudiofileDao onlineAudiofileDao;
    private static Room_DB.Online_CollectionDao_abstract onlineCollectionDao_abstract;
    private static Room_DB.Online_Collection_with_CollectionDao onlineCollectionWithCollectionDao;
    private static Room_DB.Online_AudiofileDao_abstract onlineAudiofileDao_abstract;
    private static Room_DB.Online_Audiofile_with_AudiofileDao onlineAudiofileWithAudiofileDao;

    private LiveData<List<Tables.Online_CollectionView>> dataOnlineCollList;
    private LiveData<Tables.Online_CollectionView> dataOnlineColl;
    private LiveData<List<Room_DB.Online_Audiofile>> onlineAudiofilesByIdCollList;

    private int online_collectionId;
    private int online_audiofileId;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        dbConnect();
    }

    private void dbConnect() {
        if(rdb == null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    rdb = App.getInstance().getDatabase();
                    collectionDao_abstract = rdb.collectionDaoAbstr();
                    audiofileDao_abstract = rdb.audiofileDaoAbstr();
                    onlineCollectionDao = rdb.onlineCollectionDao();
                    onlineAudiofileDao = rdb.onlineAudiofileDao();
                    onlineCollectionDao_abstract = rdb.onlineCollectionDaoAbstr();
                    onlineCollectionWithCollectionDao = rdb.onlineCollectionWithCollectionDao();
                    onlineAudiofileDao_abstract = rdb.onlineAudiofileDaoAbstr();
                    onlineAudiofileWithAudiofileDao = rdb.onlineAudiofileWithAudiofileDao();
                }
            }).start();
        }
    }

    //OnlineLibrary//==================================================================================
    /**Добавление нового Набора*/
    public void Online_AddUpdColl(long revision, String name_coll, String author_coll){
        new Thread(() -> onlineCollectionDao_abstract.insUpd(revision, name_coll, author_coll)).start();
    }

    /**Добавляем аудиозапись(с автопривязкой к Набору)*/
    public void Online_AddUpdAudiofile(long rev_id, String name, String author, String mimeType, String file_url, long coll_rev){
        new Thread(() -> onlineAudiofileDao_abstract.insUpd(rev_id,name,author,mimeType,file_url,coll_rev)).start();
    }

    /**Добавление изображения Набора*/
    public void Online_AddUpdImgColl(long revision, String img_file, String img_preview) {
        new Thread(() -> onlineCollectionDao_abstract.updateImage(getApplication(), revision, img_file, img_preview)).start();
    }

    /**Получение списка Наборов*/
    public LiveData<List<Tables.Online_CollectionView>> Online_GetDataColl() {
        dataOnlineCollList = onlineCollectionDao.getAllWichAddLive();
        return dataOnlineCollList;
    }

    /**Получение списка Наборов по букве/слову/предложению???*/
    public LiveData<List<Tables.Online_CollectionView>> Online_GetDataColl(String searchText) {
        dataOnlineCollList = onlineCollectionDao.searchOnlineCollectionLive("%" + searchText + "%");
        return dataOnlineCollList;
    }

    /**Добавление нового Набора из Онлайн библиотеки*/
    public LiveData<Boolean> addNewCollFromOnline() {
        MutableLiveData<Boolean> status = new MutableLiveData<>();
        status.setValue(false);

        new Thread(() -> {
            Tables.Online_CollectionView onlineCollection = onlineCollectionDao.getById(online_collectionId);

            if (onlineCollection != null) {
                int idCollection = collectionDao_abstract.insertOnlineCollection(getApplication(), onlineCollection.Online_Collection);
                onlineCollectionWithCollectionDao.insert(new Room_DB.Online_Collection_with_Collection(onlineCollection.Online_Collection.id_online_collection, idCollection)); //Создаем связь между онлайн набором и локальным набором

                List<Room_DB.Online_Audiofile> onlineAudiofileList = onlineAudiofileDao.getAllByIdCollection(onlineCollection.Online_Collection.id_online_collection);
                if (!onlineAudiofileList.isEmpty()) {
                    for (Room_DB.Online_Audiofile onlineAudiofile : onlineAudiofileList) {
                        int idAudiofile = audiofileDao_abstract.insertOnlineAudiofile(getApplication(), onlineAudiofile, idCollection);
                        onlineAudiofileWithAudiofileDao.insert(new Room_DB.Online_Audiofile_with_Audiofile(onlineAudiofile.id_online_audiofile, idAudiofile));
                    }
                }
                status.postValue(true);
            }
        }).start();
        return status;
    }

    /**<p>Запрос на получение данных о конкретном Наборе</p>
     * <p>Метод для получения данных  {@link #Online_GetDataSelectedColl()}</p>*/
    public void OnlineLibrary_SelectCollById(int id){
        online_collectionId = id;
    }

    ///////////////////////////////////////=bsOnlineLibrary=////////////////////////////////////////

    /**Получение данных о конкретном Онлайн Наборе*/
    public LiveData<Tables.Online_CollectionView> Online_GetDataSelectedColl() {
        dataOnlineColl = onlineCollectionDao.getByIdLive(online_collectionId);
        return dataOnlineColl;
    }

    /**Получение списка аудиозаписей выбранного Онлайн Набора*/
    public LiveData<List<Room_DB.Online_Audiofile>> Online_GetAudiofilesSelectedColl(){
        onlineAudiofilesByIdCollList = onlineAudiofileDao.getAllByIdCollectionLive(online_collectionId);
        return onlineAudiofilesByIdCollList;
    }

    /**Проигрывание Онлайн аудиозаписи*/
    public void OnlineLibrary_PlayAudio(MyMediaPlayer myMediaPlayer, int id) {
        new Thread(() -> myMediaPlayer.playURL(onlineAudiofileDao.getNonLiveById(id).audiofile)).start();
    }
}