package frc.robot;

public class Shape {

    public final int sides;
    public final double area;
    public final double x;
    public final double y;

    public Shape(int sides, double x, double y, double area) {
        this.sides = sides;
        this.x = x;
        this.y = y;
        this.area = area;
    }

    @Override
    public String toString() {
        return "Shape{" +
                "sides=" + sides +
                ", x=" + x +
                ", y=" + y +
                ", area=" + area +
                '}';
    }
}
