package com.example.todo3;

import java.util.Date;

public class TodoItem {
    private String task;
    private boolean isDone;
    private Date date;

    public TodoItem(String task, Date date) {
        this.task = task;
        this.date = date;
        this.isDone = false;
    }

    public String getTask() {
        return task;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public Date getDate() {
        return date;
    }
}
