package com.example.taskmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.example.taskmanagement.application.BaseClass;
import com.example.taskmanagement.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class Signup extends BaseClass implements View.OnClickListener {

    ActivitySignupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ClickListeners();
    }

    private void ClickListeners() {
        binding.SignIn.setOnClickListener(this);
        binding.btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.SignIn:
                loadSignIn();
                break;
            case R.id.btnSignUp:
                ValidateUser();

        }
    }

    private void ValidateUser() {
        if(binding.email.getText().toString().equals("")){
            customAlertDialogue("Please add email.");
        }else if(binding.password.getText().toString().equals("")){
            customAlertDialogue("Please add password");
        }else{
            showProgress("Creating User please wait");
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            createUserDB(email, password);
        }
    }

    private void createUserDB(String email, String password) {
      mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
              if(task.isSuccessful()){
              loadingBar.dismiss();
              signUpSuccesMessage("User Successfully created, please login");
              }else{
                  loadingBar.dismiss();
                  signUpSuccesMessage(task.getException().getMessage());
              }
          }
      });
    }

    private void loadSignIn() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void signUpSuccesMessage(String message) throws Resources.NotFoundException {
        new AlertDialog.Builder(this)
                .setTitle("Info.")
                .setMessage(message)
                .setIcon(R.drawable.ic_info)
                .setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();

                            }
                        }).show();
    }
}