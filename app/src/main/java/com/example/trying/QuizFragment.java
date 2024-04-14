package com.example.trying;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

public class QuizFragment extends Fragment implements View.OnClickListener {
    LinearLayout layout;
    Button btn_back, btn_next;
    TextView next_end, question_num, question, quiz_num;
    RadioButton answer1, answer2, answer3, answer4;
    RadioGroup radioGroup;
    Bundle bundle;
    int i = 1;
    int[] answers = new int[10];
    String strReceived, jsonString;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        layout = (LinearLayout)view.findViewById(R.id.last_question);
        layout.setVisibility(View.INVISIBLE);
        btn_next = (Button)view.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        btn_back = (Button)view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        question_num = (TextView)view.findViewById(R.id.question_num);
        next_end = (TextView)view.findViewById(R.id.next_end);
        answer1 = (RadioButton)view.findViewById(R.id.answer1);
        answer2 = (RadioButton)view.findViewById(R.id.answer2);
        answer3 = (RadioButton)view.findViewById(R.id.answer3);
        answer4 = (RadioButton)view.findViewById(R.id.answer4);
        radioGroup = (RadioGroup)view.findViewById(R.id.radioGroup);
        question = (TextView)view.findViewById(R.id.question);
        for (int j = 0; j < 10; j++) {
            answers[j] = 0;
        }
        bundle = this.getArguments();
        quiz_num =(TextView)view.findViewById(R.id.quiz_num);
        quiz_num.setText("חידון מספר " + bundle.getInt("quiz num"));

        try {
            jsonString = new JSONObject()
                    .put("quiz", bundle.getString("quiz"))
                    .put("quiz num", bundle.getInt("quiz num"))
                    .put("function", "getQuiz")
                    .put("question num", i)
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        getInfo(jsonString);
        String[] info = strReceived.split("/", 0);
        System.out.println(strReceived);
        question.setText(info[0]);
        answer1.setText(info[1]);
        answer2.setText(info[2]);
        answer3.setText(info[3]);
        answer4.setText(info[4]);


        return view;
    }
    private void buttons() {
        if (i == 1) {
            layout.setVisibility(View.INVISIBLE);
        }
        else if (i == 10) {
            next_end.setText("סיום החידון");
        }
        else {
            layout.setVisibility(View.VISIBLE);
            next_end.setText("לשאלה הבאה");
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btn_next) {
            if (i < 10) {
                whatAnswer();
                i++;
                question_num.setText("שאלה " + i);
                setIfChecked(answers[i-1]);
                buttons();
            }
            else {
                whatAnswer();
                boolean complete = false;
                for (int i = 0; i < 10 && !complete; i++) {
                    if (answers[i] != 0)
                        complete = true;
                }
                if (complete) {
                    try {
                        jsonString = new JSONObject()
                                .put("quiz", bundle.getString("quiz"))
                                .put("quiz num", bundle.getInt("quiz num"))
                                .put("function", "completeQuiz")
                                .put("id", bundle.getString("id"))
                                .toString();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    getInfo(jsonString);
                }
                bundle.putIntArray("answers", answers);
                bundle.putInt("quiz num", bundle.getInt("quiz num"));
                bundle.putString("quiz", bundle.getString("quiz"));
                bundle.putString("id", bundle.getString("id"));
                replaceFragment(new SummeryFragment());
            }
        }
        else if (view == btn_back) {
            whatAnswer();
            i--;
            question_num.setText("שאלה " + i);
            setIfChecked(answers[i-1]);
            buttons();
        }
        try {
            jsonString = new JSONObject()
                    .put("quiz", bundle.getString("quiz"))
                    .put("quiz num", bundle.getInt("quiz num"))
                    .put("function", "getQuiz")
                    .put("question num", i)
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        getInfo(jsonString);
        String[] info = strReceived.split("/", 0);
        question.setText(info[0]);
        answer1.setText(info[1]);
        answer2.setText(info[2]);
        answer3.setText(info[3]);
        answer4.setText(info[4]);
    }

    private void whatAnswer() {
        if (answer1.isChecked()) {
            answers[i-1] = 1;
        }
        else if (answer2.isChecked()) {
            answers[i-1] = 2;
        }
        else if (answer3.isChecked()) {
            answers[i-1] = 3;
        }
        else if (answer4.isChecked()){
            answers[i-1] = 4;
        }
        radioGroup.clearCheck();
    }
    private void setIfChecked(int num) {
        if (num == 1) {
            answer1.setChecked(true);
        }
        else if (num == 2) {
            answer2.setChecked(true);
        }
        else if (num == 3) {
            answer3.setChecked(true);
        }
        else if (num == 4){
            answer4.setChecked(true);
        }
    }
    private void replaceFragment(Fragment fragment) {
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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