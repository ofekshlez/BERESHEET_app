package com.example.trying;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

public class InfoFragment extends Fragment {
    View view;
    TextView full_name, id, school, email, job;
    String strReceived, jsonString;
    Dialog d;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_info, container, false);
        full_name = (TextView)view.findViewById(R.id.full_name);
        id = (TextView)view.findViewById(R.id.id);
        school = (TextView)view.findViewById(R.id.school);
        email = (TextView)view.findViewById(R.id.email);
        job = (TextView)view.findViewById(R.id.job);

        Bundle bundle = this.getArguments();
        String idN = bundle.getString("id");

        try {
            jsonString = new JSONObject()
                    .put("id", idN)
                    .put("function", "getInfo")
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        getInfo(jsonString);

        if (strReceived.equals("error")) {
            full_name.setText("לא ידוע");
            id.setText("לא ידוע");
            school.setText("לא ידוע");
            email.setText("לא ידוע");
            email.setTextSize(40);
            job.setText("לא ידוע");
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
        else {
            String[] info = strReceived.split(",", 0);
            System.out.println(strReceived);
            full_name.setText(info[0]);
            id.setText(idN);
            school.setText(info[1]);
            email.setText(info[2]);
            job.setText(info[3]);
        }

        return view;
    }

    public void signOut(){
        Intent intent = new Intent(getActivity(), SignIn.class);
        startActivity(intent);
    }

    private void getInfo(String info)
    {
        try {
            SocketTask send1 = new SocketTask(info);
            strReceived = send1.execute().get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            Log.e("Exception", e.toString());
            strReceived = "error";
        }
    }

}