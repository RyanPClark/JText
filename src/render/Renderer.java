package render;

import core.Cursor;
import core.Interactions;
import core.ScrollBar;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Created by rycla on 12/19/2016.
 */
public class Renderer {

    public static final Color backgroundColor = Color.WHITE;
    public static final Color textColor = Color.BLACK;
    public static final Color selectionColor = Color.LIGHTBLUE;
    public static final Color selectedTextColor = Color.BLACK;
    public static final Color scrollBarColor = Color.LIGHTGRAY;
    public static final Color hoveredScrollBarColor = Color.GRAY;

    private static double fontSize = 12.0;
    private static int maxWidth = 500;
    private static int maxHeight = 500;

    public static final int SPACING_X = 0;
    public static final int SPACING_Y = 2;

    private static int yOffset = 0;
    private static int numLines = 0;

    private static boolean fontChanged = false;
    private static boolean renderScrollBar = true;

    private static void fullRender(GraphicsContext gc, Canvas canvas, ConcreteNode rootNode){

        rootNode.setY(yOffset);
        rootNode.preRender();

        gc.setFill(backgroundColor);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Verdana", fontSize));

        numLines = 0;
        rootNode.render(gc);

        if(fontChanged)
            fontChanged = false;
    }

    public static void updateOffset(){
        Renderer.setyOffset(Math.min(0, Math.max(Renderer.getyOffset(),
                +Renderer.getMaxHeight()
                        - (Renderer.getNumLines()+2) * Renderer.getLineLength())));
    }

    public static void snapToCursor(Cursor cursor){
        // TODO: implement
        if((cursor.getOnLine()+3) * Renderer.getLineLength() > maxHeight - yOffset){
            yOffset = maxHeight - (cursor.getOnLine()+3) * Renderer.getLineLength();
        }
        if((cursor.getOnLine()) * Renderer.getLineLength() < 0 - yOffset ||
                (cursor.getOnLine()-1) * Renderer.getLineLength() < 0 - yOffset){
            yOffset = 0 - (cursor.getOnLine()) * Renderer.getLineLength();
        }
    }

    private static void renderScrollBar(GraphicsContext gc, ResizeableCanvas canvas, ConcreteNode rootNode, Cursor cursor){
        if(numLines * getLineLength() <= maxHeight){
            renderScrollBar = false;
            return;
        }
        double scrollPercentage = (double) maxHeight / ((double) numLines * (double)getLineLength());
        double scrollBarHeight = scrollPercentage * maxHeight;
        double percentThrough = (yOffset) / (((double) (numLines+2) * (double)getLineLength()));

        renderScrollBar = true;

        ScrollBar.setX(maxWidth - ScrollBar.WIDTH);
        ScrollBar.setY(-percentThrough * maxHeight);
        ScrollBar.setHeight(scrollBarHeight);

        boolean mouseOver = ScrollBar.isMouseOver(Interactions.getMouseX(), Interactions.getMouseY());
        gc.setFill(mouseOver ? hoveredScrollBarColor : scrollBarColor);
        gc.fillRoundRect(maxWidth - ScrollBar.WIDTH, -percentThrough * maxHeight, ScrollBar.WIDTH, scrollBarHeight, 10, 10);
    }

    public static void render(GraphicsContext gc, ResizeableCanvas canvas, ConcreteNode rootNode, Cursor cursor){

        if(Interactions.getInput().size() > 0){
            cursor.updateOnDeltaCursor();
            fullRender(gc, canvas, rootNode);
            Interactions.getInput().clear();
        }
        else {
            renderCursor(gc, cursor);
        }

        renderScrollBar(gc, canvas, rootNode, cursor);
    }

    private static void renderCursor(GraphicsContext gc, Cursor cursor){
        if(!cursor.isBlack())
            cursor.render(gc, false);
        if(cursor.next instanceof CS61BCharacter){
            CS61BCharacter nextChar = (CS61BCharacter)cursor.next;
            nextChar.renderThisOnly(gc, true);
        }
        if(cursor.isBlack())
            cursor.render(gc, false);
    }

    public static double getMaxWidthMinusScrollBar(){
        return renderScrollBar ? maxWidth - ScrollBar.WIDTH : maxWidth;
    }

    public static boolean isFontChanged() {return fontChanged;}

    public static void setFontChanged(boolean fontChanged) {Renderer.fontChanged = fontChanged;}

    public static int getLineLength(){
        return SPACING_Y + (int)fontSize;
    }

    public static double getFontSize() {
        return fontSize;
    }

    public static void setFontSize(double fontSize) {
        Renderer.fontSize = fontSize;
    }

    public static int getMaxWidth() {
        return maxWidth;
    }

    public static void setMaxWidth(int maxWidth) {
        Renderer.maxWidth = maxWidth;
    }

    public static int getyOffset() {
        return yOffset;
    }

    public static void setyOffset(int yOffset) {
        Renderer.yOffset = yOffset;
    }

    public static int getNumLines() {
        return numLines;
    }

    public static void setNumLines(int numLines) {
        Renderer.numLines = numLines;
    }

    public static int getMaxHeight() {
        return maxHeight;
    }

    public static void setMaxHeight(int maxHeight) {
        Renderer.maxHeight = maxHeight;
    }
}
