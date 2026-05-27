import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Canvas extends javafx.scene.canvas.Canvas {
    private double X1, Y1, X2, Y2;
    private GraphicsContext gc;
    private Image img, background, undoTemp, redoTemp;
    ArrayList<Shape> shapes = new ArrayList<>();
    private final SizedStack<Image> undoStack = new SizedStack<>(12);
    private final SizedStack<Image> redoStack = new SizedStack<>(12);
    private Rectangle shape;
    private Point2D startPoint;
    
    // To mimic Swing's foreground color
    private Color foreground = Color.BLACK;

    public Canvas() {
        this(800, 600);
    }

    public Canvas(double width, double height) {
        super(width, height);
        gc = getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        
        // Initial clear to white
        clear();
        defaultListener();
    }

    public void save(File file) {
        try {
            WritableImage snapshot = this.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "PNG", file);
        } catch (IOException ex) {
            // Logic preserved: empty catch
        }
    }

    public void load(File file) {
        try {
            Image loadedImage = new Image(file.toURI().toString());
            gc.drawImage(loadedImage, 0, 0);
        } catch (Exception ex) {
            // Logic preserved: empty catch
        }
    }

    public void defaultListener() {
        setOnMousePressed(e -> {
            saveToStack();
            X2 = e.getX();
            Y2 = e.getY();
        });

        setOnMouseDragged(e -> {
            X1 = e.getX();
            Y1 = e.getY();

            gc.strokeLine(X2, Y2, X1, Y1);
            X2 = X1;
            Y2 = Y1;
        });
        
        setOnMouseReleased(null);
    }

    public void addRectangle(Rectangle rectangle, Color color) {
        gc.setStroke(color);
        gc.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    public void red() {
        gc.setStroke(Color.RED);
        gc.setFill(Color.RED);
    }

    public void black() {
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
    }

    public void magenta() {
        gc.setStroke(Color.MAGENTA);
        gc.setFill(Color.MAGENTA);
    }

    public void green() {
        gc.setStroke(Color.GREEN);
        gc.setFill(Color.GREEN);
    }

    public void blue() {
        gc.setStroke(Color.BLUE);
        gc.setFill(Color.BLUE);
    }

    public void gray() {
        gc.setStroke(Color.GRAY);
        gc.setFill(Color.GRAY);
    }

    public void orange() {
        gc.setStroke(Color.ORANGE);
        gc.setFill(Color.ORANGE);
    }

    public void yellow() {
        gc.setStroke(Color.YELLOW);
        gc.setFill(Color.YELLOW);
    }

    public void pink() {
        gc.setStroke(Color.PINK);
        gc.setFill(Color.PINK);
    }

    public void cyan() {
        gc.setStroke(Color.CYAN);
        gc.setFill(Color.CYAN);
    }

    public void lightGray() {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setFill(Color.LIGHTGRAY);
    }

    public void picker(Color color) {
        gc.setStroke(color);
        gc.setFill(color);
    }

    public void clear() {
        if (background != null) {
            setImage(background);
        } else {
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, getWidth(), getHeight());
            gc.setStroke(Color.BLACK);
            gc.setFill(Color.BLACK);
        }
    }

    public void undo() {
        if (undoStack.size() > 0) {
            undoTemp = undoStack.pop();
            redoStack.push(this.snapshot(null, null));
            setImage(undoTemp);
        }
    }

    public void redo() {
        if (redoStack.size() > 0) {
            redoTemp = redoStack.pop();
            undoStack.push(this.snapshot(null, null));
            setImage(redoTemp);
        }
    }

    public void pencil() {
        defaultListener();
    }

    public void rect() {
        setOnMousePressed(e -> {
            saveToStack();
            startPoint = new Point2D(e.getX(), e.getY());
            shape = new Rectangle();
            undoTemp = this.snapshot(null, null);
        });

        setOnMouseDragged(e -> {
            double x = Math.min(startPoint.getX(), e.getX());
            double y = Math.min(startPoint.getY(), e.getY());
            double width = Math.abs(startPoint.getX() - e.getX());
            double height = Math.abs(startPoint.getY() - e.getY());

            shape.setX(x);
            shape.setY(y);
            shape.setWidth(width);
            shape.setHeight(height);

            gc.drawImage(undoTemp, 0, 0);
            gc.strokeRect(x, y, width, height);
        });

        setOnMouseReleased(e -> {
            if (shape != null && (shape.getWidth() != 0 || shape.getHeight() != 0)) {
                addRectangle(shape, foreground);
            }
            shape = null;
        });
    }

    private void setImage(Image img) {
        this.img = img;
        gc.drawImage(img, 0, 0);
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
    }

    public void setBackground(Image img) {
        background = img; // In JavaFX Image is immutable, so "copying" is less critical
        setImage(img);
    }

    private void saveToStack() {
        undoStack.push(this.snapshot(null, null));
    }

    public void setThickness(int thick) {
        gc.setLineWidth(thick);
    }
}
