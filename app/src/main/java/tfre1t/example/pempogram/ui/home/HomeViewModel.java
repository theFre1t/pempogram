package tfre1t.example.pempogram.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import tfre1t.example.pempogram.database.App;
import tfre1t.example.pempogram.database.Tables;
import tfre1t.example.pempogram.database.Room_DB;
import tfre1t.example.pempogram.MediaPlayer.MyMediaPlayer;

public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = "myLog";

    private static Room_DB.AppDatabase rdb;
    private static Room_DB.FavoriteAudioDao favoriteAudioDao;
    private static Room_DB.AudiofileDao audiofileDao;

    private LiveData<List<Tables.AudiofileWithImg>> dataFavAu;
    private LiveData<List<Tables.AudiofileWithImg>> dataSelAu;

    public HomeViewModel(@NonNull Application app) {
        super(app);
        dbConnect();
    }

    private void dbConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                rdb = App.getInstance().getDatabase();
                favoriteAudioDao = rdb.favoriteAudioDao();
                audiofileDao = rdb.audiofileDao();
            }
        }).start();
    }

    ////////////////////////////////=Dashboard_Collection_Fragment=/////////////////////////////////
    /**Получение списка аудиозаписей Бысторого вызова*/
    public LiveData<List<Tables.AudiofileWithImg>> getDataFavAu(){
        dataFavAu = favoriteAudioDao.getAll();
        return dataFavAu;
    }

    /**Удаление аудиозаписи Бысторого вызова*/
    public void removeFavAu(int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                favoriteAudioDao.delete(id);
            }
        }).start();
    }

    /**Проигрывание аудиозаписи Бысторого вызова*/
    public void playFavAu(MyMediaPlayer myMediaPlayer, int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myMediaPlayer.play(getApplication(), audiofileDao.getNonLiveById(id).audiofile);
            }
        }).start();
    }

    /////////////////////////////////////=SelectFavoriteAudio=//////////////////////////////////////
    /**Получение списка аудиозаписей не используемых в наборе Бысторого вызова*/
    public LiveData<List<Tables.AudiofileWithImg>> getDataSelAu(){
        dataSelAu = favoriteAudioDao.getAllNonFavAu();
        return dataSelAu;
    }

    /**Получение списка аудиозаписей не используемых в наборе Бысторого вызова по букве/слову/предложению???*/
    public LiveData<List<Tables.AudiofileWithImg>> getDataSelAu(String searchText){
        return favoriteAudioDao.searchAllNonFavAu("%"+searchText+"%");
    }

    /**Добавление аудиозаписи в набор Бысторого вызова*/
    public void addNewFavAu(int id){
        Room_DB.FavoriteAudio favoriteAudio = new Room_DB.FavoriteAudio();
        favoriteAudio.id_audiofile = id;
        new Thread(new Runnable() {
            @Override
            public void run() {
                favoriteAudioDao.insert(favoriteAudio);
            }
        }).start();
    }
}