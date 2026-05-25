import com.example.todo.TodoModel;
import com.example.todo.TodoFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TodoModel model = new TodoModel();
            TodoFrame frame = new TodoFrame(model);
            frame.setVisible(true);
        });
    }
}