package com.tfre1t.pempogram.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.tfre1t.pempogram.R;
import com.tfre1t.pempogram.ui.dashboard.DashboardViewModel;

public class Dialog_Delete_Sound extends DialogFragment implements View.OnClickListener {

    private DashboardViewModel dashboardViewModel;

    private View v;

    private TextView dialogTvTitle, dialogTvText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);
        v = inflater.inflate(R.layout.dialog_delete, null);

        findViewById();

        dialogTvTitle.setText(R.string.dialog_title_delete_phrase);
        dialogTvText.setText(R.string.dialog_text_delete_phrase);
        return v;
    }

    private void findViewById() {
        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        dialogTvText = v.findViewById(R.id.dialogTvText);

        v.findViewById(R.id.dialogBtnYes).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnCancel).setOnClickListener(this);
        v.findViewById(R.id.chbCheckFullDel).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialogBtnYes) {
            dashboardViewModel.deleteAudiofile();
            dismiss();
        } else if (id == R.id.dialogBtnCancel) {
            dismiss();
        }
    }
}
