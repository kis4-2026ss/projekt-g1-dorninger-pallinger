
import java.util.ArrayList;
import java.util.List;

public class TodoModel {
    private final List<TodoItem> items = new ArrayList<>();

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

    public List<TodoItem> getItems() {
        return new ArrayList<>(items);
    }
}