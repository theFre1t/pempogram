package tfre1t.example.pempogram.dialog;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import java.io.IOException;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.roundedimageview.RoundedImageView;
import tfre1t.example.pempogram.savefile.SaverAudio;
import tfre1t.example.pempogram.savefile.SaverImage;
import tfre1t.example.pempogram.ui.dashboard.fragment.Dashboard_SetSoundsCollection_Fragment;

import static android.app.Activity.RESULT_OK;

public class Dialog_Add_Sound extends DialogFragment implements View.OnClickListener {

    static final int RQS_OPEN_AUDIO = 2;

    View v;
    TextView dialogTvTitle, dialogTvNameAudiofile;
    EditText dialogEtExecutorSound, dialogEtNameSound;

    DB db;
    Dashboard_SetSoundsCollection_Fragment dsscf;
    long id;

    Uri selectedAudio;

    public Dialog_Add_Sound(DB db, Dashboard_SetSoundsCollection_Fragment fragment, long i) {
        this.db = db;
        dsscf = fragment;
        id = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_addedit_sound, null);
        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        dialogTvNameAudiofile = v.findViewById(R.id.dialogTvNameAudiofile);

        dialogEtExecutorSound =  v.findViewById(R.id.dialogEtExecutorSound);
        dialogEtNameSound = v.findViewById(R.id.dialogEtNameSound);

        v.findViewById(R.id.dialogBtnSelectAudiofile).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnAddEdit).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnCancel).setOnClickListener(this);
        v.findViewById(R.id.backgroundCl).setOnClickListener(this);

        dialogTvTitle.setText("Добавление записи");
        return v;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.dialogBtnSelectAudiofile:
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType("audio/*");
                startActivityForResult(intent, RQS_OPEN_AUDIO);
                break;
            case R.id.dialogBtnAddEdit:
                SaverAudio saverAudio = new SaverAudio();
                String nameSound = dialogEtNameSound.getText().toString();
                String executorSound = dialogEtExecutorSound.getText().toString();
                /*Cursor cImg = db.getDataCollectionById(id); cImg.moveToFirst();
                String imagefile = cImg.getString(cImg.getColumnIndex(DB.COLUMN_IMG_COLLECTION));*/
                if(!fillingCheck(nameSound, executorSound, selectedAudio)){
                    break;
                }
                String audiofile = saverAudio.saveAudio(dsscf.getContext(), selectedAudio);
                db.addRecAudiofile(nameSound, executorSound, audiofile, id);
                Toast.makeText(v.getContext(), "Запись добавлена", Toast.LENGTH_SHORT).show();
                dsscf.loadData();
                dismiss();
                break;
            case R.id.backgroundCl:
            case R.id.dialogBtnCancel:
                dismiss();
                break;
        }
    }


    int AudioFail = 0;
    private boolean fillingCheck(String nameSound, String executorSound, Uri audiofile) {
        if(!nameSound.equals("")){
            dialogEtNameSound.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
            if(!executorSound.equals("")){
                dialogEtExecutorSound.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
                if(audiofile != null){
                    dialogTvNameAudiofile.setTextColor(getResources().getColor(R.color.colorTextPrimary));
                    return true;
                }
                else {
                    dialogTvNameAudiofile.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    dialogTvNameAudiofile.setText(dialogTvNameAudiofile.getText()+"!");
                    if((AudioFail +=1) == 5){
                        dismiss();
                    }
                    else if(AudioFail > 3){
                        dialogTvNameAudiofile.setAllCaps(true);
                    }
                    return false;
                }
            }
            else {
                dialogEtExecutorSound.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
                return false;
            }
        }
        else {
            dialogEtNameSound.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case RQS_OPEN_AUDIO:
                if (resultCode == RESULT_OK){
                    selectedAudio = data.getData();
                    dialogTvNameAudiofile.setText(selectedAudio.getLastPathSegment());
                }
                break;
        }
    }
}
