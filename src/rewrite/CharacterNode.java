package rewrite;

import javafx.scene.text.Text;

public class CharacterNode {

	public double x,y, width;
	public String character;
	
	public CharacterNode next;
	public CharacterNode previous;
	
	public CharacterNode(String character) {
		this.character = character;
	}
	
	public String calculateRenderPosition(double x, double y, String text, CharacterNode lastWhitespace, String lastWhitespaceText) {

		this.x = x;
		this.y = y;
		
		if(isWhitespace()) {
			lastWhitespace = this;
			lastWhitespaceText = text;
		}
		else if (!(this instanceof Cursor) && x > ProgramEntry.maxWidth){
			if(lastWhitespace == null) {
				text += "\n";
				x = ProgramEntry.LEFT_MARGIN;
				y += ProgramEntry.lineHeight;
				this.x = x;
				this.y = y;
			}
			else {
				x = ProgramEntry.LEFT_MARGIN;
				y = y + ProgramEntry.lineHeight;
				lastWhitespaceText += "\n";
				return lastWhitespace.next.calculateRenderPosition(x, y, lastWhitespaceText, lastWhitespace, lastWhitespaceText);
			}
		}

		width = calculateWidth();
		
		text += character;
		x += width;

		if(isNewLine()) {
			x = ProgramEntry.LEFT_MARGIN;
			y += ProgramEntry.lineHeight;
		}
		
		if(next != null) {
			return next.calculateRenderPosition(x, y, text, lastWhitespace, lastWhitespaceText);
		}
		else {
			setNumLines();
			return text;
		}
	}
	
	public boolean isThisAfter(CharacterNode comparison) {
		if(comparison.y == this.y)
			return this.x > comparison.x;
		else
			return this.y > comparison.y;
	}
	
	public CharacterNode closestCharToPoint(double coordX, double coordY) {
		
		if(coordY > ProgramEntry.yOffset + ProgramEntry.lineHeight * (ProgramEntry.numLines - 1)) {
			coordY = ProgramEntry.yOffset + ProgramEntry.lineHeight * (ProgramEntry.numLines - 1);
		}
		else if (coordY < ProgramEntry.yOffset) {
			coordY = ProgramEntry.yOffset;
		}
		else {
			int lineNum = (int)((coordY - ProgramEntry.yOffset) / ProgramEntry.lineHeight);
			coordY = ProgramEntry.yOffset + lineNum * ProgramEntry.lineHeight;
		}
		
		if (coordY != this.y) {
			if(next == ProgramEntry.terminalNode)
				return this;
			else
				return next.closestCharToPoint(coordX, coordY);
		}
		else {
			if(coordX - x < calculateWidth() || next.isNewLine() || next == ProgramEntry.terminalNode)
				return this;
			else
				return next.closestCharToPoint(coordX, coordY);
		}
	}
	
	private boolean coordinatesInCharspace(double coordX, double coordY, double width, double height) {
		return coordX > x && coordX < x + width && coordY > y && coordY < y + height;
	}
	
	public void updateCursorIfMouseOver () {
		if (coordinatesInCharspace(ProgramEntry.mx, ProgramEntry.my, width, ProgramEntry.lineHeight)) {
			ProgramEntry.updateCursorIcon(javafx.scene.Cursor.TEXT);
			return;
		}
		else if (next != ProgramEntry.terminalNode)
			next.updateCursorIfMouseOver();
	}
	
    public void addNodeAfterThis(CharacterNode newNode){

        newNode.next = next;
        newNode.previous = this;
        if(next != null)
            next.previous = newNode;
        this.next = newNode;
    }

    public void shiftRight(){
    	CharacterNode after = this.next;
        after.removeThisNodeFromChain();
        this.addNodeBeforeThis(after);
    }

    public void shiftLeft(){
    	CharacterNode before = this.previous;
        before.removeThisNodeFromChain();
        this.addNodeAfterThis(before);
    }

    public void addNodeBeforeThis(CharacterNode newNode){

        if(previous != null)
            previous.next = newNode;
        newNode.previous = previous;
        newNode.next = this;
        this.previous = newNode;
    }


    public void insertChainBeforeThis(CharacterNode start, CharacterNode end){
        start.previous = this.previous;
        end.next = this;
        if(previous != null)
            previous.next = start;
        this.previous = end;
    }
    

    public void removeChain(CharacterNode startNode, CharacterNode endNode){
        startNode.previous.next = endNode.next;
        endNode.next.previous = startNode.previous;
    }
    
    public void removeThisNodeFromChain(){
        if(previous != null)
            previous.next = next;
        if(next != null)
            next.previous = previous;
    }
	
    private void setNumLines() {
    	ProgramEntry.numLines = 1 + (int)((this.y - ProgramEntry.yOffset) / ProgramEntry.lineHeight);
    }
	
	public double calculateWidth(){
        Text text = new Text("" + character);
        text.setFont(ProgramEntry.defaultFont);
        return text.getLayoutBounds().getWidth();
    }
	
	public boolean isNewLine() {
		return character.equals("\n") || character.equals("\r");
	}
	
	private boolean isWhitespace() {
		return character.matches(" |\t|\n");
	}
}
