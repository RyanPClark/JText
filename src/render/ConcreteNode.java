package render;

import core.Node;
import javafx.scene.canvas.GraphicsContext;

/**
 * Created by rycla on 12/18/2016.
 */
public class ConcreteNode extends Node {

    public ConcreteNode(int x, int y){
        this.x = x;
        this.y = y;
        this.width = 0;
        this.height = 0;
    }

    public boolean isMouseOver(){if(next != null)return next.isMouseOver(); return false;}

    public boolean wordwrap() {
        if(previous != null)
            return previous.wordwrap();

        NewLine newLine = new NewLine(true);
        this.addNodeAfterThis(newLine);
        newLine.preRender();

        return true;
    }

    public void preRender(){

        if(next != null)
            next.preRender();
    }

    public void render(GraphicsContext gc){

        // render

        if(next != null)
            next.render(gc);
    }
}
