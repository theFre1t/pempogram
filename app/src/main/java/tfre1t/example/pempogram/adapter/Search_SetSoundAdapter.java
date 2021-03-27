package tfre1t.example.pempogram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.Room_DB;

public class Search_SetSoundAdapter extends RecyclerView.Adapter<Search_SetSoundAdapter.SetSoundHolder> {

    private View.OnClickListener onItemClickListener;

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    private final Context ctx;

    private final int layout;
    private List<Room_DB.Online_Audiofile> listAudiofiles;

    class SetSoundHolder extends RecyclerView.ViewHolder {
        private final TextView tvAudiofile, tvAuthor;

        public SetSoundHolder(@NonNull View itemView) {
            super(itemView);
            tvAudiofile= itemView.findViewById(R.id.tvAudiofile);
            tvAuthor= itemView.findViewById(R.id.tvAuthor);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

    public Search_SetSoundAdapter(Context context, List<Room_DB.Online_Audiofile> list) {
        ctx = context;
        listAudiofiles = list;
        layout = R.layout.card_search_setsounds_collection_classiclist;
    }

    @NonNull
    @Override
    public Search_SetSoundAdapter.SetSoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new Search_SetSoundAdapter.SetSoundHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Search_SetSoundAdapter.SetSoundHolder holder, int position) {
        Room_DB.Online_Audiofile audiofile = listAudiofiles.get(position);
        holder.itemView.setId(audiofile.id_online_audiofile);
        holder.tvAudiofile.setText(audiofile.name_audiofile);
        holder.tvAuthor.setText(audiofile.author_audiofile);
    }

    @Override
    public int getItemCount() {
        return listAudiofiles.size();
    }

    public void swipeList(List<Room_DB.Online_Audiofile> list){
        listAudiofiles = list;
    }
}

