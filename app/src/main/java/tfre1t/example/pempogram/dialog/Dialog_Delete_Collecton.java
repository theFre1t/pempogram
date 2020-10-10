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

public class Dialog_Delete_Collecton extends DialogFragment implements View.OnClickListener {

    View v;
    TextView dialogTvTitle, dialogTvText;
    CheckBox chbCheckFullDel;

    DB db;
    long id;

    public Dialog_Delete_Collecton(DB db, long id_collection) {
        this.db = db;
        id = id_collection;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_delete, null);
        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        dialogTvText = v.findViewById(R.id.dialogTvText);
        chbCheckFullDel = v.findViewById(R.id.chbCheckFullDel);

        v.findViewById(R.id.dialogBtnYes).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnCancel).setOnClickListener(this);

        dialogTvTitle.setText("Удалить коллекцию");
        dialogTvText.setText("Вы действительно хотите удалить коллекцию?" +
                             "\nОтменить действие будет невозможно");
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogBtnYes:
                db.delRecCollection(id, chbCheckFullDel.isChecked());
                getFragmentManager().popBackStack();
                Toast.makeText(v.getContext(), "Коллекция удалена", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.dialogBtnCancel:
                dismiss();
                break;
        }
    }
}
