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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.CustomViewers.RoundedImageView;
import tfre1t.example.pempogram.database.Room_DB;
import tfre1t.example.pempogram.SaveFile.Imager;
import tfre1t.example.pempogram.TrashcanClasses.FillingCheck;
import tfre1t.example.pempogram.ui.dashboard.DashboardViewModel;

import static android.app.Activity.RESULT_OK;

public class Dialog_Edit_Collection extends DialogFragment implements View.OnClickListener {

    private static final int GALLERY_REQUEST = 1;

    private DashboardViewModel dashboardViewModel;
    private Imager imager;

    private View v;
    private Context ctx;

    private String oldNameImg, nameImg;
    private Bitmap oldBitmap, bitmap;
    private boolean isSave;

    private TextView dialogTvTitle;
    private EditText dialogEtAuthorCollection, dialogEtNameCollection;
    private RoundedImageView dialogRmvImgCollection;
    private Button dialogBtnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);
        v = inflater.inflate(R.layout.dialog_addedit_collection, null);
        ctx = v.getContext();
        
        findViewById();
        loadEditData();

        isSave = false;
        dialogTvTitle.setText(R.string.dialog_title_editing_set);
        return v;
    }

    private void findViewById() {
        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        dialogRmvImgCollection = v.findViewById(R.id.dialogRmvImgCollection);

        dialogEtAuthorCollection =  v.findViewById(R.id.dialogEtAuthorCollection);
        dialogEtNameCollection = v.findViewById(R.id.dialogEtNameCollection);
        dialogBtnAdd = v.findViewById(R.id.dialogBtnAdd);

        v.findViewById(R.id.dialogRmvImgCollection).setOnClickListener(this);
        dialogBtnAdd.setOnClickListener(this);
        v.findViewById(R.id.dialogBtnCancel).setOnClickListener(this);
    }

    private void loadEditData() {
        dashboardViewModel.getDataSelectedColl().observe(getViewLifecycleOwner(), new Observer<Room_DB.Collection>() {
            @Override
            public void onChanged(Room_DB.Collection collection) {
                dialogRmvImgCollection.setImageBitmap(oldBitmap = new Imager().setImageView(ctx, oldNameImg = collection.img_collection, false));
                dialogEtNameCollection.setText(collection.name_collection);
                dialogEtAuthorCollection.setText(collection.author_collection);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialogRmvImgCollection) {
            Intent imagePickerIntent = new Intent(Intent.ACTION_PICK);
            imagePickerIntent.setType("image/*");
            startActivityForResult(imagePickerIntent, GALLERY_REQUEST);
            dialogBtnAdd.setEnabled(false);
            dialogBtnAdd.getBackground().mutate().setColorFilter(ctx.getResources().getColor(R.color.colorLightGray), PorterDuff.Mode.SRC_ATOP);
        } else if (id == R.id.dialogBtnAdd) {
            String NameColl = dialogEtNameCollection.getText().toString();
            String AuthorColl = dialogEtAuthorCollection.getText().toString();
            if (fillingCheck(NameColl, AuthorColl)) {
                if (nameImg == null) {
                    nameImg = oldNameImg;
                }
                else {
                    imager.deleteImage(ctx, oldNameImg);
                }
                dashboardViewModel.updateCollection(NameColl, AuthorColl, nameImg);
                isSave = true;
                Toast.makeText(v.getContext(), R.string.message_changes_saved, Toast.LENGTH_SHORT).show();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogRmvImgCollection.setImageBitmap(imager.setImageView(ctx, nameImg, false));
                            }
                        });
                    }
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogBtnAdd.setEnabled(true);
                            dialogBtnAdd.getBackground().mutate().setColorFilter(ctx.getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
                        }
                    });
                }
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
