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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class StudentsFragment extends Fragment {
    ListView lst;
    ArrayList<Student> studentList;
    StudentAdapter studentAdapter;
    Student lastSelected;
    Bundle bundle;
    String strReceived, jsonString;
    TextView title;
    Dialog d;
    String letters = "abcdefghijklmnopqrstuvwxyzABCDEFJHIJKLMNOPQRSTUVWXYZאבגדהוזחטיכךלמנסעפףצץקרשת";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_students, container, false);
        bundle = this.getArguments();
        try {
            jsonString = new JSONObject()
                    .put("id", bundle.getString("id"))
                    .put("function", "getStudents")
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        studentList = new ArrayList<Student>();
        sendData(jsonString);
        if (strReceived.equals("error")) {
            createDialog(view);
        }
        else if (strReceived.equals("0")) {
            title = (TextView)view.findViewById(R.id.students);
            title.setText("אין תלמידים");
        }
        else {
            String[] students = strReceived.split("/");
            for (int i = 0; i < students.length; i++) {
                String[] student = students[i].split(",");
                studentList.add(new Student(crack(student[0], 15), student[1]));
                studentAdapter = new StudentAdapter(view.getContext(), 0, 0, studentList);
                lst = (ListView)view.findViewById(R.id.lst);
                lst.setAdapter(studentAdapter);
            }
            lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    lastSelected = studentAdapter.getItem(i);
                    bundle.putString("studentN", lastSelected.getFull_name());
                    bundle.putString("studentID", lastSelected.getId());
                    replaceFragment(new StudentTaskFragment());
                }
            });
        }


        return view;
    }

    private void signOut(){
        Intent intent = new Intent(getActivity(), SignIn.class);
        startActivity(intent);
    }
    private void replaceFragment(Fragment fragment) {
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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

    private void createDialog(View view) {
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

    public String crack(String encrypt, int key) {
        String newM = "";
        for (int i = 0; i < encrypt.length(); i++) {
            int place = letters.indexOf(encrypt.charAt(i));
            if (place == -1) {
                newM += encrypt.charAt(i);
            }
            else {
                place -= key;
                if (place < 0)
                    place = place + letters.length();
                newM += letters.charAt(place);
            }
        }
        return newM;
    }

}