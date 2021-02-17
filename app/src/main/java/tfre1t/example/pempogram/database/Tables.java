package tfre1t.example.pempogram.database;

import androidx.room.ColumnInfo;

public class Tables {

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

    public static class Online_Collection {

        public String resource_id_collection;

        public String name_collection;

        public String author_collection;

        public String public_url_collection;

        public String img_file_collection;

        public String img_preview_collection;

        public Online_Collection(String res_id, String name, String author, String img_file, String img_preview) {
            resource_id_collection = res_id;
            name_collection = name;
            author_collection = author;
            img_file_collection = img_file;
            img_preview_collection = img_preview;
        }
    }

    public static class Online_Audiofile {

        public String resource_id_audiofile;

        public String name_audiofile;

        public String audiofile;

        public String resource_id_collection;

        public Online_Audiofile(String res_id, String name, String file_url, String coll_res_id) {
            resource_id_audiofile = res_id;
            name_audiofile = name;
            audiofile = file_url;
            resource_id_collection = coll_res_id;
        }
    }
}
