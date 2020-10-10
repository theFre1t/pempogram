package tfre1t.example.pempogram.myadapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tfre1t.example.pempogram.customviewers.RoundedImageView;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.savefile.Imager;

public class SelectFavAuAdapter extends RecyclerView.Adapter<SelectFavAuAdapter.SelectFavAuHolder> {

    private View.OnClickListener onItemClickListener;

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    final Context ctx;
    final int layout;
    View view;

    final int[] mTo;
    final Cursor cursor;
    final String[] mFrom;

    class SelectFavAuHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgAudiofile;
        TextView tvAudiofile, tvAuthor;

        public SelectFavAuHolder(@NonNull View itemView) {
            super(itemView);
            imgAudiofile = itemView.findViewById(mTo[0]);
            tvAudiofile = itemView.findViewById(mTo[1]);
            tvAuthor = itemView.findViewById(mTo[2]);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

    public SelectFavAuAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        ctx = context;
        cursor = c;
        this.layout = layout;
        mTo = to;
        mFrom = from;
    }

    @NonNull
    @Override
    public SelectFavAuAdapter.SelectFavAuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new SelectFavAuAdapter.SelectFavAuHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectFavAuAdapter.SelectFavAuHolder holder, int position) {
        cursor.moveToPosition(cursor.getCount() - (position + 1));
        holder.itemView.setId(cursor.getInt(cursor.getColumnIndex(DB.COLUMN_ID_AUDIOFILE)));
        holder.imgAudiofile.setImageBitmap(new Imager().setImageView(ctx, cursor.getString(cursor.getColumnIndex(mFrom[0]))));
        holder.tvAudiofile.setText(cursor.getString(cursor.getColumnIndex(mFrom[1])));
        holder.tvAuthor.setText(cursor.getString(cursor.getColumnIndex(mFrom[2])));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
}

