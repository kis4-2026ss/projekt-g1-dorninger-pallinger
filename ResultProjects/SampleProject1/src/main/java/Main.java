import com.example.todo.TodoModel;
import com.example.todo.TodoFrame;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TodoModel model = new TodoModel();
        TodoFrame frame = new TodoFrame(model);
        frame.show();
    }
}
