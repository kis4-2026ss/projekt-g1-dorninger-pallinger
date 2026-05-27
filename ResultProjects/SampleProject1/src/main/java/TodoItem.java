package com.example.todo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class TodoItem {
    private final StringProperty text;
    private final BooleanProperty done;

    public TodoItem(String text) {
        this.text = new SimpleStringProperty(text);
        this.done = new SimpleBooleanProperty(false);
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public boolean isDone() {
        return done.get();
    }

    public BooleanProperty doneProperty() {
        return done;
    }

    public void toggleDone() {
        done.set(!done.get());
    }

    @Override
    public String toString() {
        return (isDone() ? "[x] " : "[ ] ") + getText();
    }
}
