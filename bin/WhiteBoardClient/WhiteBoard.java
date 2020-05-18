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

    public BufferedImage getCanvas() {
        return canvas;
    }

    private BufferedImage canvas;
    public MyPoint lastPoint, firstPoint;
    public boolean fixed = false;


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
    public MyPoint drawLine(MyPoint start, MyPoint end, Color colour, int size){
        this.requestFocusInWindow();
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
//        g.setStroke(new BasicStroke(weight));

        // Draw a line
        synchronized (WhiteBoard.class){
            g.drawLine(start.x, start.y, end.x, end.y);
        }

        // render the line
//        this.revalidate();
        this.repaint();
//        System.out.println("draw line");
        // return the end point
        return end;
    }

    public MyPoint drawLine(Graphics g, MyPoint start, MyPoint end, Color colour, int size){
        this.requestFocusInWindow();
        // set default color and weight
        Color col = Color.BLACK;
        int weight = 1;

        // check and receive input parameters
        if (colour != null){
            col = colour;
        }
        // setup the g for drawing
        g.setColor(col);

        // Draw a line
        synchronized (WhiteBoard.class){
            g.drawLine(start.x, start.y, end.x, end.y);
        }

        // render the line
//        this.revalidate();
//        this.repaint();
//        System.out.println("draw line");
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
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D)g.create();
//
//        Map<RenderingHints.Key, Object> hintMap = new HashMap<>();
//        // Set any rendering hints.
//        hintMap.put(RenderingHints.KEY_TEXT_ANTIALIASING,
//                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        hintMap.put(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//
//        RenderingHints hints = new RenderingHints(hintMap);
//        graphics.setRenderingHints(hints);
        graphics.drawImage(canvas, 0, 0, Color.WHITE, null);
        if (lastPoint != null && firstPoint != null ) {
            if (!fixed){
                this.drawLine(g, lastPoint, firstPoint, Color.BLACK, 1);
                System.out.println("not fixed");
            }else {
                System.out.println("fixed");
                this.drawLine(lastPoint, firstPoint, Color.BLACK, 1);
                this.firstPoint = null;
                this.lastPoint = null;
            }
        }

    }

//    @Override
//    public void paint(Graphics g) {
//        super.paint(g);
//        if (lastPoint != null && firstPoint != null)
//            g.drawLine(lastPoint.x, lastPoint.y, firstPoint.x, firstPoint.y);
//    }
}
