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
import tfre1t.example.pempogram.savefile.Imager;

public class CollectionAdater extends RecyclerView.Adapter<CollectionAdater.CollectionHolder> {

    private View.OnClickListener onItemClickListener;

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    Context ctx;
    int layout;

    int[] mTo;
    Cursor cursor;
    String[] mFrom;


    class CollectionHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgv;
        TextView tvColl, tvAuthor;

        public CollectionHolder(@NonNull View itemView) {
            super(itemView);
            tvColl= itemView.findViewById(mTo[0]);
            tvAuthor= itemView.findViewById(mTo[1]);
            imgv= itemView.findViewById(mTo[2]);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

    public CollectionAdater(Context context, int layout, Cursor c, String[] from, int[] to) {
        ctx = context;
        cursor = c;
        this.layout = layout;
        mTo = to;
        mFrom = from;
    }

    @NonNull
    @Override
    public CollectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new CollectionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionHolder holder, int position) {
        cursor.moveToPosition(getItemCount() - (position + 1));
        holder.itemView.setId(cursor.getInt(cursor.getColumnIndex(mFrom[0])));
        holder.tvColl.setText(cursor.getString(cursor.getColumnIndex(mFrom[1])));
        holder.tvAuthor.setText(cursor.getString(cursor.getColumnIndex(mFrom[2])));
        holder.imgv.setImageBitmap(new Imager().setImageView(ctx, cursor.getString(cursor.getColumnIndex(mFrom[3]))));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

}
