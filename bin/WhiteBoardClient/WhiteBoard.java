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
    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    private Mode mode = Mode.LINE;

    public BufferedImage getCanvas() {
        return canvas;
    }

    private BufferedImage canvas;
    public MyPoint lastPoint, firstPoint;
    public boolean fixed = false;


    public WhiteBoard(int width, int length) {
        this.canvas = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
//        drawLine();
    }

    public WhiteBoard(BufferedImage canvas) {
        this.canvas = canvas;
    }


    public void drawLine() {

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

    public MyPoint drawLine(MyPoint start, MyPoint end, Color colour, int size) {
        this.requestFocusInWindow();
        Graphics2D g = (Graphics2D) this.canvas.getGraphics();

        // set default color and weight
        Color col = Color.BLACK;

        // check and receive input parameters
        if (colour != null) {
            col = colour;
        }

        // setup the g for drawing
        g.setColor(col);
//        g.setStroke(new BasicStroke(weight));

        // Draw a line
        synchronized (WhiteBoard.class) {
            g.drawLine(start.x, start.y, end.x, end.y);
        }

        // render the line
        this.revalidate();
        this.repaint();
//        System.out.println("draw line");
        // return the end point
        return end;
    }

    public void drawRect(MyPoint start, MyPoint end){
        this.requestFocusInWindow();
        Graphics2D g = (Graphics2D) this.canvas.getGraphics();

        // set default color and weight
        Color col = Color.BLACK;
        g.setColor(col);
        // Draw a line
        synchronized (WhiteBoard.class) {
            System.out.println(String.format("start (%d, %d), end (%d , %d)", start.x, start.y, end.x, end.y));
            g.drawRect(start.x, start.y, end.x - start.x, end.y - start.y);
        }

        // render the line
        this.revalidate();
        this.repaint();
    }

    public void drawString(Character c){
        Graphics2D g = (Graphics2D)canvas.getGraphics();
        Font f = new Font("Serif", Font.PLAIN, 12);
        Color col = Color.BLACK;
//        if (colour != null)
//            col = colour;
//        if (font != null)
//            f = font;

        String text = String.valueOf(c);
        g.setColor(col);
        g.setFont(f);
        synchronized (WhiteBoard.class) {
            g.drawString(text, lastPoint.x, lastPoint.y);
        }
        FontMetrics metrics = g.getFontMetrics();

//        MyPoint nextPoint = new MyPoint(lastPoint.x, lastPoint.y);
        lastPoint.x += metrics.stringWidth(text);

        this.repaint();
//        return MyPoint;
    }

    public BufferedImage getWhiteBoard() {
        return this.canvas;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Paints the contents of the canvas to the JPane.
     *
     * @param g The instance to draw with.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g.create();
        graphics.drawImage(canvas, 0, 0, Color.WHITE, null);
        if (lastPoint != null && firstPoint != null) {
            if (!fixed) {
                switch (mode) {
                    case LINE:
                        g.drawLine(lastPoint.x, lastPoint.y, firstPoint.x, firstPoint.y);
                        break;
                    case RECTANGLE:
                        g.drawRect(lastPoint.x, lastPoint.y, firstPoint.x - lastPoint.x, firstPoint.y - lastPoint.y);
                        break;
                    case TEXT:
                        g.drawString("string", 10 , 10);
                        break;
                    default:
                        System.out.println("not support");
                }
                System.out.println("not fixed");
            } else {
                System.out.println(lastPoint.x);
                System.out.println(firstPoint.y);

                switch (mode) {
                    case LINE:
//                        g.drawLine(lastPoint.x, lastPoint.y, firstPoint.x, firstPoint.y);
                        this.drawLine(lastPoint, firstPoint, Color.BLACK, 1);
                        break;
                    case RECTANGLE:
                        this.drawRect(lastPoint, firstPoint);
//                        g.drawRect(lastPoint.x, lastPoint.y, firstPoint.x - lastPoint.x, firstPoint.y - lastPoint.y);
                        break;
                    case TEXT:
                        this.drawString('s');
                        break;
                    default:
                        System.out.println("not support");
                }

                this.firstPoint = null;
                this.lastPoint = null;
                System.out.println("fixed");
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
