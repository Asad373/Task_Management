package com.example.taskmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.taskmanagement.Utils.Validations;
import com.example.taskmanagement.application.BaseClass;
import com.example.taskmanagement.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class Login extends BaseClass implements View.OnClickListener {

    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.signUp.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.forgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signUp:
                loadSignup();
                break;

            case R.id.btnLogin:
                ValidateUser();
                break;
            case R.id.forgot:
                loadForgotPass();
                break;

        }
    }

    private void loadForgotPass() {
        startActivity(new Intent(Login.this, ForgotPassword.class));
    }


    private void ValidateUser() {
        if(binding.email.getText().toString().equals("")){
            customAlertDialogue("Please add email.");
        }else if(binding.password.getText().toString().equals("")){
            customAlertDialogue("Please add password");
        }else{
            showProgress("Login User please wait");
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            loginUser(email, password);
        }
    }
    private void loginUser(String email, String password) {
      mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
              loadingBar.dismiss();
              if(task.isSuccessful()){
                  Validations.mUID = task.getResult().getUser().getUid();
                  startActivity(new Intent(Login.this, Home.class));
                  finish();
              }else{
                 customAlertDialogue(task.getException().getMessage());
              }
          }
      });
    }
    private void loadSignup() {
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
    }


}