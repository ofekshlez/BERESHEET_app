package com.example.trying;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class SummeryFragment extends Fragment implements View.OnClickListener {
    ListView lst1, lst2;
    ArrayList<Result> resultArrayList1, resultArrayList2;
    ResultAdapter resultAdapter1, resultAdapter2;
    Button btn_again, btn_back;
    Dialog d;
    Bundle bundle;

    String strReceived, jsonString, jsonString2, strReceived2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bundle = this.getArguments();
        int[] answers = bundle.getIntArray("answers");
        try {
            jsonString = new JSONObject()
                    .put("quiz", bundle.getString("quiz"))
                    .put("quiz num", bundle.getInt("quiz num"))
                    .put("function", "getAnswers")
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        getAnswers(jsonString);
        int[] rightAnswers = Arrays.stream(strReceived.split("/", 0)).mapToInt(Integer::parseInt).toArray();
        int count = 0;
        for (int i = 0; i < 10; i++) {
            if (rightAnswers[i] == answers[i])
                count++;
        }
        Result r1 = new Result(1, rightAnswers[0] == answers[0]);
        Result r2 = new Result(2, rightAnswers[1] == answers[1]);
        Result r3 = new Result(3, rightAnswers[2] == answers[2]);
        Result r4 = new Result(4, rightAnswers[3] == answers[3]);
        Result r5 = new Result(5, rightAnswers[4] == answers[4]);
        Result r6 = new Result(6, rightAnswers[5] == answers[5]);
        Result r7 = new Result(7, rightAnswers[6] == answers[6]);
        Result r8 = new Result(8, rightAnswers[7] == answers[7]);
        Result r9 = new Result(9, rightAnswers[8] == answers[8]);
        Result r10 = new Result(10, rightAnswers[9] == answers[9]);

        resultArrayList1 = new ArrayList<Result>();
        resultArrayList2 = new ArrayList<Result>();
        resultArrayList1.add(r1);
        resultArrayList1.add(r2);
        resultArrayList1.add(r3);
        resultArrayList1.add(r4);
        resultArrayList1.add(r5);
        resultArrayList2.add(r6);
        resultArrayList2.add(r7);
        resultArrayList2.add(r8);
        resultArrayList2.add(r9);
        resultArrayList2.add(r10);

        View view = inflater.inflate(R.layout.fragment_summery, container, false);
        TextView result = (TextView)view.findViewById(R.id.result);
        result.setText(count + "/10");
        TextView quiz_num = (TextView)view.findViewById(R.id.quiz_num);
        quiz_num.setText("חידון מספר " + bundle.getInt("quiz num"));
        resultAdapter1 = new ResultAdapter(view.getContext(), 0, 0, resultArrayList1);
        resultAdapter2 = new ResultAdapter(view.getContext(), 0, 0, resultArrayList2);
        lst1 = (ListView)view.findViewById(R.id.lst1);
        lst1.setAdapter(resultAdapter1);
        lst2 = (ListView)view.findViewById(R.id.lst2);
        lst2.setAdapter(resultAdapter2);

        btn_back = (Button)view.findViewById(R.id.btn_home);
        btn_back.setOnClickListener(this);
        btn_again = (Button)view.findViewById(R.id.btn_again);
        btn_again.setOnClickListener(this);

        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    jsonString2 = new JSONObject()
                            .put("quiz", bundle.getString("quiz"))
                            .put("quiz num", bundle.getInt("quiz num"))
                            .put("function", "dialog")
                            .put("question num", i+1)
                            .put("answer", answers[i])
                            .toString();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                forDialog(jsonString2);
                String[] dialogA = strReceived2.split("/", 0);
                d = new Dialog(view.getContext());
                d.setContentView(R.layout.what_answer);
                d.setTitle("Answer");
                d.setCancelable(false);
                btn_back = (Button)d.findViewById(R.id.btn_back);
                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                TextView question_num = (TextView)d.findViewById(R.id.question_num);
                question_num.setText("שאלה " + (i+1));
                ImageView imgOfChosenAnswer = (ImageView)d.findViewById(R.id.imgOfChosenAnswer);
                if (resultArrayList1.get(i).isCorrect()) {
                    imgOfChosenAnswer.setImageResource(R.drawable.right);
                }
                else {
                    imgOfChosenAnswer.setImageResource(R.drawable.wrong);
                }
                TextView question = (TextView)d.findViewById(R.id.question);
                question.setText(dialogA[0]);
                TextView chosenAnswer = (TextView)d.findViewById(R.id.chosenAnswer);
                chosenAnswer.setText(dialogA[1]);
                TextView rightAnswer = (TextView)d.findViewById(R.id.rightAnswer);
                rightAnswer.setText(dialogA[2]);
                d.show();
            }
        });
        lst2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    jsonString2 = new JSONObject()
                            .put("quiz", bundle.getString("quiz"))
                            .put("quiz num", bundle.getInt("quiz num"))
                            .put("function", "dialog")
                            .put("question num", i+6)
                            .put("answer", answers[i+5])
                            .toString();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                forDialog(jsonString2);
                String[] dialogA = strReceived2.split("/", 0);
                d = new Dialog(view.getContext());
                d.setContentView(R.layout.what_answer);
                d.setTitle("Answer");
                d.setCancelable(false);
                btn_back = (Button)d.findViewById(R.id.btn_back);
                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                TextView question_num = (TextView)d.findViewById(R.id.question_num);
                question_num.setText("שאלה " + (i+6));
                ImageView imgOfChosenAnswer = (ImageView)d.findViewById(R.id.imgOfChosenAnswer);
                if (resultArrayList2.get(i).isCorrect()) {
                    imgOfChosenAnswer.setImageResource(R.drawable.right);
                }
                else {
                    imgOfChosenAnswer.setImageResource(R.drawable.wrong);
                }
                TextView question = (TextView)d.findViewById(R.id.question);
                question.setText(dialogA[0]);
                TextView chosenAnswer = (TextView)d.findViewById(R.id.chosenAnswer);
                chosenAnswer.setText(dialogA[1]);
                TextView rightAnswer = (TextView)d.findViewById(R.id.rightAnswer);
                rightAnswer.setText(dialogA[2]);
                d.show();
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == btn_back) {
            if (bundle.getChar("place") == 'H')
                replaceFragment(new HomeFragment());
            else
                replaceFragment(new TasksFragment());
        }
        else if (view == btn_again) {
            bundle.putString("quiz", bundle.getString("quiz"));
            bundle.putInt("quiz num", bundle.getInt("quiz num"));
            bundle.putString("id", bundle.getString("id"));
            replaceFragment(new QuizFragment());
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

    private void getAnswers(String info)
    {
        try {
            SocketTask send1 = new SocketTask(info);
            strReceived = send1.execute().get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            Log.e("Exception", e.toString());
        }
    }

    private void forDialog(String info)
    {
        try {
            SocketTask send2 = new SocketTask(info);
            strReceived2 = send2.execute().get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            Log.e("Exception", e.toString());
        }
    }
}