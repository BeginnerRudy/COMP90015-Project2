package Utils;

/*
* This class defines some commonly used constants and static methods, which would be used by other classes.
*
* */
public class Util {
    // These two are the width and height of the canvas
    public static final int WHITEBOARD_WIDTH = 720;
    public static final int WHITEBOARD_HEIGHT = 830;

    // This inner class represents a pair of points
    public static class PairOfPoints {

        public MyPoint start;
        public MyPoint end;

        PairOfPoints(MyPoint start, MyPoint end) {
            this.start = start;
            this.end = end;
        }

    }

    // This method would takes the user clicked point as input, then it would return the correct points for drawing purpose
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

    // This method is used to calculate the radius depends on the giving points
    public static  int getRadius(MyPoint start, MyPoint end) {
        return (int) Math.sqrt(Math.pow(Math.abs(start.x - end.x), 2) + Math.pow(Math.abs(start.x - end.x), 2));
    }

    // This method is used to print out the server logs
    public static void serverPrinter(String type, String msg){
        System.out.println(type + ": " +  msg);
    }

    // This method is used to print out the client logs
    public static void clientPrinter(String type, String msg){
        System.out.println(type + ": " +  msg);
    }
}
