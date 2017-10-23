package lab1.point;


public class Point {
    public double x;
    public double y;
    public double z = 0;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
