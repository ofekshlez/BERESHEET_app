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

public class LstOfFragment extends Fragment {
    ListView lst;
    ArrayList<Quiz> quizzesList;
    QuizAdapter quizAdapter;
    Quiz lastSelected;
    Button btn_back;
    Bundle bundle;
    String strReceived, jsonString;
    TextView title;
    Dialog d;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lstof, container, false);
        bundle = this.getArguments();
        try {
            jsonString = new JSONObject()
                    .put("quiz", bundle.getString("quizB"))
                    .put("function", "getCollections")
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        quizzesList = new ArrayList<Quiz>();
        sendData(jsonString);
        if (strReceived.equals("error")) {
            createDialog(view);
        }
        else {
            String[] nameQ = strReceived.split("/");
            Quiz[] quizzes = new Quiz[nameQ.length];
            for (int i = 0; i < nameQ.length; i++) {
                quizzes[i] = new Quiz(nameQ[i]);
                quizzesList.add(quizzes[i]);
            }
        }



        quizAdapter = new QuizAdapter(view.getContext(), 0, 0, quizzesList);
        title = (TextView)view.findViewById(R.id.sub_title);
        title.setText(bundle.getString("quiz"));
        lst = (ListView)view.findViewById(R.id.lst);
        lst.setAdapter(quizAdapter);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lastSelected = quizAdapter.getItem(i);
                String name = lastSelected.getName();
                try {
                    jsonString = new JSONObject()
                            .put("quiz_name", name)
                            .put("function", "howManyQuizzes")
                            .toString();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(jsonString);
                sendData(jsonString);
                if (!strReceived.equals("0") && !strReceived.equals("error")) {
                    bundle.putString("quiz_chosen", name);
                    bundle.putString("quizB", bundle.getString("quizB"));
                    bundle.putInt("amount", Integer.valueOf(strReceived));
                    replaceFragment(new LstOf2Fragment());
                } else if (strReceived.equals("error")) {
                    createDialog(view);
                }
            }
        });

        btn_back = (Button)view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this::onClick);
        return view;
    }

    public void onClick(View v) {
        if (v == btn_back) {
            bundle.putString("id", bundle.getString("id"));
            replaceFragment(new HomeFragment());
        }
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

}