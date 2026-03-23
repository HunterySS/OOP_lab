package shapes;

import java.awt.Graphics;

/**
 * Класс для отрезка (линии)
 */
public class Line extends AbstractShape {
    private int x2;
    private int y2;

    public Line(int x1, int y1, int x2, int y2) {
        super(x1, y1);
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void draw(Graphics g) {
        g.drawLine(x, y, x2, y2);
        System.out.println("Line(" + x + ", " + y + ", " + x2 + ", " + y2 + ")");
    }
}