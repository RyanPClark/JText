package core;

import render.CS61BCharacter;
import render.NewLine;

/**
 * Created by rycla on 12/20/2016.
 */
public class Selection {
    private Node first;
    private Node second;
    private boolean reverse;

    public static Selection generateSelectionFromString(String input){

        char[] cArray = input.toCharArray();

        Node currentNode;

        if(cArray[0] == '\n'){
            currentNode = new NewLine();
        }
        else {
            currentNode = new CS61BCharacter(Character.toString(cArray[0]));
        }

        Node initial = currentNode;

        for(int i = 1; i < cArray.length; i++){
            Node newNode = null;
            if(cArray[i] == '\n'){
                NewLine newLine = new NewLine();
                newNode = newLine;
            }
            else {
                CS61BCharacter character = new CS61BCharacter(Character.toString(cArray[i]));
                newNode = character;
            }
            currentNode.addNodeAfterThis(newNode);
            currentNode = newNode;
        }

        if(cArray.length == 1)
            return new Selection(initial, initial, false);

        return new Selection(initial, currentNode.previous, false);
    }

    public Selection(){}

    public Selection(Node first, Node second, boolean reverse) {
        this.first = first;
        this.second = second;
        this.reverse = reverse;
    }

    public Node getFirst() {
        return first;
    }

    public void setFirst(Node first) {
        this.first = first;
    }

    public Node getSecond() {
        return second;
    }

    public void setSecond(Node second) {
        this.second = second;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
}
