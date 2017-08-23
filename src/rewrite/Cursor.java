package rewrite;

import javafx.scene.canvas.GraphicsContext;

public class Cursor extends CharacterNode {

    public double render_width = 2; 
    public double height = 16;
    public double render_length = 60;
    private int tick_counter = 0;

    
	public void render (GraphicsContext gc) {

        if(isBlack()) {
            gc.setFill(ProgramEntry.TEXT_COLOR);
            gc.fillRect(x,y,render_width,4+ProgramEntry.fontSize);
        }
	}
    
    public void resetTick() {
    	tick_counter = 0;
    }
    
	public Cursor(String character) {
		super(character);
	}
	
	public void deletePrevious() {
		if(this.previous != ProgramEntry.rootNode)
			this.previous.removeThisNodeFromChain();
		resetTick();
		ProgramEntry.snapToPosition(y);
	}
	
	public void deleteNext() {
		if(this.next != ProgramEntry.terminalNode)
			this.next.removeThisNodeFromChain();
		resetTick();
		ProgramEntry.snapToPosition(y);
	}
	
	public void moveBackward() {
		if(this.previous != ProgramEntry.rootNode) {
			this.shiftLeft();
		}
		resetTick();
		ProgramEntry.snapToPosition(y);
		ProgramEntry.selection.deactivate();
	}
	
	public void moveToPoint(double coordX, double coordY) {
		resetTick();
		CharacterNode closest = ProgramEntry.rootNode.closestCharToPoint(coordX, coordY);
		this.removeThisNodeFromChain();
		if(coordX < closest.x + closest.calculateWidth() / 2)
			closest.addNodeBeforeThis(this);
		else
			closest.addNodeAfterThis(this);
		ProgramEntry.selection.deactivate();
	}
	
	public void moveUp() {
		if(y == ProgramEntry.TOP_MARGIN)
			return;
		moveToPoint(x, y-ProgramEntry.lineHeight);
		resetTick();
		ProgramEntry.snapToPosition(y);
		ProgramEntry.selection.deactivate();
	}
	
	public void moveDown() {
		if(next == ProgramEntry.terminalNode)
			return;
		moveToPoint(x, y+ProgramEntry.lineHeight);
		resetTick();
		ProgramEntry.snapToPosition(y);
		ProgramEntry.selection.deactivate();
	}
	
	public void moveForward() {
		if(this.next != ProgramEntry.terminalNode) {
			this.shiftRight();
		}
		resetTick();
		ProgramEntry.snapToPosition(y);
		ProgramEntry.selection.deactivate();
	}
	
	public void insertCharacterBehind(String ch) {
		addNodeBeforeThis(new CharacterNode(ch));
		resetTick();
		ProgramEntry.snapToPosition(y);
	}
	
    public void perFrameUpdate(){
        tick_counter++;
        if(tick_counter == render_length)
            resetTick();
    }
    
    public boolean isBlack(){
        return (tick_counter < render_length / 2);
    }
}
