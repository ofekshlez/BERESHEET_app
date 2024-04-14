package com.example.trying;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task>{
    List<Task> tasks;
    Context context;
    public TaskAdapter(Context context, int resource, int textViewResourceId, List<Task> tasks) {
        super(context, resource, textViewResourceId, tasks);
        this.context = context;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.btn_for_tasks,parent,false);
        TextView place = (TextView)view.findViewById(R.id.placeOfQuiz);
        TextView name = (TextView)view.findViewById(R.id.quiz_name);
        ImageView complete = (ImageView)view.findViewById(R.id.img);
        Task temp = tasks.get(position);
        if(temp.isComplete()) {
            complete.setImageResource(R.drawable.yes);
        }
        else {
            complete.setImageResource(R.drawable.not_complete);
        }
        place.setText(temp.getPlace());
        name.setText(temp.getName());
        return view;
    }
}
