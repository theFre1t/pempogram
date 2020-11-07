package tfre1t.example.pempogram.myadapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.customviewers.RoundedImageView;
import tfre1t.example.pempogram.database.DB_Table;

public class FavoriteAudioAdater extends RecyclerView.Adapter<FavoriteAudioAdater.FavoriteAudioHolder> {

    private static final String TAG = "myLog";

    private static View.OnClickListener onItemClickListener;

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    private final Context ctx;
    private List<DB_Table.AudiofileWithImg> list;
    private final int layout;

    static class FavoriteAudioHolder extends RecyclerView.ViewHolder {
        private final RoundedImageView imgv;
        private final TextView tvNameAudio;

        public FavoriteAudioHolder(@NonNull View itemView) {
            super(itemView);
            imgv= itemView.findViewById(R.id.imgFavAu);
            tvNameAudio= itemView.findViewById(R.id.tvNameAudio);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

    public FavoriteAudioAdater(Context context, List<DB_Table.AudiofileWithImg> list) {
        ctx = context;
        this.list = list;
        layout = R.layout.card_home_favoriteaudio_cardgrid;
    }

    @NonNull
    @Override
    public FavoriteAudioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new FavoriteAudioHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAudioHolder holder, int position) {
        if (position == list.size() && list.size() != 12) {
            holder.itemView.setId(-1);
            holder.tvNameAudio.setVisibility(View.GONE); //holder.tvNameAudio.setText("Добавить новый");
            setHolderImgBtnAddFavAu(holder);
        } else {
            DB_Table.AudiofileWithImg audiofile =  list.get(position);
            holder.itemView.setId(audiofile.id_audiofile);
            holder.tvNameAudio.setText(audiofile.name_audiofile);
            setImageView(holder, audiofile);
        }
    }

    private void setImageView(FavoriteAudioHolder holder, DB_Table.AudiofileWithImg audiofile) {
        try {
            FileInputStream fis = ctx.openFileInput(audiofile.img_collection);
            holder.imgv.setImageBitmap(BitmapFactory.decodeStream(fis));
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setHolderImgBtnAddFavAu(FavoriteAudioHolder holder) {
        holder.imgv.setImageResource(R.drawable.baseline_add_black_48);
        holder.imgv.setColorFilter(ctx.getResources().getColor(R.color.colorTextSecondary), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(list.size() == 12)
            count = list.size();
        else
            count = list.size()+1;
        return count;
    }

    public void swipeCursor(List<DB_Table.AudiofileWithImg> newList){
        list = newList;
    }
}
