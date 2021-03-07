package tfre1t.example.pempogram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.CustomViewers.RoundedImageView;
import tfre1t.example.pempogram.database.Room_DB;
import tfre1t.example.pempogram.database.Tables;

public class OnlineLibraryAdapter extends RecyclerView.Adapter<OnlineLibraryAdapter.OnlineLibraryHolder> {

    private View.OnClickListener onItemClickListener;

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    private final Context ctx;
    private List<Room_DB.Online_Collection> list;

    private final int layout;

    class OnlineLibraryHolder extends RecyclerView.ViewHolder {
        private final RoundedImageView imgCollection;
        private final TextView tvCollection, tvAuthor;

        public OnlineLibraryHolder(@NonNull View itemView) {
            super(itemView);
            imgCollection = itemView.findViewById(R.id.imgCollection);
            tvCollection = itemView.findViewById(R.id.tvCollection);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

    public OnlineLibraryAdapter(Context context, List<Room_DB.Online_Collection> listColl) {
        ctx = context;
        this.list = listColl;
        layout = R.layout.card_dashboard_online_collection_classiclist;
    }

    @NonNull
    @Override
    public OnlineLibraryAdapter.OnlineLibraryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new OnlineLibraryAdapter.OnlineLibraryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineLibraryAdapter.OnlineLibraryHolder holder, int position) {
        Room_DB.Online_Collection collection = list.get(position);
        holder.itemView.setId(collection.id_online_collection);
        Picasso.get().load(collection.img_preview_collection).resize(150, 150).centerCrop().into(holder.imgCollection);
        holder.tvCollection.setText(collection.name_collection);
        holder.tvAuthor.setText(collection.author_collection);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void swipeData(List<Room_DB.Online_Collection> listColl){
        list = listColl;
    }
}

