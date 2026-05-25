package com.example.todo;

public class TodoItem {
    private final String text;
    private boolean done;

    public TodoItem(String text) {
        this.text = text;
        this.done = false;
    }

    public String getText() {
        return text;
    }

    public boolean isDone() {
        return done;
    }

    public void toggleDone() {
        done = !done;
    }

    @Override
    public String toString() {
        return (done ? "[x] " : "[ ] ") + text;
    }
}