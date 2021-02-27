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

        public int revision_collection;

        public String name_collection;

        public String author_collection;

        public String public_url_collection;

        public String img_file_collection;

        public String img_preview_collection;

        public Online_Collection(int rev_id, String name, String author, String img_file, String img_preview) {
            revision_collection = rev_id;
            name_collection = name;
            author_collection = author;
            img_file_collection = img_file;
            img_preview_collection = img_preview;
        }
    }

    public static class Online_Audiofile {

        public int revision_audiofile;

        public String name_audiofile;

        public String audiofile;

        public int resource_id_collection;

        public Online_Audiofile(int rev_id, String name, String file_url, int coll_rev) {
            revision_audiofile = rev_id;
            name_audiofile = name;
            audiofile = file_url;
            resource_id_collection = coll_rev;
        }
    }
}
