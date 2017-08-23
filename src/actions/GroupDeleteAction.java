package actions;

import javafx.scene.input.KeyEvent;
import rewrite.CharacterNode;
import rewrite.ProgramEntry;
import rewrite.Selection;

public class GroupDeleteAction extends Action {

	public CharacterNode start = null, end = null;
	
	public GroupDeleteAction(CharacterNode previous, KeyEvent event, Selection sel) {
		super(previous, event);
		start = sel.first;
		end = sel.second;
	}

	public void doAction() {
		setCursor();
		ProgramEntry.cursor.removeChain(start, end);
		ProgramEntry.selection.deactivate();
	}

	public void undoAction() {
		setCursor();
		ProgramEntry.cursor.insertChainBeforeThis(start, end);
		ProgramEntry.selection.activate();
		ProgramEntry.selection.finalize();
		ProgramEntry.selection.first = start;
		ProgramEntry.selection.second = end;
	}

}
