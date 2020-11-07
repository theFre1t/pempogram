package tfre1t.example.pempogram.database;

import androidx.room.ColumnInfo;

public class DB_Table {

    public static class AudiofileWithImg {

        public int id_audiofile;

        public String name_audiofile;

        public String executor_audiofile;

        public String audiofile;

        public int id_collection;

        public String img_collection;
    }

    public static class AudiofileWithColli {

        public int id_audiofile;

        public String name_audiofile;

        public String executor_audiofile;

        public String audiofile;

        public int id_collection;

        @ColumnInfo(name = "id_collection_colli")
        public int id_collectionColLI;
    }

    public static class AudiofileFull {

        public int id_audiofile;

        public String name_audiofile;

        public String executor_audiofile;

        public String audiofile;

        public int id_collection;

        public String img_collection;

        @ColumnInfo(name = "id_collection_colli")
        public int id_collectionColLI;
    }
}
