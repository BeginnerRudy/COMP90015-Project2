package WhiteBoardClient;

import java.io.Serializable;

public class MyPoint implements Serializable {
    public int x;
    public int y;
    private static final long serialVersionUID = -455530706921004893L;

    public MyPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
