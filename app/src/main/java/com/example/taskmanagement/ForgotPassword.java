package com.example.taskmanagement;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.example.taskmanagement.application.BaseClass;
import com.example.taskmanagement.databinding.ActivityForgotPasswordBinding;
import com.example.taskmanagement.model.TaskModel;

public class ForgotPassword extends BaseClass {

    ActivityForgotPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.email.getText().toString().equals("")){
                    customAlertDialogue("Please enter Email");
                }else{
                    String email = binding.email.getText().toString();
                    showProgress("Sending Request");
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    loadingBar.dismiss();
                                    customeDelBox("A password rest link has been sent to your email, kindly check your email.");
                                    //dialog.dismiss();
                                } else {
                                    loadingBar.dismiss();
                                    customAlertDialogue(task.getException().getMessage());

                                }
                            });
                }
            }
        });
    }

    public void customeDelBox(String message) throws Resources.NotFoundException {
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
                                Intent intent = new Intent(ForgotPassword.this, Login.class);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();
                                //loadTasks();
                                //DeleteTask(model);

                            }
                        }).show();
    }
}