package dev.tenacity.utils.ui.render.color;


public class ColorObject {
    public final int red;
    public final int green;
    public final int blue;
    public final int alpha;

    public ColorObject(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public int getColorInt() {
        return Colors.getColor(red, green, blue, alpha);
    }

}
