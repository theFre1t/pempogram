package com.tfre1t.pempogram.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tfre1t.pempogram.R;
import com.tfre1t.pempogram.database.Tables;
import com.tfre1t.pempogram.TrashcanClasses.FillingCheck;
import com.tfre1t.pempogram.ui.dashboard.DashboardViewModel;

public class Dialog_Edit_Sound extends DialogFragment implements View.OnClickListener {

    private DashboardViewModel dashboardViewModel;

    private View v;
    private Context ctx;

    private TextView dialogTvTitle;
    private EditText dialogEtExecutorSound, dialogEtNameSound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);
        v = inflater.inflate(R.layout.dialog_edit_sound, null);
        ctx = v.getContext();

        findViewById();
        loadEditData();

        dialogTvTitle.setText(R.string.dialog_title_edit_phrase);
        return v;
    }

    private void findViewById() {
        dialogEtExecutorSound = v.findViewById(R.id.dialogEtExecutorSound);
        dialogEtNameSound = v.findViewById(R.id.dialogEtNameSound);
        dialogTvTitle = v.findViewById(R.id.dialogTvTitle);
        v.findViewById(R.id.dialogBtnEdit).setOnClickListener(this);
        v.findViewById(R.id.dialogBtnCancel).setOnClickListener(this);
    }

    private void loadEditData() {
        dashboardViewModel.getDataSelectedAudio().observe(getViewLifecycleOwner(), new Observer<Tables.AudiofileWithImg>() {
            @Override
            public void onChanged(Tables.AudiofileWithImg audiofile) {
                dialogEtNameSound.setText(audiofile.name_audiofile);
                dialogEtExecutorSound.setText(audiofile.executor_audiofile);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialogBtnEdit) {
            String nameSound = dialogEtNameSound.getText().toString();
            String executorSound = dialogEtExecutorSound.getText().toString();
            if (fillingCheck(nameSound, executorSound)) {
                dashboardViewModel.updateAudiofile(nameSound, executorSound);
                Toast.makeText(v.getContext(), R.string.message_changes_saved, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        } else if (id == R.id.dialogBtnCancel) {
            dismiss();
        }
    }

    private boolean fillingCheck(String nameSound, String executorSound) {
        FillingCheck fillCheck = new FillingCheck();
        if (fillCheck.fillingCheckEditText(ctx, nameSound, dialogEtNameSound)) return false;
        return !fillCheck.fillingCheckEditText(ctx, executorSound, dialogEtExecutorSound);
    }
}
