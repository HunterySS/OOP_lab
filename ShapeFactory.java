package shapes;


public interface ShapeFactory {
    
    AbstractShape createShape();
    String getShapeName();
}
