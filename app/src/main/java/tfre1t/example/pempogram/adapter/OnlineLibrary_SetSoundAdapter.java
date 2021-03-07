package tfre1t.example.pempogram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tfre1t.example.pempogram.CustomViewers.RoundedImageView;
import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.SaveFile.Imager;
import tfre1t.example.pempogram.database.Room_DB;
import tfre1t.example.pempogram.database.Tables;

public class OnlineLibrary_SetSoundAdapter extends RecyclerView.Adapter<OnlineLibrary_SetSoundAdapter.SetSoundHolder> {

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

    public OnlineLibrary_SetSoundAdapter(Context context, List<Room_DB.Online_Audiofile> list) {
        ctx = context;
        listAudiofiles = list;
        layout = R.layout.card_dashboard_online_setsounds_collection_classiclist;
    }

    @NonNull
    @Override
    public OnlineLibrary_SetSoundAdapter.SetSoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new OnlineLibrary_SetSoundAdapter.SetSoundHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineLibrary_SetSoundAdapter.SetSoundHolder holder, int position) {
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

