package com.example.trying;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class LstOf2Fragment extends Fragment {
    ListView lst;
    ArrayList<Quiz> quizzesList;
    QuizAdapter quizAdapter;
    Button btn_back;
    Bundle bundle;
    TextView title;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bundle = this.getArguments();
        String quiz = bundle.getString("quiz_chosen");
        int amount = bundle.getInt("amount");
        Quiz[] quizzes = new Quiz[amount];
        quizzesList = new ArrayList<Quiz>();
        for (int i = 0; i < amount; i++) {
            quizzes[i] = new Quiz("חידון " + (i+1));
            quizzesList.add(quizzes[i]);
        }

        View view = inflater.inflate(R.layout.fragment_lstof2, container, false);
        quizAdapter = new QuizAdapter(view.getContext(), 0, 0, quizzesList);
        lst = (ListView)view.findViewById(R.id.lst);
        lst.setAdapter(quizAdapter);
        title = (TextView)view.findViewById(R.id.sub_title);
        title.setText(quiz);
        btn_back = (Button)view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new LstOfFragment();
                bundle.putString("id", bundle.getString("id"));
                bundle.putString("quizB", bundle.getString("quizB"));
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Quiz lastSelected = quizAdapter.getItem(i);
                String name = lastSelected.getName();
                String[] temp = name.split(" ");
                bundle.putInt("quiz num" ,Integer.valueOf(temp[1]));
                bundle.putString("quiz", quiz);
                replaceFragment(new QuizFragment());
            }
        });

        return view;
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