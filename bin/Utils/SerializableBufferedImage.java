package Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

/*
* This class is the Serializable version of the BufferedImage class.
*
* */
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
}
