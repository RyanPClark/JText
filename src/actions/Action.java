package actions;

import java.util.ArrayList;

import javafx.scene.input.KeyEvent;
import rewrite.CharacterNode;
import rewrite.ProgramEntry;

public abstract class Action {

	public CharacterNode previous;
	public KeyEvent event;
	
	public static ArrayList<Action> undoStack = new ArrayList<Action>();
	public static ArrayList<Action> redoStack = new ArrayList<Action>();
	
	public Action(CharacterNode previous, KeyEvent event) {
		this.previous = previous;
		this.event = event;
		undoStack.add(this);
		if(undoStack.size() > 100)
			undoStack.remove(0);
		redoStack.clear();
	}
	
	protected void setCursor() {
		if(previous != ProgramEntry.cursor.previous) {
			ProgramEntry.cursor.removeThisNodeFromChain();
			previous.addNodeAfterThis(ProgramEntry.cursor);
		}
	}
	
	public abstract void doAction();
	
	public abstract void undoAction();
}
