package lab1.item;

import lab1.point.Point;

public class Item {
    public Point pnt;
    public int type;

    Item(Point pnt, int type) {
        this.pnt = pnt;
        this.type = type;
    }

    Item(double x, double y, int type) {
        this.pnt = new Point(x, y);
        this.type = type;
    }
}