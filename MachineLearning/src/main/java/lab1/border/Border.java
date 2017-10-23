package lab1.border;

import lab1.point.Point;

public class Border {
    // bottom left point
    public Point bottomPoint;
    // Top right point
    public Point topPoint;

    public int type = -1;

    public Border(Point bottom, Point top) {
        this.bottomPoint = bottom;
        this.topPoint = top;
    }

    public boolean inBorder(Point p) {
        return bottomPoint.x < p.x && topPoint.x > p.x &&
                bottomPoint.y < p.y && topPoint.y > p.y;
    }
}
