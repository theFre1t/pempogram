package com.tfre1t.pempogram.database;
import android.app.Application;

import androidx.room.Room;

public class App extends Application {
    public static App instance;

    private Room_DB.AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, Room_DB.AppDatabase.class, "database").build();
    }

    public static App getInstance(){
        return instance;
    }

    public Room_DB.AppDatabase getDatabase(){
        return database;
    }
}
