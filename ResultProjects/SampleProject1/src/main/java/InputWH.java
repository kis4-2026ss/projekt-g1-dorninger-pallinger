import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class InputWH extends Application {
    public int width;
    public int height;
    Draw draw = new Draw();

    public InputWH() {
        // showInput will be called from start(Stage) when launched as Application
    }

    @Override
    public void start(Stage primaryStage) {
        showInput();
    }

    private void showInput() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Enter Canvas Width and Height");

        VBox p = new VBox(5);
        p.setPadding(new Insets(5));

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(2);

        Label widthLabel = new Label("Width");
        widthLabel.setMinWidth(60);
        widthLabel.setAlignment(Pos.CENTER_RIGHT);
        TextField widthField = new TextField();
        grid.add(widthLabel, 0, 0);
        grid.add(widthField, 1, 0);

        Label heightLabel = new Label("Height");
        heightLabel.setMinWidth(60);
        heightLabel.setAlignment(Pos.CENTER_RIGHT);
        TextField heightField = new TextField();
        grid.add(heightLabel, 0, 1);
        grid.add(heightField, 1, 1);

        HBox labels1 = new HBox();
        labels1.setAlignment(Pos.CENTER);
        labels1.getChildren().add(new Label("Minimum Width:900, Height: 800"));

        p.getChildren().addAll(grid, labels1);

        dialog.getDialogPane().setContent(p);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Focus logic
        Platform.runLater(() -> heightField.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                width = Integer.parseInt(widthField.getText());
                height = Integer.parseInt(heightField.getText());
                if (width < 900 || height < 800) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("W:900,H:800 Minimum required");
                    alert.showAndWait();
                }
                draw.setWH(width, height);
                draw.openPaint();
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter valid number!");
                alert.showAndWait();
            }
        } else {
            Platform.exit();
        }
    }

    /**
     * @param args
     *            none
     */
    public static void main(String[] args) {
        launch(args);
    }
}
