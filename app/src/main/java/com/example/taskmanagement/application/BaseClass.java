package com.example.taskmanagement.application;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class BaseClass extends AppCompatActivity {
    public FirebaseAuth mAuth;
    String mUID;
    public FirebaseDatabase mDb;
    public  ProgressDialog loadingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseDatabase.getInstance();

        loadingBar = new ProgressDialog(this);
        if(mAuth.getCurrentUser() != null){
            mUID = mAuth.getUid();
        }

    }

    public void customAlertDialogue(String message) throws Resources.NotFoundException {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(message)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do Something Here
                                dialog.dismiss();

                            }
                        }).show();
    }

    public void customeInfoBox(String message) throws Resources.NotFoundException {
        new AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage(message)
                .setIcon(R.drawable.ic_info)
                .setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do Something Here
                                dialog.dismiss();

                            }
                        }).show();
    }

    public  void showProgress(String Message){
        loadingBar.setTitle("Processing");
        loadingBar.setMessage(Message);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
    }
}
