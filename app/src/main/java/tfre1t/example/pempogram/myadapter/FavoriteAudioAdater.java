package tfre1t.example.pempogram.myadapter;

import android.content.Context;
import android.database.Cursor;
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

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.customviewers.RoundedImageView;

public class FavoriteAudioAdater extends RecyclerView.Adapter<FavoriteAudioAdater.FavoriteAudioHolder> {

    private View.OnClickListener onItemClickListener;

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    Context ctx;
    int layout;

    int[] mTo;
    Cursor cursor;
    String[] mOriginalFrom;
    View view;


    class FavoriteAudioHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgv;
        TextView tvNameAudio;

        public FavoriteAudioHolder(@NonNull View itemView) {
            super(itemView);
            imgv= itemView.findViewById(mTo[0]);
            tvNameAudio= itemView.findViewById(mTo[1]);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

    public FavoriteAudioAdater(Context context, int layout, Cursor c, String[] from, int[] to, DB db) {
        ctx = context;
        cursor = c;
        this.layout = layout;
        mTo = to;
        mOriginalFrom = from;
    }

    @NonNull
    @Override
    public FavoriteAudioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new FavoriteAudioHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAudioHolder holder, int position) {
        if (position == cursor.getCount() && cursor.getCount() != 12) {
            holder.itemView.setId(-1);
            setHolderImgBtnAddFavAu(holder);
            holder.tvNameAudio.setVisibility(View.GONE); //holder.tvNameAudio.setText("Добавить новый");
        } else {
            cursor.moveToPosition(cursor.getCount() - (position + 1));
            holder.itemView.setId(cursor.getInt(cursor.getColumnIndex(mOriginalFrom[0])));
            setImageView(holder);
            holder.tvNameAudio.setText(cursor.getString(cursor.getColumnIndex(mOriginalFrom[2])));
        }
    }

    private void setImageView(FavoriteAudioHolder holder) {
        try {
            FileInputStream fis = ctx.openFileInput(cursor.getString(cursor.getColumnIndex(mOriginalFrom[1])));
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
        if(cursor.getCount() == 12)
            count = cursor.getCount();
        else
            count = cursor.getCount()+1;
        return count;
    }
}
