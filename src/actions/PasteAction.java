package actions;

import javafx.scene.input.KeyEvent;
import rewrite.CharacterNode;
import rewrite.ProgramEntry;

public class PasteAction extends Action {

	CharacterNode start = null, end = null;
	
	public PasteAction(CharacterNode previous, KeyEvent event, CharacterNode start, CharacterNode end) {
		super(previous, event);
		this.start = start;
		this.end = end;
	}

	public void doAction() {
		setCursor();
		ProgramEntry.cursor.insertChainBeforeThis(start, end);
	}

	public void undoAction() {
		setCursor();
		ProgramEntry.cursor.removeChain(start, end);
	}

}
