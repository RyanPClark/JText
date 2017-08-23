package actions;

import javafx.scene.input.KeyEvent;
import rewrite.CharacterNode;
import rewrite.ProgramEntry;

public class AddCharAction extends Action {

	public CharacterNode data = null;
	
	public AddCharAction(CharacterNode previous, KeyEvent event) {
		super(previous, event);
		data = new CharacterNode(event.getCharacter());
	}

	public void doAction() {
		setCursor();
		ProgramEntry.cursor.addNodeBeforeThis(data);
	}

	public void undoAction() {
		setCursor();
		ProgramEntry.cursor.deleteNext();
	}
	

	
}
