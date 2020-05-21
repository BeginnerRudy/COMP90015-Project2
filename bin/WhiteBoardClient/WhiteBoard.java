package WhiteBoardClient;

import WhiteBoardServer.SerializableBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;


public class WhiteBoard extends JPanel implements Serializable {
    private Mode mode = Mode.LINE;


    private BufferedImage canvas;

    public MyPoint lastPoint, firstPoint;
    public boolean fixed = false;

    public WhiteBoard(int width, int length) {
        this.canvas = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
    }

    public WhiteBoard(BufferedImage canvas) {
        this.canvas = canvas;
    }

    private class PairOfPoints {
        MyPoint start;
        MyPoint end;

        PairOfPoints(MyPoint start, MyPoint end) {
            this.start = start;
            this.end = end;
        }
    }

    /*========================================Getters and Setters========================================*/
    public BufferedImage getWhiteBoard() {
        return this.canvas;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /*========================================Drawing features========================================*/
    public void drawLine(MyPoint start, MyPoint end, Color colour, int size) {
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

        // Draw a line
        synchronized (WhiteBoard.class) {
            g.drawLine(start.x, start.y, end.x, end.y);
        }

        if (this.mode == Mode.FREEHAND) {
            lastPoint = firstPoint;
        }

        // render the line
        this.revalidate();
        this.repaint();
    }

    private PairOfPoints getCorrectPoints(MyPoint start, MyPoint end) {
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

    public void drawRect(MyPoint start, MyPoint end) {
        this.requestFocusInWindow();
        Graphics2D g = (Graphics2D) this.canvas.getGraphics();

        // set default color and weight
        Color col = Color.BLACK;
        g.setColor(col);

        PairOfPoints pairOfPoints = getCorrectPoints(start, end);

        MyPoint first = pairOfPoints.start;
        MyPoint last = pairOfPoints.end;

        synchronized (WhiteBoard.class) {
            g.drawRect(first.x, first.y, last.x - first.x, last.y - first.y);
        }

        // render the line
        this.revalidate();
        this.repaint();
    }

    private int getRadius(MyPoint start, MyPoint end) {
        // TODO how to make the radius to cross both start and end
        return (int) Math.sqrt(Math.pow(Math.abs(start.x - end.x), 2) + Math.pow(Math.abs(start.x - end.x), 2));
    }

    public void drawCircle(MyPoint start, MyPoint end) {
        Graphics2D g = (Graphics2D) canvas.getGraphics();
        Font f = new Font("Serif", Font.PLAIN, 12);
        Color col = Color.BLACK;

        g.setColor(col);
        g.setFont(f);


        int radius = getRadius(start, end);
        synchronized (WhiteBoard.class) {
            g.drawOval(start.x, start.y, radius, radius);
        }
        this.repaint();
    }

    public void drawString(Character c) {
        Graphics2D g = (Graphics2D) canvas.getGraphics();
        Font f = new Font("Serif", Font.PLAIN, 12);
        Color col = Color.BLACK;

        String text = String.valueOf(c);
        g.setColor(col);
        g.setFont(f);
        synchronized (WhiteBoard.class) {
            g.drawString(text, lastPoint.x, lastPoint.y);
        }
        FontMetrics metrics = g.getFontMetrics();

        lastPoint.x += metrics.stringWidth(text);

        this.repaint();
    }

    /*========================================Enable Drag Drawing========================================*/

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
                        synchronized (WhiteBoard.class) {
                            g.drawLine(lastPoint.x, lastPoint.y, firstPoint.x, firstPoint.y);
                        }
                        break;
                    case RECTANGLE:
                        synchronized (WhiteBoard.class) {
                            PairOfPoints pairOfPoints = getCorrectPoints(lastPoint, firstPoint);

                            MyPoint start = pairOfPoints.start;
                            MyPoint end = pairOfPoints.end;
                            g.drawRect(start.x, start.y, end.x - start.x, end.y - start.y);
                        }
                        break;
                    case CIRCLE:
                        int radius = getRadius(lastPoint, firstPoint);
                        synchronized (WhiteBoard.class) {
                            g.drawOval(lastPoint.x, lastPoint.y, radius, radius);
                        }
                    default:
                        System.out.println("not support");
                }
                System.out.println("not fixed");
            } else {
                System.out.println(lastPoint.x);
                System.out.println(firstPoint.y);

                switch (mode) {
                    case LINE:
                        this.drawLine(lastPoint, firstPoint, Color.BLACK, 1);
                        break;
                    case RECTANGLE:
                        this.drawRect(lastPoint, firstPoint);
                        break;
                    case CIRCLE:
                        this.drawCircle(lastPoint, firstPoint);
                    default:
                        System.out.println("not support");
                }

                if (this.getMode() != Mode.TEXT) { // no need to make it null for the text case
                    this.firstPoint = null;
                    this.lastPoint = null;
                    System.out.println("fixed");
                }
            }
        }

    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }
}
