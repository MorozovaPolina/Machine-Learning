package lab1.kdtree;

import lab1.point.Point;
import lab1.border.Border;
import lab1.item.Item;

import java.util.List;
import java.util.concurrent.Callable;

public class KDTree {


    private class Node {
        Border curBrd;
        Node left = null;
        Node right = null;

        Node(Border brd) {
            this.curBrd = brd;
        }
    }

    final double eps = 0.00001;
    private Node head;
    private List<Item> data;
    private Callable<Integer> countType;


    KDTree(List<Item> data, int recursionDepth, Callable<Integer> countType) {
        this.data = data;
        this.countType = countType;

        head = new Node(
                new Border(
                        new Point(
                                data.stream().min((a, b) -> (int)(a.pnt.x - b.pnt.x)).get().pnt.x - eps,
                                data.stream().min((a, b) -> (int)(a.pnt.y - b.pnt.y)).get().pnt.y - eps
                        ),
                        new Point(
                                data.stream().max((a, b) -> (int)(a.pnt.x - b.pnt.x)).get().pnt.x + eps,
                                data.stream().max((a, b) -> (int)(a.pnt.y - b.pnt.y)).get().pnt.y + eps
                        )
                )
        );
        build(true, head, recursionDepth);
    }



    public int evaluateType(Point p) {
        Node curNode = head;
        while(curNode.left != null && curNode.right != null) {
            if(curNode.left.curBrd.inBorder(p)) {
                curNode = curNode.left;
            } else {
                curNode = curNode.right;
            }
        }
        return curNode.curBrd.type;
    }

    private void build(boolean isVertic, Node limitBrd, int recursionDepth) {
        double step;
        double cur;
        double stop;
        int split = 60;

        recursionDepth --;
        if (isVertic) {
            step = (limitBrd.curBrd.topPoint.x - limitBrd.curBrd.bottomPoint.x) / split;
            cur = limitBrd.curBrd.bottomPoint.x;
            stop = limitBrd.curBrd.topPoint.x;
        } else {
            step = (limitBrd.curBrd.topPoint.y - limitBrd.curBrd.bottomPoint.y) / split;
            cur = limitBrd.curBrd.bottomPoint.y;
            stop = limitBrd.curBrd.topPoint.y;
        }

        int nearestToZero = 10000;
        long leftGreaterThenRight;
        double optimalLine;
        while(cur < stop) {
            final double copcur = cur;
            if (isVertic) {
                leftGreaterThenRight = data.stream().filter(a -> (a.pnt.x < copcur)).count() -
                        data.stream().filter(a -> (a.pnt.x > copcur)).count();
                if (leftGreaterThenRight < nearestToZero) {
                    nearestToZero = (int)leftGreaterThenRight;
                    optimalLine = cur;
                }

            } else {
                leftGreaterThenRight = data.stream().filter(a -> (a.pnt.y < copcur)).count() -
                        data.stream().filter(a -> (a.pnt.y > copcur)).count();
                if (leftGreaterThenRight < nearestToZero) {
                    nearestToZero = (int)leftGreaterThenRight;
                    optimalLine = copcur;
                }

                cur += step;
            }
        }

        if(recursionDepth > 0) {
            if (isVertic) {
                limitBrd.left = new Node(new Border(
                        limitBrd.curBrd.bottomPoint,
                        new Point(cur, limitBrd.curBrd.topPoint.y)
                ));
                build(false, limitBrd.left, recursionDepth);

                limitBrd.right = new Node(new Border(
                        new Point(cur, limitBrd.curBrd.topPoint.y),
                        limitBrd.curBrd.topPoint
                ));
                build(false, limitBrd.right, recursionDepth);
            } else {
                limitBrd.left = new Node(new Border(
                        limitBrd.curBrd.bottomPoint,
                        new Point(limitBrd.curBrd.topPoint.x, cur)
                ));
                build(true, limitBrd.left, recursionDepth);

                limitBrd.right = new Node(new Border(
                        new Point(limitBrd.curBrd.topPoint.x, cur),
                        limitBrd.curBrd.topPoint
                ));
                build(true, limitBrd.right, recursionDepth);
            }
        }
        else {
            try {
                limitBrd.left.curBrd.type = countType.call();
                limitBrd.right.curBrd.type = countType.call();

            } catch (Exception e) {
                System.out.println("Caught exception in count type function: " + e.toString());
            }
        }
    }




}
