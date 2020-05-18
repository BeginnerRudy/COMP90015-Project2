package WhiteBoardServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;

public class SerializableBufferedImage implements Serializable {
    private BufferedImage canvas;

    public BufferedImage getWhiteBoard() {
        return this.canvas;
    }

    public SerializableBufferedImage(int width, int height) {
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public SerializableBufferedImage(BufferedImage canvas) {
        this.canvas = canvas;
    }

    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        ImageIO.write(this.canvas, "png", stream);
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        this.canvas = ImageIO.read(stream);
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
