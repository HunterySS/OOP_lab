package shapes;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


public class ShapeList {
    private List<AbstractShape> shapes;

    public ShapeList() {
        shapes = new ArrayList<>();
    }

    
    public void addShape(AbstractShape shape) {
        if (shape != null) {
            shapes.add(shape);
        }
    }

    
    public void removeLastShape() {
        if (!shapes.isEmpty()) {
            shapes.remove(shapes.size() - 1);
        }
    }

    public void clear() {
        shapes.clear();
    }

    public void drawAll(Graphics g) {
        for (AbstractShape shape : shapes) {
            shape.draw(g);
        }
    }


    public int size() {
        return shapes.size();
    }

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ShapeList contains ").append(shapes.size()).append(" shapes:\n");
        for (int i = 0; i < shapes.size(); i++) {
            sb.append("  [").append(i).append("] ").append(shapes.get(i).getClass().getSimpleName());
            sb.append(" at (").append(shapes.get(i).getX()).append(", ").append(shapes.get(i).getY()).append(")\n");
        }
        return sb.toString();
    }
}
