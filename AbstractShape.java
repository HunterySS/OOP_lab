package shapes;

import java.awt.Graphics;


public abstract class AbstractShape {
    protected int x;
    protected int y;

    public AbstractShape(int x, int y) {
        this.x = x;
        this.y = y;
    }

    
    public abstract void draw(Graphics g);

    
    public void move(int deltaX, int deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                " at (" + x + ", " + y + ")";
    }
}
