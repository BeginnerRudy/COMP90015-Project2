package WhiteBoardClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WhiteBoardController extends JPanel
        implements MouseListener,      // Listen for mouse clicks.
        MouseMotionListener, // Listen for mouse movements.
        KeyListener
{
    private WhiteBoard canvas;
    // Points for drawing lines.
    private Point lastPoint, firstPoint;

    public WhiteBoardController(WhiteBoard canvas) {
        this.canvas = canvas;
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addKeyListener(this);
    }

    private void drawLine(Point nextPoint){
        if (lastPoint == null){ // there is no point, so the input is the first point of the line
            lastPoint = nextPoint;
            return;
        }

        if (firstPoint == null){
            firstPoint = nextPoint;
        }

        lastPoint = canvas.drawLine(lastPoint, nextPoint, Color.BLACK, 1);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        canvas.requestFocusInWindow();
        Point newPoint = e.getPoint();

        drawLine(newPoint);
//        System.out.println("clicked");
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        System.out.println("pressed");
    }

    @Override
    public void mouseReleased(MouseEvent e) {

//        System.out.println("released");
    }

    @Override
    public void mouseEntered(MouseEvent e) {

//        System.out.println("entered");
    }

    @Override
    public void mouseExited(MouseEvent e) {

//        System.out.println("exited");
    }

    @Override
    public void mouseDragged(MouseEvent e) {

//        System.out.println("dragged");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        System.out.println("moved");
    }

    @Override
    public void keyTyped(KeyEvent e) {
//        System.out.println("key pressed");
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
