package WhiteBoardServer;

import java.awt.*;
import java.io.Serializable;

public class SerializablePoint extends Point implements Serializable {
    SerializablePoint(int x, int y){
        super(x, y);
    }
}
