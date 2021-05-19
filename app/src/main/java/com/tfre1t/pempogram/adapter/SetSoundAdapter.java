package com.tfre1t.pempogram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.tfre1t.pempogram.R;
import com.tfre1t.pempogram.CustomViewers.RoundedImageView;
import com.tfre1t.pempogram.database.Tables;
import com.tfre1t.pempogram.SaveFile.Imager;

import org.jetbrains.annotations.NotNull;

import static android.view.View.*;
import static androidx.recyclerview.widget.RecyclerView.*;

public class SetSoundAdapter extends RecyclerView.Adapter<SetSoundAdapter.SetSoundHolder> {

    private OnClickListener onItemClickListener;

    public void setItemClickListener(OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    private OnClickListener onMenuClickListener;

    public void setMenuClickListener(OnClickListener clickListener) {
        onMenuClickListener = clickListener;
    }

    private final Context ctx;

    private final int layout;
    private List<Tables.AudiofileFull> listAudiofiles;

    class SetSoundHolder extends ViewHolder {
        private final RoundedImageView imgAudiofile;
        private final TextView tvAudiofile, tvAuthor;
        private final ImageView imgBtnPupupMenu;

        public SetSoundHolder(@NonNull View itemView) {
            super(itemView);
            imgAudiofile = itemView.findViewById(R.id.imgAudiofile);
            tvAudiofile = itemView.findViewById(R.id.tvAudiofile);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            imgBtnPupupMenu = itemView.findViewById(R.id.imgBtnPupupMenu);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
            imgBtnPupupMenu.setOnClickListener(onMenuClickListener);
        }
    }

    public SetSoundAdapter(Context context, List<Tables.AudiofileFull> list) {
        ctx = context;
        listAudiofiles = list;
        layout = R.layout.card_dashboard_setsounds_collection_classiclist;
    }

    @NonNull
    @Override
    public SetSoundAdapter.SetSoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new SetSoundAdapter.SetSoundHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetSoundAdapter.SetSoundHolder holder, int position) {
        Tables.AudiofileFull audiofile = listAudiofiles.get(position);
        holder.itemView.setId(audiofile.id_audiofile);
        holder.imgAudiofile.setImageBitmap(new Imager().setImageView(ctx, audiofile.img_collection, false));
        holder.tvAudiofile.setText(audiofile.name_audiofile);
        holder.tvAuthor.setText(audiofile.executor_audiofile);
        holder.imgBtnPupupMenu.setId(audiofile.id_audiofile);
    }

    @Override
    public int getItemCount() {
        return listAudiofiles.size();
    }

    public void swipeList(List<Tables.AudiofileFull> list) {
        listAudiofiles = list;
    }
}

