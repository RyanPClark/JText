package core;

import javafx.scene.canvas.GraphicsContext;
import render.CS61BCharacter;
import render.NewLine;
import render.Renderer;

/**
 * Created by rycla on 12/17/2016.
 */
public abstract class Node {

    protected static final int MIN_X = 5;

    public Node previous, next;
    protected double x, y, width, height;

    protected boolean selected = false;

    public abstract void preRender();

    public abstract void render(GraphicsContext gc);

    public abstract boolean wordwrap();

    public abstract boolean isMouseOver();

    public String genString(String previousString, Node end){
        if(this instanceof CS61BCharacter){
            CS61BCharacter character = (CS61BCharacter)this;
            previousString += character.getCharacter();
        }
        else if(this instanceof NewLine){
            previousString += "\n";
        }
        if(next != null && this != end)
            return next.genString(previousString, end);
        return previousString;
    }

    public void insertChainBeforeThis(Node start, Node end){
        start.previous = this.previous;
        end.next = this;
        if(previous != null)
            previous.next = start;
        this.previous = end;
    }

    public static void removeChain(Node startNode, Node endNode){
        startNode.previous.next = endNode.next;
        endNode.next.previous = startNode.previous;
    }

    public int positionInChain(){
        if(previous != null)
            return previous.positionInChain() + 1;
        return 0;
    }

    public Node getEndOfChain(){
        if(next != null)
            return next.getEndOfChain();
        else
            return this;
    }

    public static int onLine(int y){
        int lineLength = (int)Renderer.getFontSize() + Renderer.SPACING_Y;
        return y / lineLength;
    }

    public Node closestOnLine_back(int targetX, int yOfLine, Node currentBest){

        int lineOfTarget = Node.onLine(yOfLine);
        int lineOfThis = Node.onLine((int)y);
        int lineOfCurrentBest = Node.onLine((int)currentBest.y);

        int distance = (int) Math.abs(targetX - x) + 1000000 * Math.abs(lineOfThis - lineOfTarget);
        int distanceOfCurrentBest = (int) Math.abs(targetX - currentBest.x)
                + 1000000 * Math.abs(lineOfCurrentBest - lineOfTarget);

        Node best;

        if(distance < distanceOfCurrentBest)
            best = closestOnLine_back(targetX, yOfLine, this);
        else
            best = currentBest;

        if(previous != null)
            best = previous.closestOnLine_back(targetX, yOfLine, best);
        return best;
    }

    public Node closestOnLine_front(int targetX, int yOfLine, Node currentBest){

        int lineOfTarget = Node.onLine(yOfLine);
        int lineOfThis = Node.onLine((int)y);
        int lineOfCurrentBest = Node.onLine((int)currentBest.y);

        int distance = (int) Math.abs(targetX - x) + 1000000 * Math.abs(lineOfThis - lineOfTarget);
        int distanceOfCurrentBest = (int) Math.abs(targetX - currentBest.x)
                + 1000000 * Math.abs(lineOfCurrentBest - lineOfTarget);

        Node best;

        if(distance < distanceOfCurrentBest)
            best = closestOnLine_back(targetX, yOfLine, this);
        else
            best = currentBest;

        if(next != null)
            best = next.closestOnLine_front(targetX, yOfLine, best);
        return best;
    }

    public void addNodeAfterThis(Node newNode){

        newNode.next = next;
        newNode.previous = this;
        if(next != null)
            next.previous = newNode;
        this.next = newNode;
    }

    public void shiftRight(){
        Node after = this.next;
        after.removeThisNodeFromChain();
        this.addNodeBeforeThis(after);
    }

    public void shiftLeft(){
        Node before = this.previous;
        before.removeThisNodeFromChain();
        this.addNodeAfterThis(before);
    }

    public void addNodeBeforeThis(Node newNode){

        if(previous != null)
            previous.next = newNode;
        newNode.previous = previous;
        newNode.next = this;
        this.previous = newNode;
    }

    public void removeThisNodeFromChain(){
        if(previous != null)
            previous.next = next;
        if(next != null)
            next.previous = previous;
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
