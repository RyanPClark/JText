package core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import render.ConcreteNode;
import render.Renderer;

/**
 * Created by rycla on 12/17/2016.
 */
public class Cursor extends Node {

    private Color color = Color.BLACK;
    private double render_width, height, render_length;
    private int tick_counter = 0;
    private ConcreteNode rootNode;
    private int onLine;
    private int position;
    private boolean selecting;
    private Selection selection;


    public static boolean overPosition;

    public boolean isMouseOver(){if(next != null)return next.isMouseOver(); return false;}

    public boolean isBlack(){
        return (tick_counter < render_length / 2);
    }

    public boolean wordwrap() {
        if(previous != null)
            return previous.wordwrap();
        return false;
    }

    public void perFrameUpdate(){
        tick_counter++;
        if(tick_counter == render_length)
            tick_counter = 0;

        height = Renderer.getFontSize() + 4;
    }

    public void updateOnDeltaMouse(){
        overPosition = rootNode.isMouseOver();
    }

    public void updateOnDeltaCursor(){
        position = positionInChain();
    }

    public void preRender(){
        x = (int) (previous.x + (previous.width + getWidth()) / 2 + Renderer.SPACING_X);
        y = (int) (previous.y);

        if(next != null)
            next.preRender();
    }

    private void draw(GraphicsContext gc){

        if(isBlack())
            gc.setFill(Renderer.textColor);
        else
            gc.setFill(Renderer.backgroundColor);
        gc.fillRect(x,y + height / 2,render_width,height);
    }

    private void changeCursorIcon(){
        if(Cursor.overPosition)
            Main.getTheScene().setCursor(javafx.scene.Cursor.TEXT);
        else
            Main.getTheScene().setCursor(javafx.scene.Cursor.DEFAULT);
    }

    public void removeSelection(){
        if(selection != null){
            Node temp = rootNode;
            while(temp != null){
                temp.selected = false;

                temp = temp.next;
            }
            selection = null;
        }
    }

    public void render(GraphicsContext gc, boolean doNext){
        if(selection == null)
            draw(gc);
        changeCursorIcon();
        if (doNext && next != null)
            next.render(gc);

    }

    public void render(GraphicsContext gc){

        onLine = Renderer.getNumLines();
        render(gc, true);
    }

    public Cursor() {
        this.color = Color.BLACK;
        this.x = 0;
        this.y = 0;
        this.render_width = 2;
        this.height = 16;
        this.render_length = 60;
    }

    public Cursor(Color color, int bottom_right_x, int bottom_right_y, int render_width, int height, int render_length) {
        this.color = color;
        this.x = bottom_right_x;
        this.y = bottom_right_y;
        this.render_width = render_width;
        this.height = height;
        this.render_length = render_length;
    }

    public Selection getSelection() {return selection;}

    public void setSelection(Selection selection) {this.selection = selection;}

    public boolean isSelecting() {return selecting;}

    public void setSelecting(boolean selecting) {this.selecting = selecting;}

    public int getOnLine() {return onLine;}

    public void setOnLine(int onLine) {this.onLine = onLine;}

    public void setRootNode(ConcreteNode rootNode) {this.rootNode = rootNode;}

    public int getPosition() {return position;}

    public void setPosition(int position) {this.position = position;}

    public int getTick_counter() {
        return tick_counter;
    }

    public void setTick_counter(int tick_counter) {
        this.tick_counter = tick_counter;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getRender_width() {
        return render_width;
    }

    public void setRender_width(double render_width) {
        this.render_width = render_width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getRender_length() {
        return render_length;
    }

    public void setRender_length(double render_length) {
        this.render_length = render_length;
    }
}
