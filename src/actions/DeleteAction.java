package actions;

import javafx.scene.input.KeyEvent;
import rewrite.CharacterNode;
import rewrite.ProgramEntry;

public class DeleteAction extends Action {

	
	public CharacterNode data = null;
	
	public DeleteAction(CharacterNode previous, CharacterNode next, KeyEvent event) {
		super(previous, event);
		data = next;
	}

	public void doAction() {
		setCursor();
		ProgramEntry.cursor.deleteNext();
	}

	public void undoAction() {
		setCursor();
		ProgramEntry.cursor.addNodeAfterThis(data);
	}
}
