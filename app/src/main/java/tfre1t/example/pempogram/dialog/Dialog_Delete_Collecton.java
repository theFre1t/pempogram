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
import androidx.lifecycle.ViewModelProvider;

import tfre1t.example.pempogram.R;
import tfre1t.example.pempogram.ui.dashboard.DashboardViewModel;

public class Dialog_Delete_Collecton extends DialogFragment implements View.OnClickListener {

    private DashboardViewModel dashboardViewModel;

    private View v;

    private TextView dialogTvTitle, dialogTvText;
    private CheckBox chbCheckFullDel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);
        v = inflater.inflate(R.layout.dialog_delete, null);

        findViewById();

        dialogTvTitle.setText(R.string.dialog_title_delete_set);
        dialogTvText.setText(R.string.dialog_text_delete_set);
        return v;
    }

    private void findViewById() {
        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        dialogTvText = v.findViewById(R.id.dialogTvText);
        chbCheckFullDel = v.findViewById(R.id.chbCheckFullDel);

        v.findViewById(R.id.dialogBtnYes).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialogBtnYes) {
            dashboardViewModel.deleteCollection(chbCheckFullDel.isChecked());
            getParentFragmentManager().popBackStack();
            Toast.makeText(v.getContext(), R.string.message_set_deleted, Toast.LENGTH_SHORT).show();
            dismiss();
        } else if (id == R.id.dialogBtnCancel) {
            dismiss();
        }
    }
}
