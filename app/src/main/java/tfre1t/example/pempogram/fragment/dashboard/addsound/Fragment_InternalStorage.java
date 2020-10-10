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
    TextView tvTitle, tvNameAudiofile;
    EditText etExecutorSound, etNameSound;

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
        onFindViewById();
        tvTitle.setText("Новая запись");

        return v;
    }

    private void onFindViewById() {
        tvTitle = v.findViewById(R.id.tvTitle);
        tvNameAudiofile = v.findViewById(R.id.tvNameAudiofile);
        etExecutorSound =  v.findViewById(R.id.etExecutorSound);
        etNameSound = v.findViewById(R.id.etNameSound);
        v.findViewById(R.id.btnSelectAudiofile).setOnClickListener(this);
        v.findViewById(R.id.btnAdd).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnSelectAudiofile:
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType("audio/*");
                startActivityForResult(intent, RQS_OPEN_AUDIO);
                break;
            case R.id.btnAdd:
                SaverAudio saverAudio = new SaverAudio();
                String nameSound = etNameSound.getText().toString();
                String executorSound = etExecutorSound.getText().toString();
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
            etNameSound.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
            if(!executorSound.equals("")){
                etExecutorSound.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
                if(audiofile != null){
                    tvNameAudiofile.setTextColor(getResources().getColor(R.color.colorTextPrimary));
                    return true;
                }
                else {
                    tvNameAudiofile.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    tvNameAudiofile.setText(tvNameAudiofile.getText()+"!");
                    return false;
                }
            }
            else {
                etExecutorSound.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
                return false;
            }
        }
        else {
            etNameSound.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
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
                    tvNameAudiofile.setText(selectedAudio.getLastPathSegment());
                }
                break;
        }
    }
}
