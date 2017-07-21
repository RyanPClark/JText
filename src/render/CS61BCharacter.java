package render;

import core.Interactions;
import core.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Created by rycla on 12/17/2016.
 */
public class CS61BCharacter extends Node {

    private static final int expandedBoxSize = 2;

    private String character;
    private boolean isWhitespace;
    private boolean needsToBeRendered = true;

    public boolean wordwrap() {

        if(isWhitespace){
            NewLine newLine = new NewLine(true);
            this.addNodeAfterThis(newLine);
            newLine.preRender();
            return true;
        }

        if(previous != null)
            return previous.wordwrap();
        return false;
    }

    public void preRender(){

        if(Renderer.isFontChanged())
            calculate_dimensions();

        x = previous.getX() + (previous.getWidth() + getWidth()) / 2 + Renderer.SPACING_X;
        y = previous.getY();

        // needsToBeRendered = (Renderer.fontChanged || lastX != x || lastY != y);

        if(x + width > Renderer.getMaxWidthMinusScrollBar() && !isWhitespace){

            boolean canWordwrap = wordwrap();
            if(!canWordwrap){
                NewLine newLine = new NewLine(true);
                this.addNodeAfterThis(newLine);
            }
            return;
        }

        if(next != null)
            next.preRender();
    }

    public boolean isMouseOver(){
        if(!isWhitespace){
            if(Interactions.getMouseX() > x - expandedBoxSize && Interactions.getMouseX() < x + expandedBoxSize + width &&
                    Interactions.getAdjustedMouseY() > y - expandedBoxSize
                    && Interactions.getAdjustedMouseY() < y + height + expandedBoxSize ){
                return true;
            }
        }

        if(next != null)
            return next.isMouseOver();
        return false;
    }

    private void draw(GraphicsContext gc){
        if (selected){
            gc.setFill(Renderer.selectionColor);
            gc.fillRect(x - width / 2, y + height / 2, width, height);

            if(next instanceof NewLine){
                gc.setFill(Renderer.selectionColor);
                gc.fillRect(x - width / 2, y + Renderer.getLineLength() / 2, Renderer.getMaxWidth(), Renderer.getLineLength());
            }
            gc.setFill(Renderer.selectedTextColor);
        }
        else {
            gc.setFill(Renderer.textColor);
        }
        gc.setFont(new Font("Verdana", Renderer.getFontSize()));
        gc.fillText(
                character,
                x,
                y + getHeight()
        );
    }

    public void renderThisOnly(GraphicsContext gc, boolean clear){

        if(clear){
            gc.setFill(Renderer.backgroundColor);
            gc.fillRect(x - width / 2, y + height / 2, width, height);
        }
        draw(gc);
    }

    public void render(GraphicsContext gc){

        if(needsToBeRendered)
            draw(gc);

        if(next != null)
            next.render(gc);
    }

    public CS61BCharacter(){}

    public CS61BCharacter(String character){
        this.character = character;
        if (character.equals(" ") || character.equals("\t"))
            isWhitespace = true;
        calculate_dimensions();
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
        if (character.equals(" ") || character.equals("\t"))
            isWhitespace = true;
        calculate_dimensions();
    }

    private void calculate_dimensions(){

        Text text = new Text("" + character);
        text.setFont(new Font("Verdana", Renderer.getFontSize()));
        setWidth((int)text.getLayoutBounds().getWidth());
        setHeight((int)text.getLayoutBounds().getHeight());
    }
}
