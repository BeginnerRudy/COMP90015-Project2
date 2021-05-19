package Utils;

import java.awt.*;
import java.io.Serializable;

/*
* This class is the Serializable version of the Point class
*
* */
public class MyPoint implements Serializable {
    public int x;
    public int y;
    private static final long serialVersionUID = -455530706921004893L;

    public MyPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public MyPoint(Point point){
        this.x = point.x;
        this.y = point.y;
    }
}
