package core;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import render.*;

import java.io.*;

/**
 * Created by rycla on 12/17/2016.
 */

/**
 * Project Requirements:
 *
 Cursor The current position of the cursor should be marked with a flashing vertical line.
 Text input Each time the user types a letter on the keyboard, that letter should appear on the screen after the current cursor position, and the cursor should advance to be after the last letter that was typed.
 Word wrapping Your text editor should break text into lines such that it fits the width of the text editor window without requiring the user to scroll horizontally. When possible, your editor should break lines between words rather than within words. Lines should only be broken in the middle of a word when the word does not fit on its own line.
 Newlines When the user presses the Enter or Return key, your text editor should advance the cursor to the beginning of the next line.
 Backspace Pressing the backspace key should cause the character before the current cursor position to be deleted.
 Open and save Your editor should accept a single command line argument describing the location of the file to edit. If that file exists, your editor should display the contents of that file. Pressing shortcut+s should save the current contents of the editor to that file.
 Arrow keys Pressing any of the four arrow keys (up, down, left, right) should cause the cursor to move accordingly (e.g., the up key should move the cursor to be on the previous line at the horizontal position closest to the horizontal position of the cursor before the arrow was pressed).
 Mouse input When the user clicks somewhere on the screen, the cursor should move to the place in the text closest to that location.
 Window re-sizing When the user re-sizes the window, the text should be re-displayed so that it fits in the new window (e.g., if the new window is narrower or wider, the line breaks should be adjusted accordingly).
 Vertical scrolling Your text editor should have a vertical scroll bar on the right side of the editor that allows the user to vertically navigate through the file. Moving the scroll bar should change the positioning of the file (but not the cursor position), and if the cursor is moved (e.g., using the arrow keys) so that it's not visible, the scroll bar and window position should be updated so that the cursor is visible.
 Undo and redo Pressing shortcut+z should undo the most recent action (either inserting a character or removing a character), and pressing shortcut+y should redo. Your editor should be able to undo up to 100 actions, but no more.
 Changing font size Pressing shortcut+"+" (the shortcut key and the "+" key at the same time) should increase the font size by 4 points and pressing shortcut+"-" should decrease the font size by 4 points.
 Printing the current position To facilitate grading, pressing shortcut+p should print the top left coordinate of the current cursor position.

 */

/**
 * BUGS:
 * Extra line added when word wrapping long word
 * Delete newLine read from file twice - related to character 13
 * Line selection sometimes off when yOffset != 0
 * Line moves to beginning on first click
 * Backwards selection fails after a few lines
 */

public class Main extends Application {

    private static String[] args;
    private static Scene theScene;

    public void start(Stage theStage)
    {
        theStage.setTitle( "JText" );
        theStage.getIcons().add(new Image(this.getClass().getResourceAsStream("jlogo.jpeg")));
        
        Group root = new Group();
        theScene = new Scene( root );

        ResizeableCanvas canvas = new ResizeableCanvas( 500, 500 );
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add( canvas );

        ConcreteNode rootNode = new ConcreteNode(Node.MIN_X, 0);
        Cursor cursor = new Cursor();
        theStage.setScene( theScene );
        Interactions.init(theScene, canvas, cursor, rootNode);

        if(args.length > 0){
            try {
                IO.loadFile(args[0], rootNode, cursor);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        else {
            rootNode.addNodeAfterThis(cursor);
        }

        Interactions.getInput().add("Initial load");

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                cursor.perFrameUpdate();
                UndoManager.update();
                Renderer.render(gc, canvas, rootNode, cursor);
            }
        }.start();

        theStage.show();
    }

    public static Scene getTheScene() {
        return theScene;
    }

    public static String[] getArgs() {
        return args;
    }

    public static void main(String[] args) { Main.args = args; launch(args); }
}
