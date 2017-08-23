package actions;

import javafx.scene.input.KeyEvent;
import rewrite.CharacterNode;
import rewrite.ProgramEntry;

public class BackspaceAction extends Action {

	public CharacterNode data = null;
	public CharacterNode next = null;
	
	public BackspaceAction(CharacterNode previous, CharacterNode next, KeyEvent event) {
		super(previous, event);
		this.data = previous;
		this.next = next;
	}

	public void doAction() {
		setCursor();
		ProgramEntry.cursor.deletePrevious();
	}

	protected void setCursor() {
		ProgramEntry.cursor.removeThisNodeFromChain();
		next.addNodeBeforeThis(ProgramEntry.cursor);
	}
	
	public void undoAction() {
		setCursor();
		ProgramEntry.cursor.addNodeBeforeThis(data);
	}
}
