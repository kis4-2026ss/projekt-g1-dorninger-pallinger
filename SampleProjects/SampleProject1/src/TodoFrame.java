package com.example.todo;

import javax.swing.*;
import java.awt.*;

public class TodoFrame extends JFrame {
    private final TodoModel model;
    private final DefaultListModel<TodoItem> listModel = new DefaultListModel<>();
    private final JList<TodoItem> listView = new JList<>(listModel);
    private final JTextField inputField = new JTextField();

    public TodoFrame(TodoModel model) {
        this.model = model;
        
        setTitle("Todo Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            model.addItem(inputField.getText());
            inputField.setText("");
            refreshList();
        });

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        JButton toggleButton = new JButton("Toggle Done");
        toggleButton.addActionListener(e -> {
            int index = listView.getSelectedIndex();
            if (index >= 0) {
                model.toggleItem(index);
                refreshList();
            }
        });

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            int index = listView.getSelectedIndex();
            if (index >= 0) {
                model.removeItem(index);
                refreshList();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.add(toggleButton);
        buttonPanel.add(removeButton);

        JPanel root = new JPanel(new BorderLayout());
        root.add(inputPanel, BorderLayout.NORTH);
        root.add(new JScrollPane(listView), BorderLayout.CENTER);
        root.add(buttonPanel, BorderLayout.SOUTH);

        add(root);
        refreshList();
    }

    private void refreshList() {
        listModel.clear();
        for (TodoItem item : model.getItems()) {
            listModel.addElement(item);
        }
    }
}