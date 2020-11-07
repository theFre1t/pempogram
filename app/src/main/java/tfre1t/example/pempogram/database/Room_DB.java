package tfre1t.example.pempogram.database;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import tfre1t.example.pempogram.savefile.Imager;

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

    @Entity(foreignKeys = @ForeignKey(entity = Collection.class, parentColumns = "id_collection", childColumns = "id_collection", onDelete = SET_NULL),
            indices = @Index(value = {"id_audiofile","id_collection"}, unique = true))
    public static class Audiofile {
        @PrimaryKey(autoGenerate = true)
        public int id_audiofile;

        public String name_audiofile;

        public String executor_audiofile;

        public String audiofile;

        @ColumnInfo(index = true)
        public int id_collection;
    }

    @Entity
    public static class Categories {
        @PrimaryKey(autoGenerate = true)
        public int id_categories;

        public String name_categories;

        public String img_categories;
    }

    @Entity(foreignKeys = @ForeignKey(entity = Audiofile.class, parentColumns = "id_audiofile", childColumns = "id_audiofile", onDelete = CASCADE))
    public static class FavoriteAudio {
        @PrimaryKey(autoGenerate = true)
        public int id_favau;

        @ColumnInfo(index = true)
        public int id_audiofile;
    }

    @Entity(foreignKeys = {@ForeignKey(entity = Collection.class, parentColumns = "id_collection", childColumns = "id_collection", onDelete = CASCADE),
                           @ForeignKey(entity = Audiofile.class, parentColumns = "id_audiofile", childColumns = "id_audiofile", onDelete = CASCADE)},
            indices = @Index(value = {"id_audiofile","id_collection"}, unique = true))
    public static class Collection_left_in {
        @PrimaryKey(autoGenerate = true)
        public int id;

        @ColumnInfo(index = true)
        public int id_collection;

        @ColumnInfo(index = true)
        public int id_audiofile;
    }

    @Entity
    public static class Categories_left_in {
        @PrimaryKey(autoGenerate = true)
        public int id;
        public int id_categories;
        public int id_collection;
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

        @Query("Select * From collection Where id_collection = :id")
        LiveData<Collection> getById(int id);

        @Update
        void update(Collection collection);
    }

    @Dao
    public interface AudiofileDao{
        @Query("Select Au.*, Col.img_collection From audiofile as Au left join collection as Col on Au.id_collection = Col.id_collection")
        LiveData<List<DB_Table.AudiofileWithImg>> getAll();

        @Query("Select Au.*, Col.img_collection, Colli.id_collection as id_collection_colli From Audiofile as Au left join Collection as Col" +
                " on Au.id_collection = Col.id_collection" +
                " left join Collection_left_in as Colli" +
                " on Au.id_audiofile = Colli.id_audiofile" +
                " Where Colli.id_collection = :id")
        LiveData<List<DB_Table.AudiofileFull>> getAllByIdCollection(int id);

        @Query("Select Au.*, Colli.id_collection as id_collection_colli From Audiofile as Au left join Collection_left_in as Colli" +
                " on Au.id_audiofile = Colli.id_audiofile" +
                " Where Au.id_audiofile = :id")
        LiveData<DB_Table.AudiofileWithColli> getAllById(int id);

        @Query("Select Au.*, Col.img_collection From audiofile as Au left join collection as Col on Au.id_collection = Col.id_collection" +
                " Where Au.id_audiofile = :id")
        LiveData<DB_Table.AudiofileWithImg> getById(int id);

        @Query("Select Au.*, Col.img_collection From audiofile as Au left join collection as Col on Au.id_collection = Col.id_collection" +
                " Where Au.id_audiofile = :id")
        DB_Table.AudiofileWithImg getNonLiveById(int id);

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
                " on Au.id_collection = Col.id_collection" +
                " left join FavoriteAudio as Fav" +
                " on Au.id_audiofile = Fav.id_audiofile" +
                " Where Fav.id_audiofile is null")
        LiveData<List<DB_Table.AudiofileWithImg>> getAllNonFavAu();

        @Query("Select Au.*, Col.img_collection From audiofile as Au inner join collection as Col" +
                " on Au.id_collection = Col.id_collection" +
                " inner join FavoriteAudio as Fav" +
                " on Au.id_audiofile = Fav.id_audiofile")
        LiveData<List<DB_Table.AudiofileWithImg>> getAll();

        @Query("Select * From FavoriteAudio Where id_favau = :id")
        LiveData<FavoriteAudio> getById(int id);

        @Insert
        void insert(FavoriteAudio favoriteAudio);

        @Update
        void update(FavoriteAudio favoriteAudio);

        @Query("Delete from FavoriteAudio Where id_audiofile = :id_audiofile")
        void delete(int id_audiofile);
    }

    @Dao
    public interface Collection_left_inDao{
        @Query("Select * From Collection_left_in")
        LiveData<List<Collection_left_in>> getAll();

        @Query("Select * From Collection_left_in Where id_collection = :id")
        LiveData<Collection_left_in> getById(int id);

        @Insert
        void insert(Collection_left_in collectionLeftIn);

        @Update
        void update(Collection_left_in collectionLeftIn);

        @Query("Delete from Collection_left_in Where id_collection = :idColl and id_audiofile = :idAud")
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
        void update(Categories_left_in categories_left_in);

        @Delete
        void delete(Categories_left_in categoriesLeftIn);
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

        @Query("Select audiofile From audiofile Where id_collection = :id")
        protected abstract List<String> getAudiofiles(int id);

        @Query("Delete from Audiofile Where id_collection = :id")
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
            collectionLeftIn.id_audiofile = getInsertAudiofile();
            collectionLeftIn.id_collection = aud.id_collection;
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
    }
}