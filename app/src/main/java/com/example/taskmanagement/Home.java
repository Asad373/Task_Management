package com.example.taskmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.example.taskmanagement.Utils.Validations;
import com.example.taskmanagement.adapters.TaskAdapter;
import com.example.taskmanagement.application.BaseClass;
import com.example.taskmanagement.databinding.ActivityMainBinding;
import com.example.taskmanagement.databinding.AddTaskBinding;
import com.example.taskmanagement.model.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Home extends BaseClass implements View.OnClickListener{

    ActivityMainBinding binding;
    Dialog dialog;
    AddTaskBinding dialogueBinding;
    FirebaseDatabase dbRef;
    TaskAdapter adapter;
    ArrayList<TaskModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.getRoot();
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        //TaskList = new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance();
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialogueBinding = AddTaskBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogueBinding.getRoot());
        //load data
        loadTasks();


        //
        binding.addTask.setOnClickListener(this);
        binding.navLayput.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.signout:
                        SignOutuser();
                        break;
                    case R.id.tasks:
                        //loadDoneTasks();
                        break;
                }
                return true;
            }
        });
    }

    private void loadTasks() {
        list = new ArrayList<>();
            //ArrayList<TaskModel> mylist = new ArrayList<>();
            showProgress("Loading Tasks!");
            dbRef.getReference().child(Validations.mUID).child("NewTasks").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        TaskModel myvalue = datas.getValue(TaskModel.class);
                        list.add(myvalue);
                        loadAdapter(list);
                    }
                    //Toast.makeText(Home.this,TaskList.get(0).getDes().toString(),Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loadingBar.dismiss();
                    Toast.makeText(Home.this, databaseError + "", Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void loadAdapter(ArrayList<TaskModel> list) {

        binding.rcV.setHasFixedSize(true);
        binding.rcV.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(list, this);
        adapter.setDelete(new TaskAdapter.deleteItem() {
            @Override
            public void deleteClickListener(int position) {
             TaskModel model = list.get(position);
             customeDelBox("Are you sure to delete this task ?", model);
            }
        });
        adapter.setUpdate(new TaskAdapter.updateItem() {
            @Override
            public void updateClickListener(int position) {
                //Toast.makeText(Home.this, "Update called" + position, Toast.LENGTH_LONG).show();
                TaskModel model = list.get(position);
                updateTask(model);
            }
        });
        adapter.setMarkAsComplete(new TaskAdapter.MarkAsComplete() {
            @Override
            public void markCompleteListener(int position) {
                Toast.makeText(Home.this, "Mark as complete called" + position, Toast.LENGTH_LONG).show();
            }
        });
        binding.rcV.setAdapter(adapter);

    }

    private void updateTask(TaskModel model) {
      dialogueBinding.name.setText(model.getName());
      dialogueBinding.spinnerTaskP.setSelection(getIndex(model.getPriority()));
      dialogueBinding.dis.setText(model.getDes());
      showDialog(model,0, true);
    }

    private int getIndex( String myString){
        for (int i=0;i<dialogueBinding.spinnerTaskP.getCount();i++){
            if (dialogueBinding.spinnerTaskP.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    private void SignOutuser() {
       //mAuth.signOut();
       signUpSuccesMessage("Are you sure to logout?");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addTask:
                AddnewTask();
                break;
        }
    }

    private void AddnewTask() {
      showDialog(null,0,false);
    }

    public void signUpSuccesMessage(String message) throws Resources.NotFoundException {
        new AlertDialog.Builder(this)
                .setTitle("Info.")
                .setMessage(message)
                .setIcon(R.drawable.ic_info)
                .setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mAuth.signOut();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public  void showDialog(TaskModel model,int Position, boolean flag) {

        dialogueBinding.submit.setOnClickListener(v -> {
           if(flag){
               EditTask(model);
           }else{
               validateAddTask();
           }

        });
        dialogueBinding.submitC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

   public void validateAddTask(){
        if(dialogueBinding.name.getText().toString().equals("")){
            customAlertDialogue("Please enter Task name");
        }else if(dialogueBinding.spinnerTaskP.getSelectedItem().toString().equals("Select task Priority")){
            customAlertDialogue("Please select Task priority");
        }else if(dialogueBinding.dis.getText().toString().equals("")){
            customAlertDialogue("Please enter Task description");
        }else{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyyhhmmss", Locale.ENGLISH);
            String date = simpleDateFormat.format(new Date());
            HashMap<String, Object> mMAp = new HashMap<>();
            mMAp.put("id",date);
            mMAp.put("name", dialogueBinding.name.getText().toString());
            mMAp.put("priority", dialogueBinding.spinnerTaskP.getSelectedItem().toString());
            mMAp.put("des", dialogueBinding.dis.getText().toString());
            AddTask(mMAp, date);
        }
    }

    private void AddTask(HashMap<String, Object> map, String id) {
        showProgress("Adding Task, Please wait");
        dbRef.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dbRef.getReference().child(Validations.mUID).child("NewTasks").child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            customeInfoBoxL("Task created");

                            //Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();
                            //getdata();
                            //loadingBar.dismiss();
                            dialogueBinding.name.setText("");
                            dialogueBinding.dis.setText("");
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            loadingBar.dismiss();
                            customAlertDialogue(task.getException().getMessage());
                            //Toast.makeText(Home.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                dialog.dismiss();
            }
        });

    }

    public void customeInfoBoxL(String message) throws Resources.NotFoundException {
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
                                loadTasks();

                            }
                        }).show();
    }

    public void customeDelBox(String message, TaskModel model) throws Resources.NotFoundException {
        new AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage(message)
                .setIcon(R.drawable.ic_info)
                .setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do Something Here
                                dialog.dismiss();
                                //loadTasks();
                                DeleteTask(model);

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void DeleteTask(TaskModel model) {
        showProgress("Deleting task");
        dbRef.getReference().child(Validations.mUID).child("NewTasks").orderByChild("id").equalTo(model.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren())
                {
                     /*MyModel model1  = ds.getValue(MyModel.class);
                     model1.getDes();*/
                    ds.getRef().removeValue();
                    loadTasks();
                    //getdata();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
   public void EditTask(TaskModel model){
       if(dialogueBinding.name.getText().toString().equals("")){
           customAlertDialogue("Please enter Task name");
       }else if(dialogueBinding.spinnerTaskP.getSelectedItem().toString().equals("Select task Priority")){
           customAlertDialogue("Please select Task priority");
       }else if(dialogueBinding.dis.getText().toString().equals("")){
           customAlertDialogue("Please enter Task description");
       }else{
           loadingBar.show();
           String name = dialogueBinding.name.getText().toString();
           String prio = dialogueBinding.spinnerTaskP.getSelectedItem().toString();
           String des = dialogueBinding.dis.getText().toString();
           dbRef.getReference().child(Validations.mUID).child("NewTasks").child(model.getId()).child("name").setValue(name);
           dbRef.getReference().child(Validations.mUID).child("NewTasks").child(model.getId()).child("priority").setValue(prio);
           dbRef.getReference().child(Validations.mUID).child("NewTasks").child(model.getId()).child("des").setValue(des).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   loadTasks();
                   dialogueBinding.name.setText("");
                   dialogueBinding.dis.setText("");
                   dialog.dismiss();
               }
           });
       }
   }

}