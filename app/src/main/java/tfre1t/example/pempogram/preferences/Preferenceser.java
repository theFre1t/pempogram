package tfre1t.example.pempogram.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Preferenceser {

    private final String SAVED_TYPE_VIEW_COLLECTION = "type_view_collection";

    private final SharedPreferences sharedPreferences;

    public Preferenceser(Context ctx){
        sharedPreferences = ctx.getSharedPreferences("MyAppPreference", MODE_PRIVATE);
    }

    public void saveTypeViewCollection(int type){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SAVED_TYPE_VIEW_COLLECTION, type);
        editor.apply();
    }

    public int loadTypeViewCollection(){
        return sharedPreferences.getInt(SAVED_TYPE_VIEW_COLLECTION, 0);
    }
}
