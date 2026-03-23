package shapes;

import java.awt.Graphics;

/**
 * Класс для эллипса
 */
public class Ellipse extends AbstractShape {
    protected int width;
    protected int height;

    public Ellipse(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics g) {
        g.drawOval(x, y, width, height);
        System.out.println("Ellipse(" + x + ", " + y + ", " + width + ", " + height + ")");
    }
}