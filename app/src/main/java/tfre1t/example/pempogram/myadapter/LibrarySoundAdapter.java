package tfre1t.example.pempogram.myadapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tfre1t.example.pempogram.customviewers.RoundedImageView;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.savefile.Imager;

public class LibrarySoundAdapter extends RecyclerView.Adapter<LibrarySoundAdapter.LibrarySoundHolder> {

    Context ctx;
    int layout;

    int[] mTo;
    Cursor cursor, cursorSC;
    String[] mFrom;
    View view;
    ArrayList<Sound> sounds;

    public class Sound{
        public int id;
        Bitmap img;
        String name;
        String execute;
        public boolean check;

        Sound(int id, Bitmap img, String name, String execute, boolean check){
            this.id = id;
            this.img = img;
            this.name = name;
            this.execute = execute;
            this.check = check;
        }
    }

    class LibrarySoundHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgv;
        TextView tvNameAudio, tvExecuteAudio;
        CheckBox chbSound;

        public LibrarySoundHolder(@NonNull View itemView) {
            super(itemView);
            imgv= itemView.findViewById(mTo[0]);
            tvNameAudio= itemView.findViewById(mTo[1]);
            tvExecuteAudio= itemView.findViewById(mTo[2]);
            chbSound= itemView.findViewById(mTo[3]);
            chbSound.setOnCheckedChangeListener(myCheckedListener);
            itemView.setTag(this);
        }
    }

    public LibrarySoundAdapter(Context context, int layout, Cursor c, String[] from, int[] to, Cursor csc) {
        ctx = context;
        this.layout = layout;
        cursor = c;
        mTo = to;
        mFrom = from;
        cursorSC = csc;

        c.moveToLast();
        int count = c.getInt(c.getColumnIndex(DB.COLUMN_ID_AUDIOFILE));
        sounds = new ArrayList<Sound>(count);
        for(int i = 0; i <= count; i++){
            sounds.add(new Sound(0,null, null,null,false));
        }
    }

    OnCheckedChangeListener myCheckedListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try { sounds.get(buttonView.getId()).check = isChecked; } catch (IndexOutOfBoundsException ignored){ }
        }
    };

    public ArrayList<Sound> getSounds() {
        sounds.remove(0);
        return sounds;
    }

    @NonNull
    @Override
    public LibrarySoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new LibrarySoundAdapter.LibrarySoundHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibrarySoundHolder holder, int position) {
        cursor.moveToPosition(cursor.getCount() - (position + 1));
        Sound s = new Sound(
                cursor.getInt(cursor.getColumnIndex(mFrom[0])),
                new Imager().setImageView(ctx ,cursor.getString(cursor.getColumnIndex(mFrom[1]))),
                cursor.getString(cursor.getColumnIndex(mFrom[2])),
                cursor.getString(cursor.getColumnIndex(mFrom[3])),
                checkBoxChecker(cursor.getInt(cursor.getColumnIndex(mFrom[0])))
        );
        sounds.set(s.id,s);
        holder.itemView.setId(s.id);
        holder.imgv.setImageBitmap(s.img);
        holder.tvNameAudio.setText(s.name);
        holder.tvExecuteAudio.setText(s.execute);
        holder.chbSound.setChecked(s.check);
        holder.chbSound.setId(s.id);
    }

    boolean check;
    private boolean checkBoxChecker(int id) {
        check = false;
        if(cursorSC.getCount() != 0) {
            cursorSC.moveToFirst();
            do {
                if (check = cursorSC.getInt(cursorSC.getColumnIndex(DB.COLUMN_ID_AUDIOFILE)) == id) { break; }
            }
            while (cursorSC.moveToNext());
        }
        return check;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
}

