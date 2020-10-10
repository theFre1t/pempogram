package tfre1t.example.pempogram.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.database.DB;
import tfre1t.example.pempogram.fragment.dashboard.Dashboard_SetSoundsCollection_Fragment;

public class Dialog_Delete_Sound extends DialogFragment implements View.OnClickListener {

    View v;
    TextView dialogTvTitle, dialogTvText;

    DB db;
    Dashboard_SetSoundsCollection_Fragment dsscf;
    long id;

    public Dialog_Delete_Sound(DB db, Dashboard_SetSoundsCollection_Fragment fragment, long i) {
        this.db = db;
        dsscf = fragment;
        id = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_delete, null);
        goneView();

        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        dialogTvText = v.findViewById(R.id.dialogTvText);

        v.findViewById(R.id.dialogBtnYes).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnCancel).setOnClickListener(this);

        dialogTvTitle.setText("Удалить запись");
        dialogTvText.setText("Вы действительно хотите удалить запись?" +
                             "\nОтменить действие будет невозможно");
        return v;
    }

    private void goneView() {
        CheckBox chbCheckFullDel = v.findViewById(R.id.chbCheckFullDel);
        chbCheckFullDel.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogBtnYes:
                db.delRecAudiofile(id);
                Toast.makeText(v.getContext(), "Запись удалена", Toast.LENGTH_SHORT).show();
                dsscf.loadData();
                dismiss();
                break;
            case R.id.dialogBtnCancel:
                dismiss();
                break;
        }
    }
}
