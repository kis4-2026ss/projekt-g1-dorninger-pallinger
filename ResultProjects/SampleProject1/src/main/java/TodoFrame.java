package com.example.todo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TodoFrame extends Stage {
    private final TodoModel model;
    private final ObservableList<TodoItem> listModel = FXCollections.observableArrayList();
    private final ListView<TodoItem> listView = new ListView<>(listModel);
    private final TextField inputField = new TextField();

    public TodoFrame(TodoModel model) {
        this.model = model;
        
        setTitle("Todo Application");
        setOnCloseRequest(e -> Platform.exit());

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            model.addItem(inputField.getText());
            inputField.setText("");
            refreshList();
        });

        BorderPane inputPanel = new BorderPane();
        inputPanel.setPadding(new Insets(10));
        inputPanel.setCenter(inputField);
        inputPanel.setRight(addButton);
        BorderPane.setMargin(addButton, new Insets(0, 0, 0, 10));

        Button toggleButton = new Button("Toggle Done");
        toggleButton.setOnAction(e -> {
            int index = listView.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                model.toggleItem(index);
                refreshList();
            }
        });

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> {
            int index = listView.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                model.removeItem(index);
                refreshList();
            }
        });

        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(10));
        buttonPanel.setAlignment(Pos.CENTER_LEFT);
        buttonPanel.getChildren().addAll(toggleButton, removeButton);

        BorderPane root = new BorderPane();
        root.setTop(inputPanel);
        root.setCenter(listView);
        root.setBottom(buttonPanel);

        Scene scene = new Scene(root, 400, 500);
        setScene(scene);
        centerOnScreen();
        
        refreshList();
    }

    private void refreshList() {
        listModel.clear();
        listModel.addAll(model.getItems());
    }
}
