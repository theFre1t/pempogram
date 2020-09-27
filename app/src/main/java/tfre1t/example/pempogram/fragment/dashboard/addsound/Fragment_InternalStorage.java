package tfre1t.example.pempogram.fragment.dashboard.addsound;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.savefile.SaverAudio;

import static android.app.Activity.RESULT_OK;

public class Fragment_InternalStorage extends Fragment implements View.OnClickListener{

    static final int RQS_OPEN_AUDIO = 2;

    View v;
    TextView dialogTvTitle, dialogTvNameAudiofile;
    EditText dialogEtExecutorSound, dialogEtNameSound;

    DB db;
    long id;

    Uri selectedAudio;

    public Fragment_InternalStorage (DB db, long i) {
        this.db = db;
        id = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_addsound_internalstorage, null);
        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        dialogTvNameAudiofile = v.findViewById(R.id.dialogTvNameAudiofile);

        dialogEtExecutorSound =  v.findViewById(R.id.dialogEtExecutorSound);
        dialogEtNameSound = v.findViewById(R.id.dialogEtNameSound);

        v.findViewById(R.id.dialogBtnSelectAudiofile).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnAddEdit).setOnClickListener(this);

        dialogTvTitle.setText("Новая запись");
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
                if(!fillingCheck(nameSound, executorSound, selectedAudio)){
                    break;
                }
                String audiofile = saverAudio.saveAudio(v.getContext(), selectedAudio);
                db.addRecAudiofile(nameSound, executorSound, audiofile, id);
                Toast.makeText(v.getContext(), "Запись добавлена", Toast.LENGTH_SHORT).show();
                getActivity().setResult(1);
                getActivity().finish();
                break;
        }
    }


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
