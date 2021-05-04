package com.tfre1t.pempogram.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import com.tfre1t.pempogram.R;
import com.tfre1t.pempogram.CustomViewers.RoundedImageView;
import com.tfre1t.pempogram.database.Room_DB;
import com.tfre1t.pempogram.helper.DragAndSwipeHelper.ItemTouchHelperAdapter;
import com.tfre1t.pempogram.helper.DragAndSwipeHelper.ItemTouchHelperViewHolder;
import com.tfre1t.pempogram.SaveFile.Imager;

public class CollectionAdater extends RecyclerView.Adapter<CollectionAdater.CollectionHolder> implements ItemTouchHelperAdapter {

    private View.OnClickListener onItemClickListener;
    //private final onStartDragListener mDragStartListener;

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    private final Context ctx;

    private List<Room_DB.Collection> list;
    private int layout;

    class CollectionHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        //private final ImageView imgHandle;
        private final RoundedImageView imgv;
        private final TextView tvColl;
        private final TextView tvAuthor;

        public CollectionHolder(@NonNull View itemView) {
            super(itemView);
            tvColl= itemView.findViewById(R.id.tvCollection);
            tvAuthor= itemView.findViewById(R.id.tvAuthor);
            imgv= itemView.findViewById(R.id.imgCollection);
            //imgHandle = itemView.findViewById(R.id.imgVHandle);

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

    public CollectionAdater(Context context, int layout, List<Room_DB.Collection> list/*, onStartDragListener dragStartListener*/) {
        ctx = context;
        this.list = list;
        this.layout = layout;
        //mDragStartListener = dragStartListener;
    }

    @NonNull
    @Override
    public CollectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new CollectionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionHolder holder, int position) {
        Room_DB.Collection collection = list.get(position);
        holder.itemView.setId(collection.id_collection);
        holder.tvColl.setText(collection.name_collection);
        holder.tvAuthor.setText(collection.author_collection);
        holder.imgv.setImageBitmap(new Imager().setImageView(ctx, collection.img_collection, false));
        /*holder.imgHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN){
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void swipeCursor(List<Room_DB.Collection> newList) {
        list = newList;
    }

    //drag & drop //////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }
}
