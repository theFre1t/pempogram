package tfre1t.example.pempogram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.CustomViewers.RoundedImageView;
import tfre1t.example.pempogram.SaveFile.Imager;
import tfre1t.example.pempogram.database.Tables;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.OnlineLibraryHolder> {
    private static final String TAG = "myLog";

    private View.OnClickListener onItemClickListener;

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    private View.OnClickListener onAddItemClickListener;

    public void setAddItemClickListener(View.OnClickListener clickListener) {
        onAddItemClickListener = clickListener;
    }

    private final Context ctx;
    private List<Tables.Online_CollectionView> list;

    private final int layout;

    class OnlineLibraryHolder extends RecyclerView.ViewHolder {
        private final RoundedImageView imgCollection;
        private final TextView tvCollection, tvAuthor;
        private final ImageView imgBtnAddStatus;

        public OnlineLibraryHolder(@NonNull View itemView) {
            super(itemView);
            imgCollection = itemView.findViewById(R.id.imgCollection);
            tvCollection = itemView.findViewById(R.id.tvCollection);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            imgBtnAddStatus = itemView.findViewById(R.id.imgBtnAddStatus);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
            imgBtnAddStatus.setOnClickListener(onAddItemClickListener);
        }
    }

    public SearchAdapter(Context context, List<Tables.Online_CollectionView> listColl) {
        ctx = context;
        this.list = listColl;
        layout = R.layout.card_search_collection_classiclist;
    }

    @NonNull
    @Override
    public SearchAdapter.OnlineLibraryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(layout, parent, false);
        return new SearchAdapter.OnlineLibraryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.OnlineLibraryHolder holder, int position) {
        Tables.Online_CollectionView collection = list.get(position);
        holder.itemView.setId(collection.Online_Collection.id_online_collection);
        holder.imgCollection.setImageBitmap(new Imager().setImageView(ctx, collection.Online_Collection.img_file_preview_collection));
        holder.tvCollection.setText(collection.Online_Collection.name_collection);
        holder.tvAuthor.setText(collection.Online_Collection.author_collection);
        if(collection.collectionWithCollection != null){
            holder.imgBtnAddStatus.setVisibility(View.GONE);
        }
        holder.imgBtnAddStatus.setId(collection.Online_Collection.id_online_collection);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void swipeData(List<Tables.Online_CollectionView> listColl) {
        list = listColl;
    }
}

