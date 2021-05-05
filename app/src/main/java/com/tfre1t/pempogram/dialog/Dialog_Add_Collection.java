package com.tfre1t.pempogram.dialog;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.Objects;

import com.tfre1t.pempogram.R;
import com.tfre1t.pempogram.CustomViewers.RoundedImageView;
import com.tfre1t.pempogram.SaveFile.Imager;
import com.tfre1t.pempogram.TrashcanClasses.FillingCheck;
import com.tfre1t.pempogram.ui.dashboard.DashboardViewModel;

import static android.app.Activity.RESULT_OK;

public class Dialog_Add_Collection extends DialogFragment implements View.OnClickListener {

    private static final int GALLERY_REQUEST = 1;

    private DashboardViewModel dashboardViewModel;
    private Imager imager;

    private final Context ctx;
    private View v;

    private Bitmap bitmap;
    private String nameImg;
    private boolean isSave;

    private TextView dialogTvTitle;
    private EditText dialogEtAuthorCollection, dialogEtNameCollection;
    private RoundedImageView dialogRmvImgCollection;
    private Button dialogBtnAdd;

    public Dialog_Add_Collection(Context ctx) {
        this.ctx = ctx;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);
        v = inflater.inflate(R.layout.dialog_addedit_collection, null);

        findViewById();

        isSave = false;
        dialogTvTitle.setText(R.string.dialog_title_adding_set);
        return v;
    }

    private void findViewById() {
        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        dialogRmvImgCollection = v.findViewById(R.id.dialogRmvImgCollection);

        dialogEtAuthorCollection =  v.findViewById(R.id.dialogEtAuthorCollection);
        dialogEtNameCollection = v.findViewById(R.id.dialogEtNameCollection);
        dialogBtnAdd = v.findViewById(R.id.dialogBtnAdd);

        dialogBtnAdd.setOnClickListener(this);
        v.findViewById(R.id.dialogRmvImgCollection).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialogRmvImgCollection) {
            Intent intent = new Intent(Intent.ACTION_PICK).setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST);
            dialogBtnAdd.setEnabled(false);
            dialogBtnAdd.getBackground().mutate().setColorFilter(ctx.getResources().getColor(R.color.colorLightGray), PorterDuff.Mode.SRC_ATOP);
        } else if (id == R.id.dialogBtnAdd) {
            String NameColl = dialogEtNameCollection.getText().toString();
            String AuthorColl = dialogEtAuthorCollection.getText().toString();
            if (fillingCheck(NameColl, AuthorColl)) {
                dashboardViewModel.addNewColl(NameColl, AuthorColl, nameImg);
                isSave = true;
                Toast.makeText(ctx, R.string.message_set_added, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        } else if (id == R.id.dialogBtnCancel) {
            dismiss();
        }
    }

    private boolean fillingCheck(String nameColl, String authorColl) {
        FillingCheck fillCheck = new FillingCheck();
        if (fillCheck.fillingCheckEditText(ctx, nameColl, dialogEtNameCollection)) return false;
        return !fillCheck.fillingCheckEditText(ctx, authorColl, dialogEtAuthorCollection);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Thread(() -> {
            if (requestCode == GALLERY_REQUEST) {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imager = new Imager();
                    nameImg = imager.saveBitmapImage(ctx, bitmap);

                    requireActivity().runOnUiThread(() -> dialogRmvImgCollection.setImageBitmap(imager.setImageView(ctx, nameImg, false)));
                }

                requireActivity().runOnUiThread(() -> {
                    dialogBtnAdd.setEnabled(true);
                    dialogBtnAdd.getBackground().mutate().setColorFilter(ctx.getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
                });
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!isSave) {
            if (nameImg != null) {
                imager.deleteImage(ctx, nameImg);
            }
        }
    }
}
