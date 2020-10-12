package tfre1t.example.pempogram.myadapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import tfre1t.example.pempogram.customviewers.RoundedImageView;
import tfre1t.example.pempogram.helper.DragAndSwipeHelper.ItemTouchHelperAdapter;
import tfre1t.example.pempogram.helper.DragAndSwipeHelper.ItemTouchHelperViewHolder;
import tfre1t.example.pempogram.savefile.Imager;

public class CollectionAdater extends RecyclerView.Adapter<CollectionAdater.CollectionHolder> implements ItemTouchHelperAdapter {

    private View.OnClickListener onItemClickListener;

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    Context ctx;
    int layout;

    int[] mTo;
    Cursor cursor;
    String[] mFrom;

    ArrayList<Collection> collection;
    public class Collection{
        int id;
        Bitmap img;
        String name;
        String author;

        Collection(int id, Bitmap img, String name, String author){
            this.id = id;
            this.img = img;
            this.name = name;
            this.author = author;
        }
    }

    class CollectionHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
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

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public CollectionAdater(Context context, int layout, Cursor c, String[] from, int[] to) {
        ctx = context;
        cursor = c;
        this.layout = layout;
        mTo = to;
        mFrom = from;
        collection = new ArrayList<Collection>();
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
        Collection coll = new Collection(
                cursor.getInt(cursor.getColumnIndex(mFrom[0])),
                new Imager().setImageView(ctx, cursor.getString(cursor.getColumnIndex(mFrom[3]))),
                cursor.getString(cursor.getColumnIndex(mFrom[1])),
                cursor.getString(cursor.getColumnIndex(mFrom[2]))
        );
        collection.add(coll);
        holder.itemView.setId(coll.id);
        holder.tvColl.setText(coll.name);
        holder.tvAuthor.setText(coll.author);
        holder.imgv.setImageBitmap(coll.img);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    //drag & drop //////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(collection, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(collection, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        collection.remove(position);
        notifyItemRemoved(position);
    }
}
