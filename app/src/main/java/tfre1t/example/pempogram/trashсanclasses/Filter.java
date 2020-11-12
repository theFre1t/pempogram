package tfre1t.example.pempogram.trashсanclasses;

import java.util.ArrayList;
import java.util.List;

import tfre1t.example.pempogram.database.Room_DB;

public class Filter extends android.widget.Filter {

    List<Room_DB.Collection> list;
    List<Room_DB.Collection> newList;

    public Filter(List<Room_DB.Collection> list){
        this.list = list;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        constraint = constraint.toString().toLowerCase();
        FilterResults result = new FilterResults();
        if(constraint.toString().length()>0){
            ArrayList<Room_DB.Collection> FilterList = new ArrayList<>();

            for (int i = 0, l = list.size(); i<l; i++){
                Room_DB.Collection collection = list.get(i);
                String name = collection.name_collection;
                if(name.toLowerCase().contains(constraint))
                    FilterList.add(collection);
            }
            result.count = FilterList.size();
            result.values = FilterList;
        }
        else
        {
            synchronized(this)
            {
                result.values = list;
                result.count = list.size();
            }
        }
        return result;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        newList = (ArrayList<Room_DB.Collection>)results.values;
    }

    public List<Room_DB.Collection> getResult(){
        return newList;
    }
}
