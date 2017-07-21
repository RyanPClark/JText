package render;

import core.Node;
import javafx.scene.canvas.GraphicsContext;

/**
 * Created by rycla on 12/18/2016.
 */
public class NewLine extends Node {

    private boolean isArtificial;

    public boolean wordwrap(){
        return false;
    }

    public NewLine(){
        isArtificial = false;
    }

    public NewLine(boolean isArtificial){
        this.isArtificial = isArtificial;
    }

    public void preRender(){

        x = 5;
        y = previous.getY() + Renderer.SPACING_Y + Renderer.getFontSize();

        if(next != null)
            next.preRender();

    }

    public void render(GraphicsContext gc){

        Renderer.setNumLines(Renderer.getNumLines() + 1);

        if(next != null)
            next.render(gc);
        if(isArtificial)
            this.removeThisNodeFromChain();
    }

    public boolean isMouseOver(){if(next != null)return next.isMouseOver(); return false;}

    public boolean isArtificial() {
        return isArtificial;
    }

    public void setArtificial(boolean artificial) {
        isArtificial = artificial;
    }
}
