package WhiteBoardClient;

import WhiteBoardServer.SerializableBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WhiteBoard extends JPanel implements Serializable {

    private BufferedImage canvas;

    public WhiteBoard(int width, int length){
        this.canvas = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
//        drawLine();
    }

    public WhiteBoard(BufferedImage canvas){
        this.canvas = canvas;
    }

//    private void writeObject(java.io.ObjectOutputStream stream)
//            throws IOException {
//        stream.writeInt(this.canvas.getWidth());
//        stream.writeInt(this.canvas.getHeight());
//        stream.writeInt(this.canvas.getType());
//    }
//
//    private void readObject(java.io.ObjectInputStream stream)
//            throws IOException, ClassNotFoundException {
//        this.canvas = new SerializableBufferedImage(stream.readInt(), stream.readInt(), stream.readInt());
//    }

    public void drawLine(){

        Graphics2D g = (Graphics2D) this.canvas.getGraphics();
        g.drawLine(10, 10, 1000, 1000);
        System.out.println("client asked to draw a line");
//        try {
//            TimeUnit.SECONDS.sleep(5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        this.repaint();
    }

    public Point drawLine(Point start, Point end, Color colour, int size){
        Graphics2D g = (Graphics2D) this.canvas.getGraphics();

        // set default color and weight
        Color col = Color.BLACK;
        int weight = 1;

        // check and receive input parameters
        if (colour != null){
            col = colour;
        }
        if (size > 0){
            weight = size;
        }

        // setup the g for drawing
        g.setColor(col);
        g.setStroke(new BasicStroke(weight));

        // Draw a line
        synchronized (WhiteBoard.class){
            g.drawLine(start.x, start.y, end.x, end.y);
        }

        // render the line
        this.revalidate();
        this.repaint();
        System.out.println("draw line");
        // return the end point
        return end;
    }

    public BufferedImage getWhiteBoard() { return this.canvas; }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Paints the contents of the canvas to the JPane.
     *
     * @param g The instance to draw with.
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D graphics = (Graphics2D)g.create();
        Map<RenderingHints.Key, Object> hintMap = new HashMap<>();
        // Set any rendering hints.
        hintMap.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        hintMap.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        RenderingHints hints = new RenderingHints(hintMap);
        graphics.setRenderingHints(hints);
        graphics.drawImage(canvas, 0, 0, Color.WHITE, null);
    }
}
