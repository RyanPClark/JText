package core;

import render.ConcreteNode;

/**
 * Created by rycla on 12/19/2016.
 */
public class Action {

    private Node nodeBeforeCursor;
    private ActionType type;
    private Node data;
    private Node data2;
    private Selection selectionData;
    private Selection selectionData2;

    public void doAction(ConcreteNode rootNode, Cursor cursor){

        if(nodeBeforeCursor != cursor.previous){
            cursor.removeThisNodeFromChain();
            nodeBeforeCursor.addNodeAfterThis(cursor);
        }

        switch (type){
            case BACKSPACE:
                cursor.next.removeThisNodeFromChain(); // cursor is set to be behind character it will delete in above code
                break;
            case DELETE:
                cursor.next.removeThisNodeFromChain();
                break;
            case NEW_CHAR:
                cursor.addNodeBeforeThis(data);
                break;
            case NEW_LINE:
                cursor.addNodeBeforeThis(data);
                break;
            case GROUP_DELETE:
                deleteText(rootNode, cursor, selectionData);
                break;
            case GROUP_REPLACE_WITH_LINE:
                deleteText(rootNode, cursor, selectionData);
                cursor.addNodeBeforeThis(data2);
                break;
            case GROUP_REPLACE_WITH_CHAR:
                deleteText(rootNode, cursor, selectionData);
                cursor.addNodeBeforeThis(data2);
                break;
            case PASTE_NO_REPLACE:
                cursor.insertChainBeforeThis(selectionData2.getFirst(), selectionData2.getSecond());
                break;
            case PASTE_AND_REPLACE:
                deleteText(rootNode, cursor, selectionData);
                cursor.insertChainBeforeThis(selectionData2.getFirst(), selectionData2.getSecond());
                break;
        }
    }

    private void deleteText(ConcreteNode rootNode, Cursor cursor, Selection selectionData){

        if(selectionData.getFirst() == selectionData.getSecond()){
            cursor.removeThisNodeFromChain();
            selectionData.getFirst().selected = false;
            selectionData.getFirst().addNodeBeforeThis(cursor);
            selectionData.getFirst().removeThisNodeFromChain();
            cursor.shiftLeft();
            return;
        }
        if(selectionData.isReverse()){
            cursor.removeThisNodeFromChain();
            Node.removeChain(selectionData.getSecond(), selectionData.getFirst());
            selectionData.getFirst().next.addNodeAfterThis(cursor);
            cursor.removeSelection();
            cursor.shiftLeft();

            Node temp = selectionData.getSecond();
            while(temp != selectionData.getFirst()){
                temp.selected = false;
                temp = temp.next;
            }
            selectionData.getFirst().selected = false;
        }
        else {
            cursor.removeThisNodeFromChain();
            Node.removeChain(selectionData.getFirst(), selectionData.getSecond());
            selectionData.getSecond().next.addNodeAfterThis(cursor);
            cursor.removeSelection();
            cursor.shiftLeft();

            Node temp = selectionData.getFirst();
            while(temp != selectionData.getSecond()){
                temp.selected = false;
                temp = temp.next;
            }
            selectionData.getSecond().selected = false;
        }
    }

    public void undoAction(ConcreteNode rootNode, Cursor cursor){

        if(nodeBeforeCursor != cursor.previous){
            cursor.removeThisNodeFromChain();
            nodeBeforeCursor.addNodeAfterThis(cursor);
        }

        switch (type){
            case BACKSPACE:
                cursor.previous.addNodeAfterThis(data);
                break;
            case DELETE:
                cursor.next.addNodeBeforeThis(data);
                break;
            case NEW_CHAR:
                cursor.next.removeThisNodeFromChain();
                break;
            case NEW_LINE:
                cursor.next.removeThisNodeFromChain();
                break;
            case GROUP_DELETE:
                undoGroupDelete(rootNode, cursor);
                break;
            case GROUP_REPLACE_WITH_LINE:
                cursor.next.removeThisNodeFromChain();
                undoGroupDelete(rootNode, cursor);
                break;
            case GROUP_REPLACE_WITH_CHAR:
                cursor.next.removeThisNodeFromChain();
                undoGroupDelete(rootNode, cursor);
                break;
            case PASTE_NO_REPLACE:
                deleteText(rootNode, cursor, selectionData2);
                break;
            case PASTE_AND_REPLACE:
                deleteText(rootNode, cursor, selectionData2);
                if(!selectionData.isReverse())
                    cursor.insertChainBeforeThis(selectionData.getFirst(), selectionData.getSecond());
                else
                    cursor.insertChainBeforeThis(selectionData.getSecond(), selectionData.getFirst());
        }
    }

    private void undoGroupDelete(ConcreteNode rootNode, Cursor cursor){

        if(!selectionData.isReverse()){
            cursor.insertChainBeforeThis(selectionData.getFirst(), selectionData.getSecond());
        }

        else {
            cursor.insertChainBeforeThis(selectionData.getSecond(), selectionData.getFirst());
        }
    }

    public Action(Node nodeBeforeCursor, ActionType type){
        this.nodeBeforeCursor = nodeBeforeCursor;
        this.type = type;
    }

    public Action(Node nodeBeforeCursor, ActionType type, Selection selectionData, Node replace, boolean modifySelection){
        this.nodeBeforeCursor = nodeBeforeCursor;
        this.type = type;
        this.selectionData = selectionData;
        if(modifySelection){
            if(!this.selectionData.isReverse())
                this.selectionData.setSecond(this.selectionData.getSecond().previous);
            else{
                this.selectionData.setFirst(this.selectionData.getFirst().previous.previous);
                this.nodeBeforeCursor = this.selectionData.getSecond().previous;
            }
        }
        this.data2 = replace;
    }

    public Action(Node nodeBeforeCursor, ActionType type, Selection selectionData, boolean modifySelection){
        this.nodeBeforeCursor = nodeBeforeCursor;
        this.type = type;
        this.selectionData = selectionData;
        if(modifySelection){
            if(!this.selectionData.isReverse())
                this.selectionData.setSecond(this.selectionData.getSecond().previous);
            else{
                this.selectionData.setFirst(this.selectionData.getFirst().previous.previous);
                this.nodeBeforeCursor = this.selectionData.getSecond().previous;
            }
        }
    }

    public Action(Node nodeBeforeCursor, ActionType type, Node data) {
        this.nodeBeforeCursor = nodeBeforeCursor;
        this.type = type;
        this.data = data;
    }

    public Selection getSelectionData2() {return selectionData2;}

    public void setSelectionData2(Selection selectionData2) {this.selectionData2 = selectionData2;}

    public Node getNodeBeforeCursor() {
        return nodeBeforeCursor;
    }

    public void setNodeBeforeCursor(Node nodeBeforeCursor) {
        this.nodeBeforeCursor = nodeBeforeCursor;
    }

    public Node getData() {
        return data;
    }

    public void setData(Node data) {
        this.data = data;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }
}
