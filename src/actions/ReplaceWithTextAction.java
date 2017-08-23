package actions;

import javafx.scene.input.KeyEvent;
import rewrite.CharacterNode;
import rewrite.ProgramEntry;
import rewrite.Selection;

public class ReplaceWithTextAction extends Action {

	public CharacterNode replaceStart = null, replaceEnd = null;
	public CharacterNode start = null, end = null;

	public ReplaceWithTextAction(CharacterNode previous, KeyEvent event, CharacterNode replaceStart, CharacterNode replaceEnd, Selection sel) {
		super(previous, event);
		start = sel.first;
		end = sel.second;
		this.replaceStart = replaceStart;
		this.replaceEnd = replaceEnd;
	}

	public void doAction() {
		setCursor();
		ProgramEntry.cursor.removeChain(start, end);
		ProgramEntry.selection.deactivate();
		ProgramEntry.cursor.insertChainBeforeThis(replaceStart, replaceEnd);
	}
	
	public void undoAction() {
		setCursor();
		ProgramEntry.cursor.removeChain(replaceStart, replaceEnd);
		ProgramEntry.cursor.insertChainBeforeThis(start, end);
		ProgramEntry.selection.activate();
		ProgramEntry.selection.finalize();
		ProgramEntry.selection.first = start;
		ProgramEntry.selection.second = end;
	}
}
