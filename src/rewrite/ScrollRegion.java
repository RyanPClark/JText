package rewrite;

import javafx.scene.paint.Color;

public class ScrollRegion extends Region {

	public boolean highlight = false;
	public Color defaultColor = Color.LIGHTGREY;
	public Color highlightColor = Color.DARKGREY;
	public Color currentColor = defaultColor;
	public double animationState = 0;
	public double maxAnimationState = 30;
	
	public void animate() {
		if(highlight && animationState < maxAnimationState) {
			animationState++;
		}
		else if (!highlight && animationState > 0) {
			animationState--;
		}
		currentColor = Color.rgb((int)(255*(defaultColor.getRed() * (1-animationState/maxAnimationState) + highlightColor.getRed() * (animationState/maxAnimationState))),
				(int)(255*(defaultColor.getGreen() * (1-animationState/maxAnimationState) + highlightColor.getGreen() * (animationState/maxAnimationState))), 
				(int)(255*(defaultColor.getBlue() * (1-animationState/maxAnimationState) + highlightColor.getBlue() * (animationState/maxAnimationState))));
	}
	
	public ScrollRegion(double xLeft, double xRight, double yTop, double yBottom, int zIndex) {
		super(xLeft, xRight, yTop, yBottom, zIndex);
	}
}
