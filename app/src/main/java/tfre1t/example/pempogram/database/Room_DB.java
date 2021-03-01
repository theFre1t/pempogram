package tfre1t.example.pempogram.database;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import tfre1t.example.pempogram.SaveFile.Imager;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;

public class Room_DB {

    @Entity
    public static class Collection {

        @PrimaryKey(autoGenerate = true)
        public int id_collection;

        public String name_collection;

        public String author_collection;

        public String img_collection;
    }

    @Entity(foreignKeys = @ForeignKey(entity = Collection.class, parentColumns = "id_collection", childColumns = "_id_collection", onDelete = SET_NULL),
            indices = @Index(value = {"id_audiofile","_id_collection"}, unique = true))
    public static class Audiofile {
        @PrimaryKey(autoGenerate = true)
        public int id_audiofile;

        public String name_audiofile;

        public String executor_audiofile;

        public String audiofile;

        @ColumnInfo(index = true)
        public int _id_collection;
    }

    @Entity
    public static class Categories {
        @PrimaryKey(autoGenerate = true)
        public int id_categories;

        public String name_categories;

        public String img_categories;
    }

    @Entity(foreignKeys = @ForeignKey(entity = Audiofile.class, parentColumns = "id_audiofile", childColumns = "_id_audiofile", onDelete = CASCADE))
    public static class FavoriteAudio {
        @PrimaryKey(autoGenerate = true)
        public int id_favau;

        @ColumnInfo(index = true)
        public int _id_audiofile;
    }

    @Entity(foreignKeys = {@ForeignKey(entity = Collection.class, parentColumns = "id_collection", childColumns = "_id_collection", onDelete = CASCADE),
                           @ForeignKey(entity = Audiofile.class, parentColumns = "id_audiofile", childColumns = "_id_audiofile", onDelete = CASCADE)},
            indices = @Index(value = {"_id_audiofile", "_id_collection"}, unique = true))
    public static class Collection_left_in {
        @PrimaryKey(autoGenerate = true)
        public int id;

        @ColumnInfo(index = true)
        public int _id_collection;

        @ColumnInfo(index = true)
        public int _id_audiofile;
    }

    @Entity
    public static class Categories_left_in {
        @PrimaryKey(autoGenerate = true)
        public int id;
        public int _id_categories;
        public int _id_collection;
    }

    @Entity(foreignKeys = @ForeignKey(entity = Collection.class, parentColumns = "id_collection", childColumns = "_id_collection", onDelete = SET_NULL),
            indices = @Index(value = {"revision_collection","_id_collection"},
                    unique = true))
    public static class Online_Collection {
        @PrimaryKey(autoGenerate = true)
        public int revision_collection;

        public String name_collection;

        public String author_collection;

        public String public_url_collection;

        public String img_file_collection;

        public String img_preview_collection;

        @ColumnInfo(index = true)
        public int _id_collection;
    }

    @Entity(foreignKeys = {@ForeignKey(entity = Online_Collection.class, parentColumns = "revision_collection", childColumns = "_revision_collection", onDelete = CASCADE),
                           @ForeignKey(entity = Audiofile.class, parentColumns = "id_audiofile", childColumns = "_id_audiofile", onDelete = SET_NULL)},
            indices = @Index(value = {"_id_audiofile", "_id_collection"}, unique = true))
    public static class Online_Audiofile {
        @PrimaryKey(autoGenerate = true)
        public int revision_audiofile;

        public String name_audiofile;

        public String author_audiofile;

        public String audiofile;

        public int _revision_collection;

        @ColumnInfo(index = true)
        public int _id_audiofile;
    }

    /*@Entity
    public static class User {
        @PrimaryKey(autoGenerate = true)
        public int id_user;
        public String name_user;
        public String login_user;
        public String password_user;
        public String email_user;
    }

    @Entity(foreignKeys = @ForeignKey(entity = Audiofile.class, parentColumns = "id_audiofile", childColumns = "id_audiofile", onDelete = CASCADE))
    public static class MyAudiofile {
        @PrimaryKey(autoGenerate = true)
        public int id;
        public int id_audiofile;
        public String id_user;
    }

    @Entity
    public static class MyCollection {
        @PrimaryKey(autoGenerate = true)
        public int id;
        public int id_collection;
        public String id_user;
    }*/

    @Dao
    public interface CollectionDao{
        @Query("Select * From collection")
        LiveData<List<Collection>> getAll();

        @Query("Select * From collection Where name_collection LIKE :text OR author_collection LIKE :text")
        LiveData<List<Collection>> searchCollection(String text);

        @Query("Select * From collection Where id_collection = :id")
        LiveData<Collection> getById(int id);

        @Update
        void update(Collection collection);
    }

    @Dao
    public interface AudiofileDao{
        @Query("Select Au.*, Col.img_collection From audiofile as Au left join collection as Col on Au._id_collection = Col.id_collection")
        LiveData<List<Tables.AudiofileWithImg>> getAll();

        @Query("Select Au.*, Col.img_collection, Colli._id_collection as id_collection_colli From Audiofile as Au left join Collection as Col" +
                " on Au._id_collection = Col.id_collection" +
                " left join Collection_left_in as Colli" +
                " on Au.id_audiofile = Colli._id_audiofile" +
                " Where Colli._id_collection = :id")
        LiveData<List<Tables.AudiofileFull>> getAllByIdCollection(int id);

        @Query("Select Au.*, Colli._id_collection as id_collection_colli From Audiofile as Au left join Collection_left_in as Colli" +
                " on Au.id_audiofile = Colli._id_audiofile" +
                " Where Au.id_audiofile = :id")
        LiveData<Tables.AudiofileWithColli> getAllById(int id);

        @Query("Select Au.*, Col.img_collection From audiofile as Au left join collection as Col on Au._id_collection = Col.id_collection" +
                " Where Au.id_audiofile = :id")
        LiveData<Tables.AudiofileWithImg> getById(int id);

        @Query("Select Au.*, Col.img_collection From audiofile as Au left join collection as Col on Au._id_collection = Col.id_collection" +
                " Where Au.id_audiofile = :id")
        Tables.AudiofileWithImg getNonLiveById(int id);

        @Query("Update Audiofile Set name_audiofile = :name, executor_audiofile = :executor Where id_audiofile = :id")
        void update(int id, String name, String executor);
    }

    @Dao
    public interface CategoriesDao{
        @Query("Select * From Categories")
        LiveData<List<Categories>> getAll();

        @Query("Select * From Categories Where id_categories = :id")
        LiveData<Categories> getById(int id);

        @Insert
        void insert(Categories categories);

        @Update
        void update(Categories categories);

        @Delete
        void delete(Categories categories);
    }

    @Dao
    public interface FavoriteAudioDao{
        @Query("Select Au.*, Col.img_collection From audiofile as Au left join collection as Col" +
                " on Au._id_collection = Col.id_collection" +
                " left join FavoriteAudio as Fav" +
                " on Au.id_audiofile = Fav._id_audiofile" +
                " Where Fav._id_audiofile is null")
        LiveData<List<Tables.AudiofileWithImg>> getAllNonFavAu();

        @Query("Select Au.*, Col.img_collection From audiofile as Au left join collection as Col" +
                " on Au._id_collection = Col.id_collection" +
                " left join FavoriteAudio as Fav" +
                " on Au.id_audiofile = Fav._id_audiofile" +
                " Where Fav._id_audiofile is null and" +
                " (Au.name_audiofile LIKE :text OR Au.executor_audiofile LIKE :text)")
        LiveData<List<Tables.AudiofileWithImg>> searchAllNonFavAu(String text);

        @Query("Select Au.*, Col.img_collection From audiofile as Au inner join collection as Col" +
                " on Au._id_collection = Col.id_collection" +
                " inner join FavoriteAudio as Fav" +
                " on Au.id_audiofile = Fav._id_audiofile")
        LiveData<List<Tables.AudiofileWithImg>> getAll();

        @Query("Select * From FavoriteAudio Where id_favau = :id")
        LiveData<FavoriteAudio> getById(int id);

        @Insert
        void insert(FavoriteAudio favoriteAudio);

        @Update
        void update(FavoriteAudio favoriteAudio);

        @Query("Delete from FavoriteAudio Where _id_audiofile = :id_audiofile")
        void delete(int id_audiofile);
    }

    @Dao
    public interface Collection_left_inDao{
        @Query("Select * From Collection_left_in")
        LiveData<List<Collection_left_in>> getAll();

        @Query("Select * From Collection_left_in Where _id_collection = :id")
        LiveData<Collection_left_in> getById(int id);

        @Insert
        void insert(Collection_left_in collectionLeftIn);

        @Update
        void update(Collection_left_in collectionLeftIn);

        @Query("Delete from Collection_left_in Where _id_collection = :idColl and _id_audiofile = :idAud")
        void delete(int idColl, int idAud);
    }

    @Dao
    public interface Categories_left_inDao{
        @Query("Select * From Categories_left_in")
        LiveData<List<Categories_left_in>> getAll();

        @Query("Select * From Categories_left_in Where id = :id")
        LiveData<Categories_left_in> getById(int id);

        @Insert
        void insert(Categories_left_in categoriesLeftIn);

        @Update
        void update(Categories_left_in categoriesLeftIn);

        @Delete
        void delete(Categories_left_in categoriesLeftIn);
    }

    @Dao
    public interface Online_CollectionDao{
        @Query("Select * From Online_Collection")
        LiveData<List<Online_Collection>> getAll();

        @Query("Select * From Online_Collection Where revision_collection = :revision")
        LiveData<Online_Collection> getByRevision(int revision);

        @Insert
        void insert(Online_Collection online_collection);

        @Update
        void update(Online_Collection online_collection);

        @Delete
        void delete(Online_Collection online_collection);
    }

    @Dao
    public interface Online_AudiofileDao{
        @Query("Select * From Online_Audiofile")
        LiveData<List<Online_Audiofile>> getAll();

        @Query("Select * From Online_Audiofile Where revision_audiofile = :revision")
        LiveData<Online_Audiofile> getByRevision(int revision);

        @Insert
        void insert(Online_Audiofile online_audiofile);

        @Update
        void update(Online_Audiofile online_audiofile);

        @Delete
        void delete(Online_Audiofile online_audiofile);
    }

    @Dao
    public static abstract class CollectionDao_abstract{
        @Insert
        protected abstract void insertCollection(Collection collection);

        @Transaction
        public void insert(Collection collection){
            if(collection.img_collection == null){
                collection.img_collection = "default.png";
            }
            insertCollection(collection);
        }

        @Query("Select audiofile From audiofile Where _id_collection = :id")
        protected abstract List<String> getAudiofiles(int id);

        @Query("Delete from Audiofile Where _id_collection = :id")
        protected abstract void deleAllAudiofilesByIdCollection(int id);

        @Delete
        protected abstract void deleteCollection(Collection collection);

        @Transaction
        public void delete(Context ctx, Collection collection, boolean full){
            new Imager().deleteImage(ctx, collection.img_collection);
            if (full){
                List<String> audiofiles = getAudiofiles(collection.id_collection);
                for (String audiofile: audiofiles) {
                    ctx.deleteFile(audiofile);
                }
                deleAllAudiofilesByIdCollection(collection.id_collection);
            }
            deleteCollection(collection);
        }
    }

    @Dao
    public abstract static class AudiofileDao_abstract{
        @Insert
        protected abstract void insertAudiofile(Audiofile audiofile);

        @Query("Select last_insert_rowid()")
        protected abstract int getInsertAudiofile();

        @Insert
        protected abstract void insertCollection_left_in(Collection_left_in collectionLeftIn);

        @Transaction
        public void insert(Audiofile aud){
            Collection_left_in collectionLeftIn = new Collection_left_in();
            insertAudiofile(aud);
            collectionLeftIn._id_audiofile = getInsertAudiofile();
            collectionLeftIn._id_collection = aud._id_collection;
            insertCollection_left_in(collectionLeftIn);
        }

        @Delete
        protected abstract void deleteAudiofile(Audiofile audiofile);

        @Transaction
        public void delete(Context ctx, Audiofile audiofile){
            ctx.deleteFile(audiofile.name_audiofile);
            deleteAudiofile(audiofile);
        }
    }

    @Database(entities = {Collection.class, Audiofile.class, Categories.class, FavoriteAudio.class, Collection_left_in.class, Categories_left_in.class}, version = 1)
    public abstract static class AppDatabase extends RoomDatabase{
        public abstract CollectionDao collectionDao();
        public abstract CollectionDao_abstract collectionDaoAbstr();
        public abstract AudiofileDao audiofileDao();
        public abstract AudiofileDao_abstract audiofileDaoAbstr();
        public abstract CategoriesDao categoriesDao();
        public abstract FavoriteAudioDao favoriteAudioDao();
        public abstract Collection_left_inDao collectionLeftInDao();
        public abstract Categories_left_inDao categoriesLeftInDao();
        public abstract Online_CollectionDao onlineCollectionDao();
        public abstract Online_AudiofileDao onlineAudiofileDao();
    }
}