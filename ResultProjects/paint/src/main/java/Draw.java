import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Draw {

	Canvas canvas;
	Color color = Color.WHITE;
	Button clearButton, blackButton, blueButton, greenButton, redButton,
			colorPicker, magentaButton, grayButton, orangeButton, yellowButton,
			pinkButton, cyanButton, lightGrayButton, saveButton, loadButton,
			saveAsButton, rectangle, pencil, undoButton, redoButton;
	private FileChooser fileChooser;
	private File file;
	private Image saveIcon;
	private Image undoIcon;
	private Image redoIcon;
	private Image pencilIcon;
	private Image rectIcon;
	private int saveCounter = 0;
	private Label filenameBar, thicknessStat;
	private Slider thicknessSlider;
	private int width, height;

	public void setWH(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void openPaint() {
		Stage stage = new Stage();
		stage.setTitle("Paint (" + width + "X" + height + ")");

		canvas = new Canvas(width, height);

		// Load icons
		saveIcon = loadImage("save.png");
		undoIcon = loadImage("undo.png");
		redoIcon = loadImage("redo.png");
		pencilIcon = loadImage("pencil.png");
		rectIcon = loadImage("rect.png");

		pencil = new Button();
		if (pencilIcon != null) pencil.setGraphic(new ImageView(pencilIcon));
		pencil.setPrefSize(40, 40);
		pencil.setOnAction(e -> canvas.pencil());

		rectangle = new Button();
		if (rectIcon != null) rectangle.setGraphic(new ImageView(rectIcon));
		rectangle.setPrefSize(40, 40);
		rectangle.setOnAction(e -> canvas.rect());

		thicknessSlider = new Slider(0, 50, 1);
		thicknessSlider.setMajorTickUnit(25);
		thicknessSlider.setShowTickMarks(true);
		thicknessSlider.setPrefWidth(100);
		thicknessSlider.valueProperty().addListener((ov, old_val, new_val) -> {
			thicknessStat.setText(String.format("%d", new_val.intValue()));
			canvas.setThickness(new_val.intValue());
		});

		undoButton = new Button();
		if (undoIcon != null) undoButton.setGraphic(new ImageView(undoIcon));
		undoButton.setPrefSize(20, 20);
		undoButton.setOnAction(e -> canvas.undo());

		redoButton = new Button();
		if (redoIcon != null) redoButton.setGraphic(new ImageView(redoIcon));
		redoButton.setPrefSize(20, 20);
		redoButton.setOnAction(e -> canvas.redo());

		blackButton = createColorButton(Color.BLACK);
		blackButton.setOnAction(e -> canvas.black());

		blueButton = createColorButton(Color.BLUE);
		blueButton.setOnAction(e -> canvas.blue());

		greenButton = createColorButton(Color.GREEN);
		greenButton.setOnAction(e -> canvas.green());

		redButton = createColorButton(Color.RED);
		redButton.setOnAction(e -> canvas.red());

		magentaButton = createColorButton(Color.MAGENTA);
		magentaButton.setOnAction(e -> canvas.magenta());

		grayButton = createColorButton(Color.GRAY);
		grayButton.setOnAction(e -> canvas.gray());

		orangeButton = createColorButton(Color.ORANGE);
		orangeButton.setOnAction(e -> canvas.orange());

		yellowButton = createColorButton(Color.YELLOW);
		yellowButton.setOnAction(e -> canvas.yellow());

		pinkButton = createColorButton(Color.PINK);
		pinkButton.setOnAction(e -> canvas.pink());

		cyanButton = createColorButton(Color.CYAN);
		cyanButton.setOnAction(e -> canvas.cyan());

		lightGrayButton = createColorButton(Color.LIGHTGRAY);
		lightGrayButton.setOnAction(e -> canvas.lightGray());

		saveButton = new Button();
		if (saveIcon != null) saveButton.setGraphic(new ImageView(saveIcon));
		saveButton.setOnAction(e -> {
			if (saveCounter == 0) {
				fileChooser = new FileChooser();
				File selectedFile = fileChooser.showSaveDialog(stage);
				if (selectedFile != null) {
					file = selectedFile;
					saveCounter = 1;
					filenameBar.setText(file.getPath());
					canvas.save(file);
				}
			} else {
				filenameBar.setText(file.getPath());
				canvas.save(file);
			}
		});

		saveAsButton = new Button("Save As");
		saveAsButton.setOnAction(e -> {
			saveCounter = 1;
			fileChooser = new FileChooser();
			File selectedFile = fileChooser.showSaveDialog(stage);
			if (selectedFile != null) {
				file = selectedFile;
				filenameBar.setText(file.getPath());
				canvas.save(file);
			}
		});

		loadButton = new Button("Load");
		loadButton.setOnAction(e -> {
			fileChooser = new FileChooser();
			File selectedFile = fileChooser.showOpenDialog(stage);
			if (selectedFile != null) {
				file = selectedFile;
				filenameBar.setText(file.getPath());
				canvas.load(file);
			}
		});

		colorPicker = new Button("Color Picker");
		colorPicker.setOnAction(e -> {
			ColorPicker cp = new ColorPicker(color);
			Dialog<Color> dialog = new Dialog<>();
			dialog.setTitle("Pick your color!");
			dialog.getDialogPane().setContent(cp);
			dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			dialog.setResultConverter(b -> b == ButtonType.OK ? cp.getValue() : null);
			dialog.showAndWait().ifPresent(newColor -> {
				color = newColor;
				canvas.picker(color);
			});
		});

		clearButton = new Button("Clear");
		clearButton.setOnAction(e -> canvas.clear());

		filenameBar = new Label("No file");
		thicknessStat = new Label("1");

		VBox box = new VBox(5);
		box.setPadding(new Insets(5));
		box.getChildren().add(new Region() {{ setPrefHeight(40); }});
		
		HBox box1 = new HBox(5);
		box1.getChildren().addAll(thicknessSlider, thicknessStat);
		box.getChildren().add(box1);
		
		box.getChildren().add(new Region() {{ setPrefHeight(20); }});
		box.getChildren().add(undoButton);
		box.getChildren().add(new Region() {{ setPrefHeight(5); }});
		box.getChildren().add(redoButton);
		// pencil and rectangle were commented out in Swing layout

		FlowPane panel = new FlowPane(5, 5);
		panel.setPadding(new Insets(5));
		panel.getChildren().addAll(greenButton, blueButton, blackButton, redButton, magentaButton, 
				grayButton, orangeButton, yellowButton, pinkButton, cyanButton, lightGrayButton, 
				saveButton, saveAsButton, loadButton, colorPicker, clearButton);

		HBox panel1 = new HBox(5);
		panel1.setPadding(new Insets(5));
		panel1.getChildren().add(filenameBar);

		BorderPane container = new BorderPane();
		container.setTop(panel);
		container.setLeft(box);
		container.setCenter(canvas);
		container.setBottom(panel1);

		Scene scene = new Scene(container, width + 79, height + 110);
		stage.setScene(scene);
		stage.show();
	}

	private Image loadImage(String name) {
		try {
			var resource = getClass().getResource(name);
			if (resource != null) {
				return new Image(resource.toExternalForm());
			}
		} catch (Exception e) {
			// Ignore
		}
		return null;
	}

	private Button createColorButton(Color color) {
		Button button = new Button();
		button.setPrefSize(40, 40);
		String hex = String.format("#%02X%02X%02X",
				(int) (color.getRed() * 255),
				(int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
		button.setStyle("-fx-background-color: " + hex + ";");
		return button;
	}

}
