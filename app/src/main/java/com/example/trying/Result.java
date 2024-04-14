package com.example.trying;

public class Result {
    private int question_num;
    private boolean correct;
    public Result(int question_num, boolean correct) {
        this.question_num = question_num;
        this.correct = correct;
    }

    public int getQuestion_num() {
        return question_num;
    }
    public boolean isCorrect() {
        return correct;
    }
    public void setQuestion_num(int question_num) {
        this.question_num = question_num;
    }
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
