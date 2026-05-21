
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;

public class TodoFrame extends JFrame {
    private final TodoModel model;
    private final DefaultListModel<TodoItem> listModel = new DefaultListModel<>();
    private final JList<TodoItem> itemList = new JList<>(listModel);
    private final JTextField inputField = new JTextField();

    public TodoFrame(TodoModel model) {
        this.model = model;

        setTitle("Swing Todo App");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton addButton = new JButton("Add");
        JButton toggleButton = new JButton("Toggle Done");
        JButton removeButton = new JButton("Remove");

        addButton.addActionListener(event -> {
            model.addItem(inputField.getText());
            inputField.setText("");
            refreshList();
        });

        toggleButton.addActionListener(event -> {
            model.toggleItem(itemList.getSelectedIndex());
            refreshList();
        });

        removeButton.addActionListener(event -> {
            model.removeItem(itemList.getSelectedIndex());
            refreshList();
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(toggleButton);
        buttonPanel.add(removeButton);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(itemList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void refreshList() {
        listModel.clear();
        for (TodoItem item : model.getItems()) {
            listModel.addElement(item);
        }
    }
}