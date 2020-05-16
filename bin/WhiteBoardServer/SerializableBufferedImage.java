package WhiteBoardServer;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;

public class SerializableBufferedImage extends BufferedImage implements Serializable{

    public SerializableBufferedImage(int width, int height, int imageType) {
        super(width, height, imageType);
    }


//    public String toString() {
//        return name + "\t" + id + "\t" + DOB + "\t";
//    }


//    public SerializableBufferedImage(int width, int height, int imageType, IndexColorModel cm) {
//        super(width, height, imageType, cm);
//    }
//
//    public SerializableBufferedImage(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied, Hashtable<?, ?> properties) {
//        super(cm, raster, isRasterPremultiplied, properties);
//    }
}
