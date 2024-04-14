package com.example.trying;

public class Task {
    private String place;
    private String name;
    private boolean complete;

    public Task(String place, String name, boolean complete) {
        this.place = place;
        this.name = name;
        this.complete = complete;
    }

    public String getPlace() {
        return place;
    }
    public String getName() {
        return name;
    }
    public boolean isComplete() {
        return complete;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
