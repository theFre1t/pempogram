package tfre1t.example.pempogram.dialog;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
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

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.fragment.dashboard.Dashboard_SetSoundsCollection_Fragment;

public class Dialog_Edit_Sound extends DialogFragment implements View.OnClickListener {

    MyTask mt;;
    Cursor cursor;

    View v;
    TextView dialogTvTitle, dialogTvNameAudiofile;
    EditText dialogEtExecutorSound, dialogEtNameSound;
    Button dialogBtnSelectAudiofile;

    DB db;
    Context ctx;
    Dashboard_SetSoundsCollection_Fragment dsscf;
    long id;

    public Dialog_Edit_Sound(DB db, Dashboard_SetSoundsCollection_Fragment fragment, long i) {
        this.db = db;
        dsscf = fragment;
        ctx = dsscf.getContext();
        id = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_edit_sound, null);
        goneView();
        loadEditData();

        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        v.findViewById(R.id.dialogBtnAddEdit).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnCancel).setOnClickListener(this);

        dialogTvTitle.setText("Редактирование записи");

        return v;
    }

    private void goneView() {
        dialogTvNameAudiofile = v.findViewById(R.id.dialogTvNameAudiofile);
        dialogBtnSelectAudiofile = v.findViewById(R.id.dialogBtnSelectAudiofile);
        dialogTvNameAudiofile.setVisibility(View.GONE);
        dialogBtnSelectAudiofile.setVisibility(View.GONE);
    }

    private void loadEditData() {
        mt = new MyTask();
        mt.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogBtnAddEdit:
                String nameSound = dialogEtNameSound.getText().toString();
                String executorSound = dialogEtExecutorSound.getText().toString();
                if(!fillingCheck(nameSound, executorSound)){
                    break;
                }
                db.updateRecAudiofile(id, nameSound, executorSound);
                Toast.makeText(v.getContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show();
                dsscf.loadData();
                dismiss();
                break;
            case R.id.dialogBtnCancel:
                dismiss();
                break;
        }
    }

    private boolean fillingCheck(String nameSound, String executorSound) {
        if(!nameSound.equals("")){
            dialogEtNameSound.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
            if(!executorSound.equals("")){
                dialogEtExecutorSound.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
                return true;
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

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogEtExecutorSound = v.findViewById(R.id.dialogEtExecutorSound);
            dialogEtNameSound = v.findViewById(R.id.dialogEtNameSound);
        }

        @Override
        protected Void doInBackground(Void... params) {
            cursor = db.getDataAudiofileByIdAudifile(id);
            cursor.moveToFirst();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialogEtNameSound.setText(cursor.getString(cursor.getColumnIndex(DB.COLUMN_NAME_AUDIOFILE)));
            dialogEtExecutorSound.setText(cursor.getString(cursor.getColumnIndex(DB.COLUMN_EXECUTOR_AUDIOFILE)));
        }
    }
}
