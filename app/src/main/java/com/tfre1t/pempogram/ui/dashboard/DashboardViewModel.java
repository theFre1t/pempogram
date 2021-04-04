package com.tfre1t.pempogram.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.tfre1t.pempogram.database.App;
import com.tfre1t.pempogram.database.Tables;
import com.tfre1t.pempogram.database.Room_DB;
import com.tfre1t.pempogram.MediaPlayer.MyMediaPlayer;
import com.tfre1t.pempogram.adapter.LibrarySoundAdapter;

public class DashboardViewModel extends AndroidViewModel {
    private static final String TAG = "myLog";

    private static Room_DB.AppDatabase rdb;
    private static Room_DB.CollectionDao collectionDao;
    private static Room_DB.AudiofileDao audiofileDao;
    private static Room_DB.CollectionDao_abstract collectionDao_abstract;
    private static Room_DB.AudiofileDao_abstract audiofileDao_abstract;
    private static Room_DB.Collection_with_AudiofileDao collectionWithAudiofileDao;

    private LiveData<Room_DB.Collection> dataCollById;
    private LiveData<List<Room_DB.Collection>> dataCollList;
    private LiveData<List<Tables.AudiofileFull>> audiofilesByIdCollList;
    private LiveData<Tables.AudiofileWithImg> dataAudiofileById;
    private LiveData<List<Tables.AudiofileWithImg>> dataAudiofilesList;

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
                    collectionWithAudiofileDao = rdb.collectionWithAudiofileDao();
                }
            }).start();
        }
    }

    //Dashboard_Collection_Fragment//==================================================================
    /**Получение списка Наборов*/
    public LiveData<List<Room_DB.Collection>> getDataCollList(){
        dataCollList = collectionDao.getAll();
        return dataCollList;
    }

    /**Получение списка Наборов по букве/слову/предложению???*/
    public LiveData<List<Room_DB.Collection>> getDataColl(String searchText){
        dataCollList = collectionDao.searchCollection("%"+searchText+"%");
        return dataCollList;
    }

    /**<p>Запрос на получение данных о конкретном Наборе</p>
     * <p>Метод для получения данных  {@link #getDataSelectedColl()}</p>*/
    public void selectCollById(int id){
        collectionId = id;
    }

    ////////////////////////////////////=Dialog_Add_Collection=/////////////////////////////////////
    /**Добавление нового Набора*/
    public void addNewColl(String nameColl, String authorColl, String imgName){
        Room_DB.Collection collection = new Room_DB.Collection(nameColl, authorColl, imgName);
        new Thread(() -> collectionDao_abstract.insert(collection)).start();
    }

    //Dashboard_SetSoundsCollection_Fragment//=========================================================
    /**Получение данных о конкретном Наборе*/
    public LiveData<Room_DB.Collection> getDataSelectedColl(){
        dataCollById = collectionDao.getById(collectionId);
        return dataCollById;
    }

    /**Получение списка аудиозаписей выбранного Набора*/
    public LiveData<List<Tables.AudiofileFull>> getAudiofilesSelectedColl(){
        audiofilesByIdCollList = audiofileDao.getAllByIdCollection(collectionId);
        return audiofilesByIdCollList;
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
        new Thread(() -> myMediaPlayer.play(getApplication(), audiofileDao.getNonLiveById(id).audiofile)).start();
    }

    ////////////////////////////////////=Dialog_Edit_Collection=////////////////////////////////////
    /**Обновление данных о Наборе*/
    public void updateCollection(String newNameColl, String newAuthorColl, String newNameImg){
        Room_DB.Collection collection = dataCollById.getValue();
        if(collection != null){
            collection.name_collection = newNameColl;
            collection.author_collection = newAuthorColl;
            collection.img_collection = newNameImg;
            new Thread(() -> collectionDao.update(collection)).start();
        }
    }

    ////////////////////////////////////=Dialog_Delete_Collecton=///////////////////////////////////
    /**Удаление Набора*/
    public void deleteCollection(boolean full){
        new Thread(() -> collectionDao_abstract.delete(getApplication(), Objects.requireNonNull(dataCollById.getValue()), full)).start();
    }

    ///////////////////////////////////////=Dialog_Edit_Sound=//////////////////////////////////////
    /**Получение данных о конкретном Наборе*/
    public LiveData<Tables.AudiofileWithImg> getDataSelectedAudio(){
        return dataAudiofileById;
    }

    /**Обновление данных о Наборе*/
    public void updateAudiofile(String newNameSound, String newExecutorSound){
        new Thread(() -> audiofileDao.update(audiofileId, newNameSound, newExecutorSound)).start();
    }

    //////////////////////////////////////=Dialog_Delete_Sound=/////////////////////////////////////
    /**Удаление аудиозаписи*/
    public void deleteAudiofile(){
        new Thread(() -> {
            Tables.AudiofileWithImg audiofileWithImg = audiofileDao.getNonLiveById(audiofileId);
            Room_DB.Audiofile audiofile = new Room_DB.Audiofile(audiofileWithImg.id_audiofile, audiofileWithImg.name_audiofile);
            audiofileDao_abstract.delete(getApplication(), audiofile);
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
        dataAudiofilesList = audiofileDao.getAll();
        return dataAudiofilesList;
    }

    /**Получаем аудиозаписи привязанные к Набору*/
    public LiveData<List<Tables.AudiofileFull>> getAudiofilesByIdCollList(){
        audiofilesByIdCollList = audiofileDao.getAllByIdCollection(collectionId);
        return audiofilesByIdCollList;
    }

    /**Передаем ArrayList для Добавления/Удаления привязки аудиозаписей с Набором*/
    public void editCollection(HashMap<Integer, LibrarySoundAdapter.Check> checkList) {
        new Thread(() -> {
            for (LibrarySoundAdapter.Check check: checkList.values()) {

                Room_DB.Collection_with_Audiofile collection_with_audiofile = new Room_DB.Collection_with_Audiofile(collectionId, check.id);

                if(check.check){
                    collectionWithAudiofileDao.insert(collection_with_audiofile);
                }else {
                    collectionWithAudiofileDao.delete(collectionId, check.id);
                }
            }
        }).start();
    }

    /////////////////////////////////////=Fragment_RecordSound=/////////////////////////////////////
    ///////////////////////////////////=Fragment_InternalStorage=///////////////////////////////////
    /**Добавляем аудиозапись(с автопривязкой к Набору)*/
    public void addNewAudiofile(String nameSound, String executorSound, String audiofile){
        Room_DB.Audiofile audio = new Room_DB.Audiofile(nameSound, executorSound, audiofile, collectionId);
        new Thread(() -> audiofileDao_abstract.insert(audio)).start();
    }
}