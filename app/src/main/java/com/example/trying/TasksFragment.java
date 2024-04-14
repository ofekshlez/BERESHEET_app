package com.example.trying;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class TasksFragment extends Fragment {
    ListView lst;
    ArrayList<Task> taskArrayList;
    TaskAdapter taskAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Task t1 = new Task("בראשית","חידון 1", true);
        Task t2 = new Task("ויקרא","חידון 5", false);
        Task t3 = new Task("במדבר","חידון 3", true);
        Task t4 = new Task("עמוס","חידון 7", false);
        Task t5 = new Task("חידון תנך","חידון 2019", false);

        taskArrayList = new ArrayList<Task>();
        taskArrayList.add(t1);
        taskArrayList.add(t2);
        taskArrayList.add(t3);
        taskArrayList.add(t4);
        taskArrayList.add(t5);

        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        taskAdapter = new TaskAdapter(view.getContext(), 0, 0, taskArrayList);
        view = inflater.inflate(R.layout.fragment_tasks, container, false);
        lst = (ListView)view.findViewById(R.id.lst);
        lst.setAdapter(taskAdapter);
        return view;
    }
}