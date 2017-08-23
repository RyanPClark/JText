package rewrite;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import actions.Action;
import actions.AddCharAction;
import actions.BackspaceAction;
import actions.DeleteAction;
import actions.GroupDeleteAction;
import actions.PasteAction;
import actions.ReplaceWithCharAction;
import actions.ReplaceWithTextAction;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class ProgramEntry extends Application {

	public static final int INITIAL_WIDTH = 500;
	public static final int INITIAL_HEIGHT = 500;
	public static final int LEFT_MARGIN = 20;
	public static final int TOP_MARGIN = 10;
	public static final int BOTTOM_MARGIN = 20;
	public static final int RIGHT_MARGIN = 20;
	public static final Color BACKGROUND_COLOR = Color.WHITE;
	public static final Color HIGHLIGHT_COLOR = Color.LIGHTBLUE;
	public static final Color TEXT_COLOR = Color.BLACK;
	
	public static double fontSize = 12.0;
	public static double lineHeight = 1.0;
	public static double maxWidth = INITIAL_WIDTH - RIGHT_MARGIN;
	public static double mx = 0;
	public static double my = 0;
	public static int numLines = 0;
	public static double yOffset = TOP_MARGIN;
	public static double maxCharacterHeight = 0;
	public static double canvasHeight = INITIAL_HEIGHT;
	public static double documentHeight = 0;
	
	public static ArrayList<Region> regions = new ArrayList<Region>();
	public static TextRegion mainArea = new TextRegion(0, INITIAL_WIDTH, 0, INITIAL_HEIGHT, 0);
	public static ScrollRegion scrollBar = new ScrollRegion(0,0,0,0,1);
	
	public static Font defaultFont = new Font("Verdana", fontSize);
	public static CharacterNode rootNode = new CharacterNode("");
	public static CharacterNode terminalNode = new CharacterNode("");
	public static Cursor cursor = new Cursor("");
	public static Selection selection = new Selection();
	public static String fileLocation;
	public static Scene primaryScene;
	
	public static void main(String[] args) throws IOException {
		
		if(args.length != 1) {
			rootNode.addNodeAfterThis(cursor);
			terminalNode.previous = cursor;
			cursor.next = terminalNode;
		}
		else {
			fileLocation = args[0];
			
			Charset encoding = Charset.defaultCharset();
			File file = new File(fileLocation);
			handleFile(file, encoding);
		}
		
		launch(args);
	}
	
	private static void handleFile(File file, Charset encoding) 
	throws IOException {
		try (
			InputStream in = new FileInputStream(file);
			Reader reader = new InputStreamReader(in, encoding);
			Reader buffer = new BufferedReader(reader)){
			handleCharacters(buffer);
		}
	}
	
	private static void handleCharacters(Reader reader) 
	throws IOException {
		CharacterNode lastNode = rootNode;
		int r;
		while((r = reader.read()) != -1) {
			char ch = (char)r;
			if(ch == '\r')
				continue;
			CharacterNode newNode = new CharacterNode(String.valueOf(ch));
			newNode.previous = lastNode;
			lastNode.next = newNode;
			lastNode = newNode;
		}
		lastNode.next = cursor;
		cursor.previous = lastNode;
		cursor.next = terminalNode;
		terminalNode.previous = cursor;
	}
	
	public static Selection textToSelection(String text) {
		CharacterNode lastNode = null;
		CharacterNode start = null;
		for(int i = 0; i < text.length(); i++) {
			CharacterNode newNode = new CharacterNode(String.valueOf(text.charAt(i)));
			newNode.previous = lastNode;
			if(lastNode != null)
				lastNode.next = newNode;
			lastNode = newNode;
			if(i == 0)
				start = lastNode;
		}
		Selection ret = new Selection();
		ret.first = start;
		ret.second = lastNode;
		return ret;
	}
	
	public static void updateCursorIcon(javafx.scene.Cursor icon) {
        primaryScene.setCursor(icon);
	}
	
	private static void close() {
		System.exit(0);
	}

	private void editorRefreshScreen(GraphicsContext gc, ResizeableCanvas canvas) {
		gc.clearRect(0, 0, 10000, 10000);
		renderText(gc, canvas);
		renderScrollBar(gc);
	}
	
	private void calculateLineHeight() {
		Text text = new Text("H");
		text.setFont(defaultFont);
		lineHeight = text.getLayoutBounds().getHeight();
	}
	
	private static void renderScrollBar(GraphicsContext gc) {
		
		if(documentHeight <= canvasHeight - TOP_MARGIN - BOTTOM_MARGIN) {
			scrollBar.xRight=scrollBar.xLeft;
			return;
		}
		
		double barHeight = (canvasHeight-TOP_MARGIN-BOTTOM_MARGIN) * (canvasHeight-TOP_MARGIN-BOTTOM_MARGIN) / documentHeight;
		double barY = -(yOffset-TOP_MARGIN) * (canvasHeight-TOP_MARGIN-BOTTOM_MARGIN) / documentHeight;
		
		scrollBar.xLeft = maxWidth+RIGHT_MARGIN * 0.25;
		scrollBar.xRight = maxWidth+RIGHT_MARGIN * 0.75;
		scrollBar.yTop = barY;
		scrollBar.yBottom = barY + barHeight;
		
		scrollBar.animate();
		
		gc.setFill(scrollBar.currentColor);
		gc.fillRoundRect(maxWidth+RIGHT_MARGIN * 0.25, barY, RIGHT_MARGIN * 0.5, barHeight, 10, 10);
	}
	
	private void renderText(GraphicsContext gc, ResizeableCanvas canvas) {
		
		calculateLineHeight();
		
		gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

		String renderText = rootNode.calculateRenderPosition(LEFT_MARGIN, yOffset, "", null, "");
		maxCharacterHeight = terminalNode.y;
		documentHeight = maxCharacterHeight - yOffset;
		
		if(selection.activated)
			selection.render(gc);
		cursor.render(gc);
		
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.BLACK);
        gc.setFont(defaultFont);
		gc.fillText(renderText, rootNode.x, rootNode.y);
	}
	
	private static void updateFontSize(double size) {
		fontSize = size;
		defaultFont = new Font("Verdana", fontSize);
	}
	
	public static String nodesToString(CharacterNode start, CharacterNode end) {
		
		String output = "";
		CharacterNode currentNode = start;
		while(currentNode != null && currentNode != end.next) {
			if(currentNode.character.equals("\r"))
				output += "\n";
			output += currentNode.character;
			currentNode = currentNode.next;
		}
		return output;
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
	
	public static void copyToClipboard() {
		if(!selection.activated)
			return;
		StringSelection strSel = new StringSelection(nodesToString(selection.first, selection.second));
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(strSel, strSel);
	}
	
	public static void save(String fileLocation, String outText) {
		File saveFile = new File(fileLocation);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(saveFile));
			writer.write(outText);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void snapToPosition(double y) {
		if(y < TOP_MARGIN)
			yOffset += (TOP_MARGIN - y);
		else if (y > ProgramEntry.canvasHeight - ProgramEntry.BOTTOM_MARGIN)
			yOffset += (canvasHeight - BOTTOM_MARGIN - y);
	}
	
	public void handleMouseEvent(MouseEvent e, String type) {
		mx = e.getSceneX();
		my = e.getSceneY();
		Region interactionRegion = null;
		int zIndex = -1;
		for(Region r : regions) {
			if(r.pointInRegion(mx, my)) {
				if(r.zIndex > zIndex) {
					zIndex = r.zIndex;
					interactionRegion = r;
				}
			}
		}
		
		if(interactionRegion instanceof TextRegion) {
			updateCursorIcon(javafx.scene.Cursor.DEFAULT);
			rootNode.updateCursorIfMouseOver();
		}
		else {
			updateCursorIcon(javafx.scene.Cursor.HAND);
		}
		
		if(type.equals("press")) {
			if(interactionRegion instanceof TextRegion) {
				selection.deactivate();
	    		cursor.moveToPoint(e.getSceneX(), e.getSceneY());
			}
			Region.dragStartRegion = null;
		}
		else if (type.equals("release")) {
			selection.finalize();
		}
		else if (type.equals("drag")) {
			if(Region.dragStartRegion == null) {
				Region.dragStartRegion = interactionRegion;
				Region.lastY = my;
			}
			if(Region.dragStartRegion instanceof TextRegion) {
				selection.respondToMouseDrag(e.getSceneX(), e.getSceneY());
			}
			else if (Region.dragStartRegion instanceof ScrollRegion) {
				scroll(Region.lastY - my);
				Region.lastY = my;
			}
		}
		else if (type.equals("move")) {
			if(interactionRegion instanceof ScrollRegion){
				ScrollRegion scrollRegion = (ScrollRegion)interactionRegion;
				scrollRegion.highlight = true;
			}
			else {
				scrollBar.highlight = false;
			}
		}
	}
	
	public void scroll(double dy) {

		double maxScrollAmount = documentHeight > canvasHeight - TOP_MARGIN - BOTTOM_MARGIN ?
				canvasHeight - TOP_MARGIN - BOTTOM_MARGIN - documentHeight : TOP_MARGIN;
		
		if(yOffset < maxScrollAmount)
			yOffset = maxScrollAmount;
		
		if(yOffset + dy > maxScrollAmount) {
    		yOffset = Math.min(TOP_MARGIN, yOffset + dy);
		}
		else {
			yOffset = maxScrollAmount;
		}
	}

	private static boolean canRenderCharacter(KeyEvent e) {
		int code = (int)e.getCharacter().charAt(0);
		return ((code != 127 && code >= 31) || code == 13 || code == 9);
	}
	
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle( "JText" );
		primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("jlogo.jpeg")));
        
        Group root = new Group();
        primaryScene = new Scene( root );

        ResizeableCanvas canvas = new ResizeableCanvas( INITIAL_WIDTH, INITIAL_HEIGHT );
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add( canvas );
        
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        
        regions.add(mainArea);
        regions.add(scrollBar);
        
        primaryScene.widthProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                canvas.resizeWidth(newSceneWidth.doubleValue());
                maxWidth = canvas.getWidth() - RIGHT_MARGIN;
                mainArea.xRight = canvas.getWidth();
            }
        });
        primaryScene.heightProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                canvas.resizeHeight(newSceneHeight.doubleValue());
                canvasHeight = canvas.getHeight();
                mainArea.yBottom = canvas.getHeight();
            }
        });
        
        primaryScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
        	public void handle(KeyEvent e) {
        		if(e.getCode() == KeyCode.LEFT)
        			cursor.moveBackward();
        		else if(e.getCode() == KeyCode.RIGHT)
        			cursor.moveForward();
        		else if(e.getCode() == KeyCode.UP)
        			cursor.moveUp();
        		else if(e.getCode() == KeyCode.DOWN)
        			cursor.moveDown();
        		else if(e.isControlDown() && e.getCode() == KeyCode.MINUS)
        			updateFontSize(Math.max(fontSize - 4, 4));
        		else if(e.isControlDown() && e.getCode() == KeyCode.EQUALS)
        			updateFontSize(fontSize + 4);
        		else if(e.isControlDown() && e.getCode() == KeyCode.S)
        			save(fileLocation, nodesToString(rootNode, terminalNode));
        		else if(e.isControlDown() && e.getCode() == KeyCode.C)
        			copyToClipboard();
        		else if(e.isControlDown() && e.getCode() == KeyCode.V) {
        			Selection cbContents = textToSelection(getClipboardContents());
        			if(!selection.activated)
        				new PasteAction(cursor.previous, e, cbContents.first, cbContents.second).doAction();
        			else
        				new ReplaceWithTextAction(cursor.previous, e, cbContents.first, cbContents.second, selection).doAction();
        		}
        		else if(e.isControlDown() && e.getCode() == KeyCode.Z) {
        			if(Action.undoStack.size() > 0) {
        				Action toUndo = Action.undoStack.remove(Action.undoStack.size()-1);
        				toUndo.undoAction();
        				Action.redoStack.add(toUndo);
        			}
        		}
        		else if(e.isControlDown() && e.getCode() == KeyCode.Y) {
        			if(Action.redoStack.size() > 0) {
        				Action toRedo = Action.redoStack.remove(Action.redoStack.size()-1);
        				toRedo.doAction();
        				Action.undoStack.add(toRedo);
        			}
        		}
        		else if(e.getCode() == KeyCode.BACK_SPACE)
        			if(!selection.activated)
        				new BackspaceAction(cursor.previous, cursor.next, e).doAction();
        			else
        				new GroupDeleteAction(cursor.previous, e, selection).doAction();
        		else if(e.getCode() == KeyCode.DELETE)
        			if(!selection.activated)
        				new DeleteAction(cursor.previous, cursor.next, e).doAction();
        			else
        				new GroupDeleteAction(cursor.previous, e, selection).doAction();
        		else if(e.isControlDown() && e.getCode() == KeyCode.Q)
        			close();
        	}
        });
        
        primaryScene.setOnKeyTyped(new EventHandler<KeyEvent>() {
        	public void handle(KeyEvent e) {
        		if(canRenderCharacter(e))
        			if(!selection.activated)
        				new AddCharAction(cursor.previous, e).doAction();
        			else
        				new ReplaceWithCharAction(cursor.previous, e, selection).doAction();
        		editorRefreshScreen(gc, canvas);
        	}
        });
        
        primaryScene.setOnMouseMoved(new EventHandler<MouseEvent>() {
        	public void handle(MouseEvent e) {
        		handleMouseEvent(e, "move");
        	}
        });
        
        primaryScene.setOnMousePressed(new EventHandler<MouseEvent>() {
        	public void handle(MouseEvent e) {
        		handleMouseEvent(e, "press");
        	}
        });
        
        primaryScene.setOnMouseReleased(new EventHandler<MouseEvent>() {
        	public void handle(MouseEvent e) {
        		handleMouseEvent(e, "release");
        	}
        });
        
        primaryScene.setOnMouseDragged(new EventHandler<MouseEvent>() {
        	public void handle(MouseEvent e) {
        		handleMouseEvent(e, "drag");
        	}
        });
        
        primaryScene.setOnScroll(new EventHandler<ScrollEvent>() {
        	public void handle(ScrollEvent e) {
        		scroll(e.getDeltaY());
        	}
        });
        
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
            	cursor.perFrameUpdate();
        		editorRefreshScreen(gc, canvas);
            }
        }.start();
	}
}
