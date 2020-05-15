package WhiteBoardClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class WhiteBoard extends JPanel {

    private BufferedImage canvas;

    public WhiteBoard(int width, int length){
        this.canvas = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
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
        this.repaint();

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
