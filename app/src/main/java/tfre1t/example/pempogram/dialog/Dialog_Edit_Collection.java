package tfre1t.example.pempogram.dialog;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.FileInputStream;
import java.io.IOException;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.customviewers.RoundedImageView;
import tfre1t.example.pempogram.savefile.SaverImage;
import tfre1t.example.pempogram.ui.dashboard.fragment.Dashboard_SetSoundsCollection_Fragment;

import static android.app.Activity.RESULT_OK;

public class Dialog_Edit_Collection extends DialogFragment implements View.OnClickListener {

    static final int GALLERY_REQUEST = 1;

    Bitmap bitmap;
    Cursor cursor_collection;

    View v;
    TextView dialogTvTitle;
    EditText dialogEtAuthorCollection, dialogEtNameCollection;
    RoundedImageView dialogRmvImgCollection;

    DB db;
    Context ctx;
    Dashboard_SetSoundsCollection_Fragment dsscf;
    long id_collection;

    public Dialog_Edit_Collection(DB db, Dashboard_SetSoundsCollection_Fragment fragment, long i) {
        this.db = db;
        dsscf = fragment;
        ctx = dsscf.getContext();
        id_collection = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_addedit_collection, null);
        loadEditData();

        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        dialogRmvImgCollection = v.findViewById(R.id.dialogRmvImgCollection);

        dialogEtAuthorCollection =  v.findViewById(R.id.dialogEtAuthorCollection);
        dialogEtNameCollection = v.findViewById(R.id.dialogEtNameCollection);

        v.findViewById(R.id.dialogRmvImgCollection).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnAddEdit).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnCancel).setOnClickListener(this);
        v.findViewById(R.id.backgroundCl).setOnClickListener(this);

        dialogTvTitle.setText("Редактирование коллекции");

        return v;
    }

    private void loadEditData() {
        new MyTask().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogRmvImgCollection:
                Intent imagePickerIntent = new Intent(Intent.ACTION_PICK);
                imagePickerIntent.setType("image/*");
                startActivityForResult(imagePickerIntent, GALLERY_REQUEST);
                break;
            case R.id.dialogBtnAddEdit:
                SaverImage saverImage = new SaverImage();
                long id = id_collection;
                String NameColl = dialogEtNameCollection.getText().toString();
                String AuthorColl = dialogEtAuthorCollection.getText().toString();
                String currentNameImg = cursor_collection.getString(cursor_collection.getColumnIndex(DB.COLUMN_IMG_COLLECTION));
                String newNameImg = saverImage.saveImage(ctx, dialogRmvImgCollection, bitmap, currentNameImg);
                if(!fillingCheck(NameColl, AuthorColl)){
                    break;
                }
                if(!newNameImg.equals(currentNameImg)){
                    db.delImgCollection(currentNameImg);
                }
                db.updateRecCollection(id, NameColl, AuthorColl, newNameImg);
                Toast.makeText(v.getContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show();
                dsscf.loadPresenColl();
                dismiss();
                break;
            case R.id.backgroundCl:
            case R.id.dialogBtnCancel:
                dismiss();
                break;
        }
    }

    private boolean fillingCheck(String nameSound, String executorSound) {
        if(!nameSound.equals("")){
            dialogEtNameCollection.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
            if(!executorSound.equals("")){
                dialogEtAuthorCollection.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
                return true;
            }
            else {
                dialogEtAuthorCollection.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
                return false;
            }
        }
        else {
            dialogEtNameCollection.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
            return false;
        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogEtAuthorCollection =  v.findViewById(R.id.dialogEtAuthorCollection);
            dialogEtNameCollection = v.findViewById(R.id.dialogEtNameCollection);
            dialogRmvImgCollection = v.findViewById(R.id.dialogRmvImgCollection);
        }

        @Override
        protected Void doInBackground(Void... params) {
            cursor_collection = db.getDataCollectionById(id_collection);
            cursor_collection.moveToFirst();
            try {
                FileInputStream fis = ctx.openFileInput(cursor_collection.getString(cursor_collection.getColumnIndex(DB.COLUMN_IMG_COLLECTION)));
                bitmap = BitmapFactory.decodeStream(fis);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialogRmvImgCollection.setImageBitmap(bitmap);
            dialogEtNameCollection.setText(cursor_collection.getString(cursor_collection.getColumnIndex(DB.COLUMN_NAME_COLLECTION)));
            dialogEtAuthorCollection.setText(cursor_collection.getString(cursor_collection.getColumnIndex(DB.COLUMN_AUTHOR_COLLECTION)));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap newImageBitmap = null;

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    try {
                        newImageBitmap = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialogRmvImgCollection.setImageBitmap(newImageBitmap);
                }
                break;
        }
    }
}
