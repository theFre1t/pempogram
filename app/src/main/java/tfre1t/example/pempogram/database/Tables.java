package tfre1t.example.pempogram.database;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

public class Tables {

    public static class AudiofileWithImg {

        public int id_audiofile;

        public String name_audiofile;

        public String executor_audiofile;

        public String audiofile;

        public int _id_collection;

        public String img_collection;
    }

    public static class AudiofileWithColli {

        public int id_audiofile;

        public String name_audiofile;

        public String executor_audiofile;

        public String audiofile;

        public int _id_collection;

        @ColumnInfo(name = "id_collection_colli")
        public int id_collectionColLI;
    }

    public static class AudiofileFull {

        public int id_audiofile;

        public String name_audiofile;

        public String executor_audiofile;

        public String audiofile;

        public int _id_collection;

        public String img_collection;

        @ColumnInfo(name = "id_collection_colli")
        public int id_collectionColLI;
    }

    @DatabaseView("SELECT * FROM Online_Collection as oc left join Online_Collection_with_Collection as ocw on oc.id_online_collection == ocw._id_online_collection or ocw._id_online_collection isnull")
    public static class Online_CollectionView{

        @Embedded
        public Room_DB.Online_Collection Online_Collection;

        @Embedded
        public Room_DB.Online_Collection_with_Collection collectionWithCollection;
    }
}
