package com.example.todo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TodoModel {
    private final ObservableList<TodoItem> items = FXCollections.observableArrayList();

    public void addItem(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        items.add(new TodoItem(text.trim()));
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public void toggleItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.get(index).toggleDone();
        }
    }

    public ObservableList<TodoItem> getItems() {
        return items;
    }
}
