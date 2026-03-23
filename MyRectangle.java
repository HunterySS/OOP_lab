package shapes;

import java.awt.Graphics;

/**
 * Класс для прямоугольника
 */
public class MyRectangle extends AbstractShape {
    protected int width;
    protected int height;

    public MyRectangle(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics g) {
        g.drawRect(x, y, width, height);
        System.out.println("Rectangle(" + x + ", " + y + ", " + width + ", " + height + ")");
    }
}