package render;

import javafx.scene.canvas.Canvas;

/**
 * Created by rycla on 12/17/2016.
 */
public class ResizeableCanvas extends Canvas {

    public ResizeableCanvas(double width, double height){
        super.setWidth(width);
        super.setHeight(height);
    }

    public double minHeight(double width) {
        return 0;
    }

    public double maxHeight(double width) {
        return 1000;
    }

    public double minWidth(double height) {
        return 0;
    }

    public double maxWidth(double height) {
        return 10000;
    }

    public boolean isResizable() {
        return true;
    }

    public void resize(double width, double height) {
        super.setWidth(width);
        super.setHeight(height);
    }

    public void resizeWidth(double width){
        super.setWidth(width);
    }

    public void resizeHeight(double height){
        super.setHeight(height);
    }
}