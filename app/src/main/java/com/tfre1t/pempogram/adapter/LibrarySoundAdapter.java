package com.tfre1t.pempogram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import com.tfre1t.pempogram.R;
import com.tfre1t.pempogram.CustomViewers.RoundedImageView;
import com.tfre1t.pempogram.database.Tables;
import com.tfre1t.pempogram.SaveFile.Imager;

public class LibrarySoundAdapter extends RecyclerView.Adapter<LibrarySoundAdapter.LibrarySoundHolder> {
    private final static String TAG = "myLog";

    private final Context ctx;
    private final int layout;

    private final List<Tables.AudiofileWithImg> list;
    private final List<Tables.AudiofileFull> listSAudio;
    private final HashMap<Integer,Check> oldCheckList, checkList;

    public static class Check{
        public int id;
        public boolean check;

        Check(int id, boolean check){
            this.id = id;
            this.check = check;
        }
    }

    class LibrarySoundHolder extends RecyclerView.ViewHolder {
        private final RoundedImageView imgv;
        private final TextView tvNameAudio;
        private final TextView tvExecuteAudio;
        private final CheckBox chbSound;

        public LibrarySoundHolder(@NonNull View itemView) {
            super(itemView);
            imgv= itemView.findViewById(R.id.imgAudiofile);
            tvNameAudio= itemView.findViewById(R.id.tvNameAudio);
            tvExecuteAudio= itemView.findViewById(R.id.tvExecutorAudio);
            chbSound= itemView.findViewById(R.id.chbSound);

            chbSound.setOnCheckedChangeListener(myCheckedListener);
            itemView.setTag(this);
        }
    }

    public LibrarySoundAdapter(Context context, List<Tables.AudiofileWithImg> list, List<Tables.AudiofileFull> listSAudio) {
        ctx = context;
        layout = R.layout.card_addsound_librarysound_classiclist;
        this.list = list;
        this.listSAudio = listSAudio;
        oldCheckList = new HashMap<>(list.size());
        checkList = new HashMap<>(list.size());
    }

    OnCheckedChangeListener myCheckedListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                int id = buttonView.getId();
                Check check = oldCheckList.get(id);
                if(check.check == isChecked){
                    checkList.remove(id);
                }else {
                    checkList.put(id, new Check(check.id, isChecked));
                }
            }catch (NullPointerException ignored){ }
        }
    };

    public HashMap<Integer,Check> getSounds() {
        return checkList;
    }

    @NonNull
    @Override
    public LibrarySoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new LibrarySoundAdapter.LibrarySoundHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibrarySoundHolder holder, int position) {
        Tables.AudiofileWithImg audiofile =  list.get(getItemCount()- (position + 1));

        holder.itemView.setId(audiofile.id_audiofile);
        holder.imgv.setImageBitmap(new Imager().setImageView(ctx, audiofile.img_collection, false));
        holder.tvNameAudio.setText(audiofile.name_audiofile);
        holder.tvExecuteAudio.setText(audiofile.executor_audiofile);
        holder.chbSound.setChecked(checkBoxChecker(audiofile.id_audiofile));
        holder.chbSound.setId(audiofile.id_audiofile);

        oldCheckList.put(audiofile.id_audiofile, new Check(audiofile.id_audiofile, check));
    }

    private boolean check;
    private boolean checkBoxChecker(int id) {
        check = false;
        if(listSAudio.size() != 0) {
            for (Tables.AudiofileFull audio : listSAudio) {
                if (check = audio.id_audiofile == id) { break; }
            }
        }
        return check;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

