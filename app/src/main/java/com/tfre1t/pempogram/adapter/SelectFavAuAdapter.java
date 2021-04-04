package com.tfre1t.pempogram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.tfre1t.pempogram.R;
import com.tfre1t.pempogram.CustomViewers.RoundedImageView;
import com.tfre1t.pempogram.database.Tables;
import com.tfre1t.pempogram.SaveFile.Imager;

public class SelectFavAuAdapter extends RecyclerView.Adapter<SelectFavAuAdapter.SelectFavAuHolder> {

    private View.OnClickListener onItemClickListener;

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    private final Context ctx;
    private List<Tables.AudiofileWithImg> list;

    private final int layout;

    class SelectFavAuHolder extends RecyclerView.ViewHolder {
        private final RoundedImageView imgAudiofile;
        private final TextView tvAudiofile, tvAuthor;

        public SelectFavAuHolder(@NonNull View itemView) {
            super(itemView);
            imgAudiofile = itemView.findViewById(R.id.imgAudiofile);
            tvAudiofile = itemView.findViewById(R.id.tvNameAudio);
            tvAuthor = itemView.findViewById(R.id.tvExecutorAudio);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

    public SelectFavAuAdapter(Context context, List<Tables.AudiofileWithImg> list) {
        ctx = context;
        this.list = list;
        layout = R.layout.card_home_favoriteaudio_select_classiclist;
    }

    @NonNull
    @Override
    public SelectFavAuAdapter.SelectFavAuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new SelectFavAuAdapter.SelectFavAuHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectFavAuAdapter.SelectFavAuHolder holder, int position) {
        Tables.AudiofileWithImg audiofile = list.get(position);
        holder.itemView.setId(audiofile.id_audiofile);
        holder.imgAudiofile.setImageBitmap(new Imager().setImageView(ctx, audiofile.img_collection, false));
        holder.tvAudiofile.setText(audiofile.name_audiofile);
        holder.tvAuthor.setText(audiofile.executor_audiofile);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void swipeData(List<Tables.AudiofileWithImg> listSelAu){
        list = listSelAu;
    }
}

