package core;

/**
 * Created by rycla on 12/21/2016.
 */
public class ScrollBar {

    public static final int WIDTH = 20;

    private static double percentThrough;
    private static double x, y, height;
    private static boolean pressedUpon = false;

    private static double lastX, lastY, startOffset;

    public static boolean isMouseOver(double mx, double my){
        return (mx > x && mx < x + WIDTH && my > y && my < y + height);
    }

    public static boolean isPressedUpon() {
        return pressedUpon;
    }

    public static void setPressedUpon(boolean pressedUpon, double lastX, double lastY, double startOffset) {
        ScrollBar.pressedUpon = pressedUpon;
        ScrollBar.lastX = lastX;
        ScrollBar.lastY = lastY;
        ScrollBar.startOffset = startOffset;
    }

    public static double getStartOffset() {
        return startOffset;
    }

    public static void setStartOffset(double startOffset) {
        ScrollBar.startOffset = startOffset;
    }

    public static double getLastX() {
        return lastX;
    }

    public static void setLastX(double lastX) {
        ScrollBar.lastX = lastX;
    }

    public static double getLastY() {
        return lastY;
    }

    public static void setLastY(double lastY) {
        ScrollBar.lastY = lastY;
    }

    public static double getPercentThrough() {
        return percentThrough;
    }

    public static void setPercentThrough(double percentThrough) {
        ScrollBar.percentThrough = percentThrough;
    }

    public static double getX() {
        return x;
    }

    public static void setX(double x) {
        ScrollBar.x = x;
    }

    public static double getY() {
        return y;
    }

    public static void setY(double y) {
        ScrollBar.y = y;
    }

    public static double getHeight() {
        return height;
    }

    public static void setHeight(double height) {
        ScrollBar.height = height;
    }
}
