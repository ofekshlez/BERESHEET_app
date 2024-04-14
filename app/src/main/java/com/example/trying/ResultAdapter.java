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

public class ResultAdapter extends ArrayAdapter<Result> {
    List<Result> results;
    Context context;
    public ResultAdapter(Context context, int resource, int textViewResourceId, List<Result> results) {
        super(context, resource, textViewResourceId, results);
        this.context = context;
        this.results = results;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.results,parent,false);
        TextView question = (TextView)view.findViewById(R.id.question_num);
        Result temp = results.get(position);
        question.setText("שאלה " + temp.getQuestion_num());
        ImageView correct = (ImageView)view.findViewById(R.id.correct);
        if (temp.isCorrect()) {
            correct.setImageResource(R.drawable.right);
        }
        else {
            correct.setImageResource(R.drawable.wrong);
        }
        return view;
    }
}
