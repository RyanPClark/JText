package rewrite;

public class Region {
	
	/**
	 * Interaction region. zIndex indicates priority level
	 */
	
	public static Region dragStartRegion = null;
	public static double lastY = 0;
	
	public double xLeft, xRight, yTop, yBottom;
	public int zIndex = 0;
	
	public Region(double xLeft, double xRight, double yTop, double yBottom, int zIndex) {
		this.xLeft = xLeft;
		this.xRight = xRight;
		this.yTop = yTop;
		this.yBottom = yBottom;
		this.zIndex = zIndex;
	}
	
	public boolean pointInRegion(double x, double y) {
		return (x > xLeft && x < xRight && y > yTop && y < yBottom);
	}
}
