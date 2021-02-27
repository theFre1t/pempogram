package tfre1t.example.pempogram.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.HashMap;
import java.util.List;

import tfre1t.example.pempogram.database.App;
import tfre1t.example.pempogram.database.Tables;
import tfre1t.example.pempogram.database.Room_DB;
import tfre1t.example.pempogram.MediaPlayer.MyMediaPlayer;
import tfre1t.example.pempogram.adapter.LibrarySoundAdapter;

public class DashboardViewModel extends AndroidViewModel {
    private static final String TAG = "myLog";

    private static Room_DB.AppDatabase rdb;
    private static Room_DB.CollectionDao collectionDao;
    private static Room_DB.AudiofileDao audiofileDao;
    private static Room_DB.CollectionDao_abstract collectionDao_abstract;
    private static Room_DB.AudiofileDao_abstract audiofileDao_abstract;
    private static Room_DB.Collection_left_inDao collectionLeftInDao;

    private LiveData<Room_DB.Collection> dataCollById;
    private LiveData<List<Room_DB.Collection>> dataColl;
    private LiveData<List<Tables.AudiofileFull>> audiofilesByIdColl;
    private LiveData<Tables.AudiofileWithImg> dataAudiofileById;
    private LiveData<List<Tables.AudiofileWithImg>> dataAudiofiles;

    private int collectionId;
    private int audiofileId;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        dbConnect();
    }

    private void dbConnect() {
        if(rdb == null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    rdb = App.getInstance().getDatabase();
                    collectionDao = rdb.collectionDao();
                    audiofileDao = rdb.audiofileDao();
                    collectionDao_abstract = rdb.collectionDaoAbstr();
                    audiofileDao_abstract = rdb.audiofileDaoAbstr();
                    collectionLeftInDao = rdb.collectionLeftInDao();
                }
            }).start();
        }
    }

    //Dashboard_Collection_Fragment//==================================================================
    /**Получение списка Наборов*/
    public LiveData<List<Room_DB.Collection>> getDataColl(){
        dataColl = collectionDao.getAll();
        return dataColl;
    }

    /**Получение списка Наборов по букве/слову/предложению???*/
    public LiveData<List<Room_DB.Collection>> getDataColl(String searchText){
        return collectionDao.searchCollection("%"+searchText+"%");
    }

    /**<p>Запрос на получение данных о конкретном Наборе</p>
     * <p>Метод для получения данных  {@link #getDataSelectedColl()}</p>*/
    public void selectCollById(int id){
        collectionId = id;
    }

    ////////////////////////////////////=Dialog_Add_Collection=/////////////////////////////////////
    /**Добавление нового Набора*/
    public void addNewColl(String nameColl, String authorColl, String imgName){
        Room_DB.Collection collection = new Room_DB.Collection();
        collection.name_collection = nameColl;
        collection.author_collection = authorColl;
        collection.img_collection = imgName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                collectionDao_abstract.insert(collection);
            }
        }).start();
    }

    //Dashboard_SetSoundsCollection_Fragment//=========================================================
    /**Получение данных о конкретном Наборе*/
    public LiveData<Room_DB.Collection> getDataSelectedColl(){
        return collectionDao.getById(collectionId);
    }

    /**Получение списка аудиозаписей выбранного Набора*/
    public LiveData<List<Tables.AudiofileFull>> getAudiofilesSelectedColl(){
        audiofilesByIdColl = audiofileDao.getAllByIdCollection(collectionId);
        return audiofilesByIdColl;
    }

    /**<p>Запрос на получение данных о конкретной аудиозаписи</p>
     * <p>Метод для получения данных {@link #getDataSelectedAudio()}</p>*/
    public void selectAudiofileById(int id){
        audiofileId = id;
        dataAudiofileById = audiofileDao.getById(audiofileId);
    }

    public int getIdCollection(){
        return collectionId;
    }

    /**Проигрывание аудиозаписи*/
    public void playAudio(MyMediaPlayer myMediaPlayer, int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myMediaPlayer.play(getApplication(), audiofileDao.getNonLiveById(id).audiofile);
            }
        }).start();
    }

    ////////////////////////////////////=Dialog_Edit_Collection=////////////////////////////////////
    /**Обновление данных о Наборе*/
    public void updateCollection(String newNameColl, String newAuthorColl, String newNameImg){
        Room_DB.Collection collection = new Room_DB.Collection();
        collection.id_collection = collectionId;
        collection.name_collection = newNameColl;
        collection.author_collection = newAuthorColl;
        collection.img_collection = newNameImg;
        new Thread(new Runnable() {
            @Override
            public void run() {
                collectionDao.update(collection);
            }
        }).start();
    }

    ////////////////////////////////////=Dialog_Delete_Collecton=///////////////////////////////////
    /**Удаление Набора*/
    public void deleteCollection(boolean full){
        new Thread(new Runnable() {
            @Override
            public void run() {
                collectionDao_abstract.delete(getApplication(), dataCollById.getValue(), full);
            }
        }).start();
    }

    ///////////////////////////////////////=Dialog_Edit_Sound=//////////////////////////////////////
    /**Получение данных о конкретном Наборе*/
    public LiveData<Tables.AudiofileWithImg> getDataSelectedAudio(){
        return dataAudiofileById;
    }

    /**Обновление данных о Наборе*/
    public void updateAudiofile(String newNameSound, String newExecutorSound){
        new Thread(new Runnable() {
            @Override
            public void run() {
                audiofileDao.update(audiofileId, newNameSound, newExecutorSound);
            }
        }).start();
    }

    //////////////////////////////////////=Dialog_Delete_Sound=/////////////////////////////////////
    /**Удаление аудиозаписи*/
    public void deleteAudiofile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Room_DB.Audiofile audiofile = new Room_DB.Audiofile();
                Tables.AudiofileWithImg audiofileWithImg = audiofileDao.getNonLiveById(audiofileId);
                audiofile.id_audiofile = audiofileWithImg.id_audiofile;
                audiofile.name_audiofile = audiofileWithImg.name_audiofile;
                audiofileDao_abstract.delete(getApplication(), audiofile);
            }
        }).start();
    }


    //Dashboard_Add_sound//============================================================================
    /**Передаем id Набора*/
    public void setIdCollection(int id){
        collectionId = id;
    }

    ////////////////////////////////////=Fragment_LibrarySound=/////////////////////////////////////
    /**Получем все аудиозаписи*/
    public LiveData<List<Tables.AudiofileWithImg>> getAllAudiofiles(){
        dataAudiofiles = audiofileDao.getAll();
        return dataAudiofiles;
    }

    /**Получаем аудиозаписи привязанные к Набору*/
    public LiveData<List<Tables.AudiofileFull>> getAudiofilesByIdColl(){
        audiofilesByIdColl = audiofileDao.getAllByIdCollection(collectionId);
        return audiofilesByIdColl;
    }

    /**Передаем ArrayList для Добавления/Удаления привязки аудиозаписей с Набором*/
    public void editCollection(HashMap<Integer, LibrarySoundAdapter.Check> checkList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (LibrarySoundAdapter.Check check: checkList.values()) {

                    Room_DB.Collection_left_in collectionLeftIn = new Room_DB.Collection_left_in();
                    collectionLeftIn.id_audiofile = check.id;
                    collectionLeftIn.id_collection = collectionId;

                    if(check.check){
                        collectionLeftInDao.insert(collectionLeftIn);
                    }else {
                        collectionLeftInDao.delete(collectionId, check.id);
                    }
                }
            }
        }).start();
    }

    /////////////////////////////////////=Fragment_RecordSound=/////////////////////////////////////
    ///////////////////////////////////=Fragment_InternalStorage=///////////////////////////////////
    /**Добавляем аудиозапись(с автопривязкой к Набору)*/
    public void addNewAudiofile(String nameSound, String executorSound, String audiofile){
        Room_DB.Audiofile audio = new Room_DB.Audiofile();
        audio.name_audiofile = nameSound;
        audio.executor_audiofile = executorSound;
        audio.audiofile = audiofile;
        audio.id_collection = collectionId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                audiofileDao_abstract.insert(audio);
            }
        }).start();
    }

    //OnlineLibrary//==================================================================================
    /**Добавление нового Набора*/
    public void OnlineLibrary_AddNewColl(String nameColl, String authorColl, String imgName){
        Room_DB.Collection collection = new Room_DB.Collection();
        collection.name_collection = nameColl;
        collection.author_collection = authorColl;
        collection.img_collection = imgName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                collectionDao_abstract.insert(collection);
            }
        }).start();
    }

    /**Добавляем аудиозапись(с автопривязкой к Набору)*/
    public void OnlineLibrary_AddNewAudiofile(String nameSound, String executorSound, String audiofile){
        Room_DB.Audiofile audio = new Room_DB.Audiofile();
        audio.name_audiofile = nameSound;
        audio.executor_audiofile = executorSound;
        audio.audiofile = audiofile;
        audio.id_collection = collectionId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                audiofileDao_abstract.insert(audio);
            }
        }).start();
    }

    /**Получение списка Наборов*/
    public LiveData<List<Room_DB.Collection>> OnlineLibrary_GetDataColl(){
        dataColl = collectionDao.getAll();
        return dataColl;
    }

    /**Получение списка Наборов по букве/слову/предложению???*/
    public LiveData<List<Room_DB.Collection>> OnlineLibrary_GetDataColl(String searchText){
        return collectionDao.searchCollection("%"+searchText+"%");
    }

    /**<p>Запрос на получение данных о конкретном Наборе</p>
     * <p>Метод для получения данных  {@link #getDataSelectedColl()}</p>*/
    public void OnlineLibrary_SelectCollById(int id){
        collectionId = id;
    }

    ///////////////////////////////////////=bsOnlineLibrary=////////////////////////////////////////
    /**Получение данных о конкретном Наборе*/
    public LiveData<Room_DB.Collection> OnlineLibrary_GetDataSelectedColl(){
        return collectionDao.getById(collectionId);
    }

    /**Получение списка аудиозаписей выбранного Набора*/
    public LiveData<List<Tables.AudiofileFull>> OnlineLibrary_GetAudiofilesSelectedColl(){
        audiofilesByIdColl = audiofileDao.getAllByIdCollection(collectionId);
        return audiofilesByIdColl;
    }

    /**Проигрывание аудиозаписи*/
    public void OnlineLibrary_PlayAudio(MyMediaPlayer myMediaPlayer, int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myMediaPlayer.play(getApplication(), audiofileDao.getNonLiveById(id).audiofile);
            }
        }).start();
    }
 }