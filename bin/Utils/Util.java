package Utils;

public class Util {
    public static final int WHITEBOARD_WIDTH = 720;
    public static final int WHITEBOARD_HEIGHT = 830;
    public static class PairOfPoints {

        public MyPoint start;
        public MyPoint end;

        PairOfPoints(MyPoint start, MyPoint end) {
            this.start = start;
            this.end = end;
        }

    }

    public static PairOfPoints getCorrectPoints(MyPoint start, MyPoint end) {
        MyPoint first;
        MyPoint last;

        if (start.x < end.x) {
            // bottom right
            if (start.y < end.y) {
                first = start;
                last = end;
                System.out.println("bottom right");
                // top right
            } else {
                first = new MyPoint(start.x, end.y);
                last = new MyPoint(end.x, start.y);
                System.out.println("top right");
            }
        } else {
            // bottom left
            if (start.y < end.y) {
                first = new MyPoint(end.x, start.y);
                last = new MyPoint(start.x, end.y);
                System.out.println("bottom left");
                // top left
            } else {
                first = end;
                last = start;
                System.out.println("top left");

            }
        }
        return new PairOfPoints(first, last);
    }

    public static  int getRadius(MyPoint start, MyPoint end) {
        // TODO how to make the radius to cross both start and end
        return (int) Math.sqrt(Math.pow(Math.abs(start.x - end.x), 2) + Math.pow(Math.abs(start.x - end.x), 2));
    }
}
