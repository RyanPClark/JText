package core;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import render.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by rycla on 12/19/2016.
 */
public class Interactions {

    private static int mouseX = 0;
    private static int mouseY = 0;

    private static Scene theScene;
    private static Cursor cursor;

    private static ArrayList<String> input = new ArrayList<String>();

    public static boolean mouseUp = true;

    private static void scroll(double dy){
        int lines = (int)dy / ((int)Renderer.getFontSize() + Renderer.SPACING_Y);
        int offset = Renderer.getyOffset() + lines * (Renderer.SPACING_Y + (int)Renderer.getFontSize());
        Renderer.setyOffset(Math.min(0, Math.max(offset,
                +Renderer.getMaxHeight()
                        - (Renderer.getNumLines()+2) * Renderer.getLineLength())));
    }

    public static void scrollFromPoint(double dy, double startPoint){
        int lines = (int)dy / ((int)Renderer.getFontSize() + Renderer.SPACING_Y);
        int offset = (int)startPoint + lines * (Renderer.SPACING_Y + (int)Renderer.getFontSize());
        Renderer.setyOffset(Math.min(0, Math.max(offset,
                +Renderer.getMaxHeight()
                        - (Renderer.getNumLines()+2) * Renderer.getLineLength())));
    }

    public static void init(Scene theScene, ResizeableCanvas canvas, Cursor cursor, ConcreteNode rootNode){

        Interactions.cursor = cursor;
        Interactions.cursor.setRootNode(rootNode);
        Interactions.theScene = theScene;

        input.add("Load");

        canvas.addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                mouseX = (int)event.getX();
                mouseY = (int)event.getY();

                cursor.updateOnDeltaMouse();
            }
        });


        canvas.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                scroll(event.getDeltaY());
                input.add("Scroll");
            }
        });

        theScene.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                mouseUp = true;
                ScrollBar.setPressedUpon(false, 0, 0, Renderer.getyOffset());
            }
        });

        theScene.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {

                boolean dragging = ScrollBar.isPressedUpon();
                if(dragging){
                    double dy = event.getY() - ScrollBar.getLastY();
                    Interactions.scrollFromPoint(-dy / ScrollBar.getHeight() * Renderer.getMaxHeight(), ScrollBar.getStartOffset());
                    input.add("MOVE SCROLLBAR");
                    return;
                }

                Node best = rootNode.closestOnLine_front((int)event.getX(), adjustY((int)event.getY()),
                        cursor);
                if(cursor.getSelection() == null){
                    cursor.setSelection(new Selection());
                    cursor.getSelection().setFirst(cursor.next);
                }
                cursor.getSelection().setSecond(best);

                boolean coloring = false;
                boolean hasEncounteredFirst = false;
                boolean correct = false;
                Node temp = rootNode;

                while(temp != null){
                    if(temp == cursor.getSelection().getFirst()){
                        coloring = !coloring;
                        if(!hasEncounteredFirst){
                            hasEncounteredFirst = true;
                            correct = true;
                        }
                    }
                    if (temp == cursor.getSelection().getSecond()){
                        coloring = !coloring;
                        if(!hasEncounteredFirst){
                            hasEncounteredFirst = true;
                            correct = false;
                        }
                    }
                    if(!coloring)
                        temp.selected = false;
                    else
                        temp.selected = true;

                    temp = temp.next;
                }

                if(cursor.getSelection().getFirst() == cursor.getSelection().getSecond())
                    cursor.getSelection().getFirst().selected = true;

                cursor.getSelection().setReverse(!correct);

                mouseUp = false;

                input.add("Mouse drag");
                Renderer.snapToCursor(cursor);
            }
        });

        theScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                boolean mouseOverCursor = ScrollBar.isMouseOver(Interactions.getMouseX(), Interactions.getMouseY());
                ScrollBar.setPressedUpon(mouseOverCursor, getMouseX(), getAdjustedMouseY(), Renderer.getyOffset());
                if(mouseOverCursor)
                    return;

                Node best = cursor.closestOnLine_front((int)mouseEvent.getX(), (int)getAdjustedMouseY(),
                        rootNode);

                if(best instanceof NewLine)
                    if(best.previous != null)
                        if(best.previous instanceof CS61BCharacter)
                            best = best.previous;

                if(best != cursor){
                    cursor.removeThisNodeFromChain();
                    if(best.next == null){
                        best.addNodeAfterThis(cursor);
                    }
                    else {
                        if(cursor.getX() < best.getX())
                            best.addNodeBeforeThis(cursor);
                        else
                            best.addNodeAfterThis(cursor);
                    }

                    input.add("Cursor moved");
                }
                cursor.removeSelection();
                mouseUp = false;
            }
        });

        theScene.setOnKeyPressed(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                        String code = e.getCode().toString();

                        // only add once... prevent duplicates
                        if ( !input.contains(code) )
                            input.add( code );

                        if(e.isShortcutDown()) {
                            if (e.getCode() == KeyCode.EQUALS){
                                Renderer.setFontSize(Renderer.getFontSize() + 4);
                                Renderer.setFontChanged(true);
                            }
                            else if (e.getCode() == KeyCode.MINUS){
                                Renderer.setFontSize(Math.max(Renderer.getFontSize() - 4, 4));
                                Renderer.setFontChanged(true);
                            }
                            else if (e.getCode() == KeyCode.S)
                                IO.saveFile(rootNode, Main.getArgs()[0]);
                            else if (e.getCode() == KeyCode.Z)
                                UndoManager.undo(cursor, rootNode);
                            else if (e.getCode() == KeyCode.Y)
                                UndoManager.redo(cursor, rootNode);
                            else if (e.getCode() == KeyCode.P)
                                System.out.println(cursor.getX() + ", " +cursor.getY());
                            else if(e.getCode() == KeyCode.C)
                                copyToClipboard(cursor);
                        }
                        else {
                            cursor.setTick_counter(0);
                            if (e.getCode() == KeyCode.LEFT){
                                if(cursor.previous != rootNode)
                                    cursor.shiftLeft();
                                if(cursor.getSelection() != null)
                                    cursor.removeSelection();
                            }
                            else if (e.getCode() == KeyCode.RIGHT){
                                if(cursor.next != null)
                                    cursor.shiftRight();
                                if(cursor.getSelection() != null)
                                    cursor.removeSelection();
                            }
                            else if (e.getCode() == KeyCode.UP){

                                Node best = cursor.closestOnLine_back((int)cursor.getX(),
                                        (int)(cursor.getY() - Renderer.getFontSize() - Renderer.SPACING_Y), cursor);

                                if(best instanceof NewLine)
                                    if(best.previous != null)
                                        if(best.previous instanceof CS61BCharacter)
                                            best = best.previous;

                                if(best != cursor){
                                    cursor.removeThisNodeFromChain();
                                    if(cursor.getX() < best.getX())
                                        best.addNodeBeforeThis(cursor);
                                    else
                                        best.addNodeAfterThis(cursor);
                                }
                                Renderer.snapToCursor(cursor);
                                if(cursor.getSelection() != null)
                                    cursor.removeSelection();
                            }
                            else if (e.getCode() == KeyCode.DOWN){
                                Node best = cursor.getEndOfChain().closestOnLine_back((int)cursor.getX(),
                                        (int)(cursor.getY() + Renderer.getFontSize() + Renderer.SPACING_Y), cursor);

                                if(best instanceof NewLine)
                                    if(best.previous != null)
                                        if(best.previous instanceof CS61BCharacter)
                                            best = best.previous;

                                if(best != cursor){
                                    cursor.removeThisNodeFromChain();
                                    if(cursor.getX() < best.getX())
                                        best.addNodeBeforeThis(cursor);
                                    else
                                        best.addNodeAfterThis(cursor);
                                }
                                Renderer.snapToCursor(cursor);
                                if(cursor.getSelection() != null)
                                    cursor.removeSelection();
                            }
                        }
                    }
                });

        theScene.setOnKeyTyped(
                new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent event) {
                        if(
                                (((int)event.getCharacter().charAt(0)==22) && event.isShortcutDown())
                                        || !event.isShortcutDown()){
                            Action action = null;
                            if (event.getCharacter().equals("\b")){
                                if(cursor.previous != null){
                                    if(cursor.getSelection() == null){
                                        if(cursor.previous != rootNode)
                                            action = new Action(cursor.previous.previous, ActionType.BACKSPACE, cursor.previous);
                                    }
                                    else
                                        action = new Action(cursor.previous, ActionType.GROUP_DELETE,
                                                cursor.getSelection(), true);
                                }
                            }
                            else if (event.getCharacter().equals("\r")){
                                NewLine newLine = new NewLine();
                                if(cursor.getSelection() == null){
                                    action = new Action(cursor.previous, ActionType.NEW_LINE, newLine);
                                }
                                else {
                                    action = new Action(cursor.previous, ActionType.GROUP_REPLACE_WITH_LINE,
                                            cursor.getSelection(), newLine, true);
                                }
                            }
                            else if (event.getCharacter().charAt(0) == 127){
                                if(cursor.next != null){
                                    if(cursor.getSelection() == null)
                                        action = new Action(cursor.previous, ActionType.DELETE, cursor.next);
                                    else
                                        action = new Action(cursor.previous, ActionType.GROUP_DELETE,
                                                cursor.getSelection(), true);
                                }
                            }
                            else if ((int)event.getCharacter().charAt(0)==22 && event.isShortcutDown()){
                                Selection data = Selection.generateSelectionFromString(getClipboardContents());
                                if(cursor.getSelection() == null){
                                    action = new Action(cursor.previous, ActionType.PASTE_NO_REPLACE);
                                    action.setSelectionData2(data);
                                }
                                else {
                                    action = new Action(cursor.previous, ActionType.PASTE_AND_REPLACE, cursor.getSelection(), true);
                                    action.setSelectionData2(data);
                                }
                            }
                            else {
                                CS61BCharacter character = new CS61BCharacter(event.getCharacter());
                                if(cursor.getSelection() == null){
                                    action = new Action(cursor.previous, ActionType.NEW_CHAR, character);
                                }
                                else {
                                    action = new Action(cursor.previous, ActionType.GROUP_REPLACE_WITH_CHAR,
                                            cursor.getSelection(), character, true);
                                }
                            }
                            Renderer.snapToCursor(cursor);
                            UndoManager.resetActionsBack();
                            UndoManager.getAllActions().add(action);

                            if(action != null)
                                action.doAction(rootNode, cursor);
                        }
                    }
                }
        );

        theScene.widthProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                canvas.resizeWidth(newSceneWidth.doubleValue());
                Renderer.setMaxWidth((int)canvas.getWidth());
                Renderer.updateOffset();
                if(!input.contains("RESIZE X"))
                    input.add("RESIZE X");
            }
        });
        theScene.heightProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                canvas.resizeHeight(newSceneHeight.doubleValue());
                Renderer.setMaxHeight((int)canvas.getHeight());
                Renderer.updateOffset();
                if(!input.contains("RESIZE Y"))
                    input.add("RESIZE Y");
            }
        });
    }

    private static String getClipboardContents(){
        String data = "EXCEPTION";
        try {
            data = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
        }
        catch(IOException | UnsupportedFlavorException e){e.printStackTrace();}
        return data;
    }

    private static void copyToClipboard(Cursor cursor){
        if(cursor.getSelection() == null)
            return;
        String theString = "";
        if(cursor.getSelection().isReverse()){
            theString = cursor.getSelection().getSecond().genString(theString, cursor.getSelection().getFirst());
        }
        else {
            theString = cursor.getSelection().getFirst().genString(theString, cursor.getSelection().getSecond());
        }
        StringSelection selection = new StringSelection(theString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    public static ArrayList<String> getInput() {
        return input;
    }

    public static Scene getTheScene() {
        return theScene;
    }

    public static void setTheScene(Scene theScene) {
        Interactions.theScene = theScene;
    }

    public static int adjustY(int in){
        return in - 6;
    }

    public static int getAdjustedMouseY(){
        return adjustY(mouseY);
    }

    public static int getMouseX() {
        return mouseX;
    }

    public static void setMouseX(int mouseX) {
        Interactions.mouseX = mouseX;
    }

    public static int getMouseY() {
        return mouseY;
    }

    public static void setMouseY(int mouseY) {
        Interactions.mouseY = mouseY;
    }
}
