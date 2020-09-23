package tfre1t.example.pempogram.myadapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.roundedimageview.RoundedImageView;

public class SetSoundAdapter extends SimpleCursorAdapter {

    final Context ctx;
    final int layout;

    final int[] mTo;
    final Cursor cursor;
    final String[] mOriginalFrom;

    public SetSoundAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        ctx = context;
        cursor = c;
        this.layout = layout;
        mTo = to;
        mOriginalFrom = from;
    }

    @Override
    public void setViewImage(ImageView v, String value) {
        try {
            FileInputStream fis = v.getContext().openFileInput(value);
            v.setImageBitmap(BitmapFactory.decodeStream(fis));
            fis.close();
        } catch (NumberFormatException | FileNotFoundException nfe) {
            v.setImageURI(Uri.parse(value));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

