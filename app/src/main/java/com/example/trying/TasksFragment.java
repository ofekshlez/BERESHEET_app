package com.example.trying;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trying.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TasksFragment extends Fragment {
    ListView lst;
    ArrayList<Task> taskArrayList;
    TaskAdapter taskAdapter;
    String strReceived, jsonString;
    TextView taskH;
    Dialog d;
    Bundle bundle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        bundle = this.getArguments();
        try {
            jsonString = new JSONObject()
                    .put("id", bundle.getString("id"))
                    .put("function", "tasks")
                    .toString();
            sendData(jsonString);
            if (strReceived.equals("error")) {
                dialogE(view);
            }
            else if (strReceived.equals("0")) {
                taskH = (TextView)view.findViewById(R.id.taskH);
                taskH.setText("אין משימות");
            }
            else {
                String[] fromS = strReceived.split("/", 0);
                taskArrayList = new ArrayList<Task>();
                for (int i = 0; i < fromS.length; i++) {
                    String[] task = fromS[i].split(",", 0);
                    taskArrayList.add(new Task(task[0], "חידון " + task[1], task[2].equals("true")));
                }
                taskAdapter = new TaskAdapter(view.getContext(), 0, 0, taskArrayList);
                lst = (ListView)view.findViewById(R.id.lst);
                lst.setAdapter(taskAdapter);
                lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Task lastSelected = taskAdapter.getItem(i);
                        String name = lastSelected.getName();
                        String[] temp = name.split(" ");
                        bundle.putInt("quiz num" ,Integer.valueOf(temp[1]));
                        bundle.putString("quiz", lastSelected.getPlace());
                        bundle.putChar("place", 'T');
                        replaceFragment(new QuizFragment());
                    }
                });
            }
        } catch (JSONException e) {
            dialogE(view);
        }
        return view;
    }

    private void signOut(){
        Intent intent = new Intent(getActivity(), SignIn.class);
        startActivity(intent);
    }

    private void sendData(String signUP)
    {
        try {
            SocketTask send1 = new SocketTask(signUP);
            strReceived = send1.execute().get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            strReceived = "error";
            Log.e("Exception", e.toString());
        }
    }

    private void dialogE(View view) {
        d = new Dialog(view.getContext());
        d.setContentView(R.layout.error);
        d.setTitle("Error");
        d.setCancelable(false);
        Button btn_back = (Button)d.findViewById(R.id.btn_signIn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        d.show();
    }

    private void replaceFragment(Fragment fragment) {
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}