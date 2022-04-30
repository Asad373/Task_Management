package com.example.taskmanagement.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagement.R;
import com.example.taskmanagement.databinding.RowTaskBinding;
import com.example.taskmanagement.model.TaskModel;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewholder>{

   ArrayList<TaskModel> mModelList;
   Context mContext;
   TaskViewholder holder;
   boolean isExpanded = false;
   deleteItem delete;
   updateItem update;
   MarkAsComplete mark;
    public  TaskAdapter(ArrayList<TaskModel> list, Context context){
        this.mContext = context;
        this.mModelList = list;
    }
    @NonNull
    @Override
    public TaskViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowTaskBinding binding = RowTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskAdapter.TaskViewholder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewholder holder, int position) {
        this.holder = holder;
        TaskModel mModel = mModelList.get(position);
        holder.binding.textView.setText(mModel.getName());
        if(mModel.getPriority().equals("High")){
            holder.binding.view.setBackgroundColor(mContext.getResources().getColor(R.color.high));
        }else if(mModel.getPriority().equals("Normal")){
            holder.binding.view.setBackgroundColor(mContext.getResources().getColor(R.color.normal));
        }else if(mModel.getPriority().equals("Low")){
            holder.binding.view.setBackgroundColor(mContext.getResources().getColor(R.color.low));
        }
        holder.binding.textView2.setText(mModel.getDes());

        holder.binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isExpanded){
                    isExpanded = true;
                    holder.binding.dicription.setVisibility(View.VISIBLE);

                }else{
                    isExpanded = false;
                    holder.binding.dicription.setVisibility(View.GONE);
                }
            }
        });

        holder.binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete.deleteClickListener(holder.getAdapterPosition());
            }
        });

        holder.binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update.updateClickListener(holder.getAdapterPosition());
            }
        });

        holder.binding.markAsComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mark.markCompleteListener(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }


    public class TaskViewholder extends RecyclerView.ViewHolder {
        RowTaskBinding binding;
        public TaskViewholder(RowTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    public void setDelete(deleteItem delete){
        this.delete = delete;
    }
    public void setUpdate(updateItem update){
        this.update = update;
    }
    public void setMarkAsComplete(MarkAsComplete mark){
        this.mark = mark;
    }

    public interface deleteItem{
        void deleteClickListener(int position);
    }

    public interface updateItem{
        void updateClickListener(int position);
    }

    public interface MarkAsComplete{
        void markCompleteListener(int position);
    }
}
