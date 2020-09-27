package tfre1t.example.pempogram.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
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

import java.io.IOException;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.customviewers.RoundedImageView;
import tfre1t.example.pempogram.savefile.SaverImage;
import tfre1t.example.pempogram.fragment.dashboard.Dashboard_Collection_Fragment;

import static android.app.Activity.RESULT_OK;

public class Dialog_Add_Collection extends DialogFragment implements View.OnClickListener {

    static final int GALLERY_REQUEST = 1;

    View v;
    TextView dialogTvTitle;
    EditText dialogEtAuthorCollection, dialogEtNameCollection;
    RoundedImageView dialogRmvImgCollection;

    DB db;
    Dashboard_Collection_Fragment dсf;
    Context ctx;

    public Dialog_Add_Collection(DB db, Dashboard_Collection_Fragment fragment) {
        this.db = db;
        dсf = fragment;
        ctx = dсf.getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_addedit_collection, null);
        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        dialogRmvImgCollection = v.findViewById(R.id.dialogRmvImgCollection);

        dialogEtAuthorCollection =  v.findViewById(R.id.dialogEtAuthorCollection);
        dialogEtNameCollection = v.findViewById(R.id.dialogEtNameCollection);

        v.findViewById(R.id.dialogRmvImgCollection).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnAddEdit).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnCancel).setOnClickListener(this);

        dialogTvTitle.setText("Добавление коллекции");
        return v;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.dialogRmvImgCollection:
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
                break;
            case R.id.dialogBtnAddEdit:
                SaverImage saverImage = new SaverImage();
                String NameColl = dialogEtNameCollection.getText().toString();
                String AuthorColl = dialogEtAuthorCollection.getText().toString();
                String ImgName = saverImage.saveImage(ctx, dialogRmvImgCollection);
                if(!fillingCheck(NameColl, AuthorColl)){
                    break;
                }
                db.addRecCollection(NameColl, AuthorColl, ImgName);
                Toast.makeText(v.getContext(), "Коллекция добавлена", Toast.LENGTH_SHORT).show();
                dсf.loadData();
                dismiss();
                break;
            case R.id.dialogBtnCancel:
                dismiss();
                break;
        }
    }

    private boolean fillingCheck(String nameColl, String authorColl) {
        if(!nameColl.equals("")){
            dialogEtNameCollection.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
            if(!authorColl.equals("")){
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
        }
    }
}
