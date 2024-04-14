package com.example.trying;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class QuizAdapter extends ArrayAdapter<Quiz> {
    List<Quiz> quizzes;
    Context context;
    public QuizAdapter(Context context, int resource, int textViewResourceId, List<Quiz> quizzes) {
        super(context, resource, textViewResourceId, quizzes);
        this.context = context;
        this.quizzes = quizzes;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.btn_for_lst,parent,false);
        TextView tvQuiz = (TextView)view.findViewById(R.id.quiz_name);
        Quiz temp = quizzes.get(position);
        tvQuiz.setText(temp.getName());
        return view;
    }
}
