package com.example.taskmanagement.Utils;

import android.widget.EditText;

public class Validations {
    public boolean CheckCondition(EditText text){
        return !text.getText().toString().equals("");
    }

    public static String mUID = null;
}
