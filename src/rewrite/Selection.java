package rewrite;

import javafx.scene.canvas.GraphicsContext;

public class Selection {

	public boolean activated = false;
	public boolean finalized = false;
	public CharacterNode first, second, initial;
	
	public void deactivate() {
		activated = finalized = false;
		first = second = initial = null;
	}
	
	public void activate() {
		activated = true;
		finalized = false;
	}
	
	public void finalize() {
		finalized = true;
	}
	
	public void respondToMouseDrag(double x, double y) {
		
		CharacterNode nearestNode = ProgramEntry.rootNode.closestCharToPoint(x, y);

		if(nearestNode == ProgramEntry.cursor || nearestNode.isNewLine())
			nearestNode = nearestNode.next;
		
		if(!activated) {
			activate();
			initial = nearestNode;
		}
		else {
			if (initial.isThisAfter(nearestNode)) {
				first = nearestNode;
				second = initial;
				ProgramEntry.cursor.removeThisNodeFromChain();
				first.addNodeBeforeThis(ProgramEntry.cursor);
			}
			else {
				first = initial;
				second = nearestNode;
				ProgramEntry.cursor.removeThisNodeFromChain();
				if(nearestNode.next != null)
					if(!nearestNode.next.isNewLine())
						second.addNodeAfterThis(ProgramEntry.cursor);
					else
						second.addNodeBeforeThis(ProgramEntry.cursor);
			}
			ProgramEntry.snapToPosition(nearestNode.y);
		}
	}
	
	public void render(GraphicsContext gc) {
		if(!activated)
			return;
		CharacterNode currentRenderNode = first;
		gc.setFill(ProgramEntry.HIGHLIGHT_COLOR);
		if(second == null)
			return;
		while(currentRenderNode != second.next && currentRenderNode != null) {
			gc.fillRect(currentRenderNode.x, currentRenderNode.y, currentRenderNode.calculateWidth() * 1.1, ProgramEntry.lineHeight);
			currentRenderNode = currentRenderNode.next;
		}
	}
	
	public void respondToMouseRelease() {
		if(activated && finalized)
			deactivate();
		else if (activated && !finalized)
			finalize();
	}
}
