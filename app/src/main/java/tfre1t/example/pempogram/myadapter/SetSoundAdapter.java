package tfre1t.example.pempogram.myadapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tfre1t.example.pempogram.customviewers.RoundedImageView;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.savefile.Imager;

public class SetSoundAdapter extends RecyclerView.Adapter<SetSoundAdapter.SetSoundHolder> {

    private View.OnClickListener onItemClickListener;

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    private View.OnClickListener onMenuClickListener;

    public void setMenuClickListener(View.OnClickListener clickListener) {
        onMenuClickListener = clickListener;
    }

    final Context ctx;
    final int layout;
    View view;

    final int[] mTo;
    final Cursor cursor;
    final String[] mFrom;

    class SetSoundHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
        RoundedImageView imgAudiofile;
        TextView tvAudiofile, tvAuthor;
        ImageButton imgBtnPupupMenu;

        public SetSoundHolder(@NonNull View itemView) {
            super(itemView);
            imgAudiofile= itemView.findViewById(mTo[0]);
            tvAudiofile= itemView.findViewById(mTo[1]);
            tvAuthor= itemView.findViewById(mTo[2]);
            imgBtnPupupMenu = itemView.findViewById(mTo[3]);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
            imgBtnPupupMenu.setOnClickListener(onMenuClickListener);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return false;
        }
    }

    public SetSoundAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        ctx = context;
        cursor = c;
        this.layout = layout;
        mTo = to;
        mFrom = from;
    }

    @NonNull
    @Override
    public SetSoundAdapter.SetSoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new SetSoundAdapter.SetSoundHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetSoundAdapter.SetSoundHolder holder, int position) {
        cursor.moveToPosition(cursor.getCount() - (position + 1));
        holder.itemView.setId(cursor.getInt(cursor.getColumnIndex(DB.COLUMN_ID_AUDIOFILE)));
        holder.imgAudiofile.setImageBitmap(new Imager().setImageView(ctx, cursor.getString(cursor.getColumnIndex(mFrom[0]))));
        holder.tvAudiofile.setText(cursor.getString(cursor.getColumnIndex(mFrom[1])));
        holder.tvAuthor.setText(cursor.getString(cursor.getColumnIndex(mFrom[2])));
        holder.imgBtnPupupMenu.setId(cursor.getInt(cursor.getColumnIndex(DB.COLUMN_ID_AUDIOFILE)));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
}

