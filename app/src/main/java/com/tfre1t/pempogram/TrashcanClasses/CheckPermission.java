package com.tfre1t.pempogram.TrashcanClasses;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CheckPermission extends AppCompatActivity {

    public void setPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private static final int REQUEST_CODE_RECORD_AUDIO = 1;

    public boolean CheckPermissionRecord(Activity activity) {
        int permissionStatus = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_RECORD_AUDIO);
            return false;
        }
        return true;
    }

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2;

    public void CheckPermissionExternalStorage(Activity activity) {
        int permissionStatus = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    private static boolean checkRecord;
    public boolean getCheckRecord() {
        return checkRecord;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_RECORD_AUDIO:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkRecord = true;
                }
                else {
                    checkRecord = false;
                }
                break;
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }
                break;
        }
    }
}
