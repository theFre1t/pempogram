package tfre1t.example.pempogram.database;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import tfre1t.example.pempogram.R;

public class DB {
    private static final String DB_NAME = "db";
    private static final int DB_VERSION = 1;

    public static final String DB_TABLE_COLLECTION = "collection";
    public static final String COLUMN_ID_COLLECTION = "_id";
    public static final String COLUMN_NAME_COLLECTION = "name";
    public static final String COLUMN_AUTHOR_COLLECTION = "author";
    public static final String COLUMN_IMG_COLLECTION = "img";

    public static final String DB_TABLE_AUDIOFILE = "audiofile";
    public static final String COLUMN_ID_AUDIOFILE = "_id";
    public static final String COLUMN_NAME_AUDIOFILE = "name";
    public static final String COLUMN_EXECUTOR_AUDIOFILE = "executor";
    public static final String COLUMN_AUDIOFILE = "audiofile";
    public static final String COLUMN_IDCOLLECTION_AUDIOFILE = "id_collection";

    public static final String DB_TABLE_FAVORITEAUDIO = "favoriteaudio";
    public static final String COLUMN_ID_FAVORITEAUDIO = "_id";
    public static final String COLUMN_IDAUDIOFILE_FAVORITEAUDIO = "id_audiofile";

    private static final String CREATE_TABLE_COLLECTION =
                    "create table " + DB_TABLE_COLLECTION + "(" +
                            COLUMN_ID_COLLECTION + " integer primary key autoincrement, " +
                            COLUMN_NAME_COLLECTION + " text, " +
                            COLUMN_AUTHOR_COLLECTION + " text, " +
                            COLUMN_IMG_COLLECTION + " text" +
                            ");";
    private static final String CREATE_TABLE_AUDIOFILE =
                    "create table " + DB_TABLE_AUDIOFILE + "(" +
                            COLUMN_ID_AUDIOFILE + " integer primary key autoincrement, " +
                            COLUMN_NAME_AUDIOFILE + " text, " +
                            COLUMN_EXECUTOR_AUDIOFILE + " text, " +
                            COLUMN_AUDIOFILE + " text, " +
                            COLUMN_IDCOLLECTION_AUDIOFILE + " integer" +
                            ");";
    private static final String CREATE_TABLE_FAVORITEAUDIO =
                    "create table " + DB_TABLE_FAVORITEAUDIO + "(" +
                            COLUMN_ID_FAVORITEAUDIO + " integer primary key autoincrement, " +
                            COLUMN_IDAUDIOFILE_FAVORITEAUDIO + " integer" +
                            ");";

    private final Context myCtx;

    private DBHelper myDBHelper;
    private SQLiteDatabase myDB;

    public DB(Context ctx) {
        myCtx = ctx;
    }

    // открыть подключение
    public void open() {
        myDBHelper = new DBHelper(myCtx, DB_NAME, null, DB_VERSION);
        myDB = myDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (myDBHelper!=null) myDBHelper.close();
    }
    //==============================================================================================

    // работа с таблицей DB_TABLE_COLLECTION
    // получить все данные из таблицы DB_TABLE_COLLECTION
    public Cursor getAllDataCollection() {
        return myDB.query(DB_TABLE_COLLECTION, null, null, null, null, null, null);
    }

    // получить все данные из таблицы DB_TABLE_COLLECTION по id коллекции
    public Cursor getDataCollectionById(long id) {
        String selection = COLUMN_ID_COLLECTION + " = " + id;
        return myDB.query(DB_TABLE_COLLECTION, null, selection,  null, null, null, null);
    }

    // добавить запись в DB_TABLE_COLLECTION
    public void addRecCollection(String name, String author, String img) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_COLLECTION, name);
        cv.put(COLUMN_AUTHOR_COLLECTION, author);
        if(img != null) {
            cv.put(COLUMN_IMG_COLLECTION, img);
        }
        else {
            cv.put(COLUMN_IMG_COLLECTION, "default.png");
        }
        myDB.insert(DB_TABLE_COLLECTION, null, cv);
    }

    // изменить запись в DB_TABLE_COLLECTION
    public void updateRecCollection(long id, String name, String author, String img) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_COLLECTION, name);
        cv.put(COLUMN_AUTHOR_COLLECTION, author);
        cv.put(COLUMN_IMG_COLLECTION, img);
        myDB.update(DB_TABLE_COLLECTION, cv, COLUMN_ID_COLLECTION+" = ?", new String[]{ "" + id });
    }

    // удалить запись из DB_TABLE_COLLECTION
    public void delRecCollection(long id) {
        delRecAudiofilesByIdColletion(id);
        Cursor cursor = getDataCollectionById(id); cursor.moveToFirst();
        String nameImg = cursor.getString(cursor.getColumnIndex(COLUMN_IMG_COLLECTION));
        if(!nameImg.equals("default.png")){
            delImgCollection(nameImg);
        }
        myDB.delete(DB_TABLE_COLLECTION, COLUMN_ID_COLLECTION + " = " + id, null);
    }

    // удаление файла изоражения коллекции
    public void delImgCollection(String nameImg){
        if(!nameImg.equals("default.png")){
            myCtx.deleteFile(nameImg);
        }
    }
    //==============================================================================================

    // работа с таблицей DB_TABLE_AUDIOFILE
    // получить все данные из таблицы DB_TABLE_AUDIOFILE
    public Cursor getAllDataAudiofile() {
        String table = DB_TABLE_AUDIOFILE + " as AUD inner join " + DB_TABLE_COLLECTION + " as COLL on AUD." + COLUMN_IDCOLLECTION_AUDIOFILE + " = COLL."+ COLUMN_ID_COLLECTION;
        String[] columns = {"AUD.*", "COLL."+COLUMN_IMG_COLLECTION};
        return myDB.query(table, columns, null, null, null, null, null);
    }

    // получить все данные из таблицы DB_TABLE_AUDIOFILE кроме задействованных в DB_TABLE_FAVORITEAUDIO
    public Cursor getAllDataAudiofileNonFavAu() {
        String table = "("+ DB_TABLE_AUDIOFILE + " as AUD inner join " + DB_TABLE_COLLECTION + " as COLL on AUD." + COLUMN_IDCOLLECTION_AUDIOFILE + " = COLL."+ COLUMN_ID_COLLECTION +
                ") left join " + DB_TABLE_FAVORITEAUDIO + " as FAVO on AUD." + COLUMN_ID_AUDIOFILE + " = FAVO." + COLUMN_IDAUDIOFILE_FAVORITEAUDIO;
        String[] columns = {"AUD.*", "COLL."+COLUMN_IMG_COLLECTION};
        String selection = "FAVO." + COLUMN_IDAUDIOFILE_FAVORITEAUDIO + " is null";
        return myDB.query(table, columns, selection, null, null, null, null);
    }

    // получить все данные из таблицы DB_TABLE_AUDIOFILE по id коллекции
    public Cursor getDataAudiofileByIdCollection(long id) {
        String table = DB_TABLE_AUDIOFILE + " as AUD inner join " + DB_TABLE_COLLECTION + " as COLL on AUD." + COLUMN_IDCOLLECTION_AUDIOFILE + " = COLL."+ COLUMN_ID_COLLECTION;
        String[] columns = {"AUD.*", "COLL."+COLUMN_IMG_COLLECTION};
        String selection = "AUD."+COLUMN_IDCOLLECTION_AUDIOFILE + " = " + id;
        return myDB.query(table, columns, selection, null, null, null, null);
    }

    // получить данные аудиофайла из таблицы DB_TABLE_AUDIOFILE по id аудиофайла
    public Cursor getDataAudiofileByIdAudifile(long id) {
        String table = DB_TABLE_AUDIOFILE + " as AUD inner join " + DB_TABLE_COLLECTION + " as COLL on AUD." + COLUMN_IDCOLLECTION_AUDIOFILE + " = COLL."+ COLUMN_ID_COLLECTION;
        String[] columns = {"AUD.*", "COLL."+COLUMN_IMG_COLLECTION};
        String selection = "AUD."+COLUMN_ID_AUDIOFILE + " = " + id;
        return myDB.query(table, columns, selection, null, null, null, null);
    }

    // добавить запись в DB_TABLE_AUDIOFILE
    public void addRecAudiofile(String name, String executor, String pathaudiofile , long idcollection) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_AUDIOFILE, name);
        cv.put(COLUMN_EXECUTOR_AUDIOFILE, executor);
        cv.put(COLUMN_AUDIOFILE, pathaudiofile);
        cv.put(COLUMN_IDCOLLECTION_AUDIOFILE, idcollection);
        myDB.insert(DB_TABLE_AUDIOFILE, null, cv);
    }

    // изменить запись в DB_TABLE_AUDIOFILE
    public void updateRecAudiofile (long id_audiofile, String name, String executor){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_AUDIOFILE, name);
        cv.put(COLUMN_EXECUTOR_AUDIOFILE, executor);
        myDB.update(DB_TABLE_AUDIOFILE, cv, COLUMN_ID_AUDIOFILE+" = ?", new String[]{ "" + id_audiofile });
    }

    // удалить запись из DB_TABLE_AUDIOFILE
    public void delRecAudiofile(long id) {
        Cursor cursor = getDataAudiofileByIdAudifile(id); cursor.moveToFirst();
        String nameAudio = cursor.getString(cursor.getColumnIndex(COLUMN_AUDIOFILE));
        try { delRecFavoriteaudio(id); }
        catch (NullPointerException e){ }

        myCtx.deleteFile(nameAudio);
        myDB.delete(DB_TABLE_AUDIOFILE, COLUMN_ID_AUDIOFILE + " = " + id, null);
    }

    // удалить записи из DB_TABLE_AUDIOFILE по id коллекции
    public void delRecAudiofilesByIdColletion(long id) {
        Cursor cursor = getDataAudiofileByIdCollection(id);
        if(cursor.getCount() != 0){
            cursor.moveToFirst();
            long id_audio;
            do{
                id_audio = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_AUDIOFILE));
                delRecAudiofile(id_audio);
            } while (cursor.moveToNext());
        }
    }

    //==============================================================================================

    // работа с таблицей DB_TABLE_FAVORITEAUDIO
    // получить все данные из таблицы DB_TABLE_FAVORITEAUDIO
    public Cursor getAllDataFavoriteaudio() {
        return myDB.query(DB_TABLE_FAVORITEAUDIO, null, null, null, null, null, null);
    }

    // получить все данные из таблицы DB_TABLE_AUDIOFILE по связи с DB_TABLE_FAVORITEAUDIO
    public Cursor getAllDataAudiofileFromFavoriteaudio() {
        String table = "("+ DB_TABLE_AUDIOFILE + " as AUD inner join " + DB_TABLE_COLLECTION + " as COLL on AUD." + COLUMN_IDCOLLECTION_AUDIOFILE + " = COLL."+ COLUMN_ID_COLLECTION +
                ") inner join " + DB_TABLE_FAVORITEAUDIO + " as FAVO on AUD." + COLUMN_ID_AUDIOFILE + " = FAVO." + COLUMN_IDAUDIOFILE_FAVORITEAUDIO;
        String[] columns = {"AUD.*", "COLL."+COLUMN_IMG_COLLECTION};
        return myDB.query(table, columns, null, null, null, null, null);
    }

    // добавить запись в DB_TABLE_FAVORITEAUDIO
    public void addRecFavoriteaudio(long idaudiofile) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IDAUDIOFILE_FAVORITEAUDIO, idaudiofile);
        myDB.insert(DB_TABLE_FAVORITEAUDIO, null, cv);
    }

    // удалить запись из DB_TABLE_FAVORITEAUDIO
    public void delRecFavoriteaudio(long id) {
        myDB.delete(DB_TABLE_FAVORITEAUDIO, COLUMN_IDAUDIOFILE_FAVORITEAUDIO + " = " + id, null);
    }
    //==============================================================================================

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_COLLECTION);
            db.execSQL(CREATE_TABLE_AUDIOFILE);
            db.execSQL(CREATE_TABLE_FAVORITEAUDIO);
            writeFileIMG();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        String filename;

        void writeFileIMG() {
            try {
                filename = "default.png";
                FileOutputStream fOut = myCtx.openFileOutput(filename, myCtx.MODE_PRIVATE);
                Bitmap bitmap = BitmapFactory.decodeResource(myCtx.getResources(), R.drawable.defaultimg);
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (
                    FileNotFoundException e) {
                e.printStackTrace();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }
    }
}
