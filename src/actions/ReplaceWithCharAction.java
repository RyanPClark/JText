package actions;

import javafx.scene.input.KeyEvent;
import rewrite.CharacterNode;
import rewrite.ProgramEntry;
import rewrite.Selection;

public class ReplaceWithCharAction extends Action {

	public CharacterNode data = null;
	public CharacterNode start = null, end = null;

	public ReplaceWithCharAction(CharacterNode previous, KeyEvent event, Selection sel) {
		super(previous, event);
		start = sel.first;
		end = sel.second;
		data = new CharacterNode(event.getCharacter());
	}

	public void doAction() {
		setCursor();
		ProgramEntry.cursor.removeChain(start, end);
		ProgramEntry.selection.deactivate();
		ProgramEntry.cursor.addNodeBeforeThis(data);
	}
	
	public void undoAction() {
		setCursor();
		ProgramEntry.cursor.deleteNext();
		ProgramEntry.cursor.insertChainBeforeThis(start, end);
		ProgramEntry.selection.activate();
		ProgramEntry.selection.finalize();
		ProgramEntry.selection.first = start;
		ProgramEntry.selection.second = end;
	}

}
