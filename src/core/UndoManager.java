package core;

import render.ConcreteNode;

import java.util.ArrayList;

/**
 * Created by rycla on 12/19/2016.
 */
public class UndoManager {

    private static ArrayList<Action> allActions = new ArrayList<Action>();

    private static int actionsBack = 0;

    private static boolean justUndone = false;

    public static void update(){
        while(allActions.size() > 100){
            allActions.remove(0);
        }
    }

    public static void undo(Cursor cursor, ConcreteNode rootNode){
        if(allActions.size() - actionsBack > 0){
            allActions.get(allActions.size()-actionsBack-1).undoAction(rootNode, cursor);
            actionsBack++;
            justUndone = true;
        }
    }

    public static void redo(Cursor cursor, ConcreteNode rootNode){
        if(actionsBack > 0){
            allActions.get(allActions.size()-actionsBack).doAction(rootNode, cursor);
            actionsBack--;
        }
    }

    public static void resetActionsBack(){
        if(justUndone){
            allActions.clear();
            actionsBack = 0;
            justUndone = false;
        }
    }

    public static ArrayList<Action> getAllActions() {
        return allActions;
    }

    public static void setAllActions(ArrayList<Action> allActions) {
        UndoManager.allActions = allActions;
    }
}
