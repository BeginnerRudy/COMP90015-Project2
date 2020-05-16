package WhiteBoardClient;

import RemoteInterface.IRemoteShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class WhiteBoardController extends JPanel
        implements MouseListener,      // Listen for mouse clicks.
        MouseMotionListener, // Listen for mouse movements.
        KeyListener {
    private IRemoteShape remoteShape;
    //    private WhiteBoard canvas
    // Points for drawing lines.
    private Point lastPoint, firstPoint;

    public WhiteBoardController(IRemoteShape remoteShape){
        this.remoteShape = remoteShape;
        try {
            remoteShape.addMouseListener(this);
//            this.addMouseMotionListener(this);
        }catch (RemoteException e){
            e.printStackTrace();
        }

//        remoteShape.addMouseMotionListener(this);
//        remoteShape.addKeyListener(this);
    }

    private void drawLine(Point nextPoint) throws RemoteException {
        if (lastPoint == null) { // there is no point, so the input is the first point of the line
            lastPoint = nextPoint;
            return;
        }

        if (firstPoint == null) {
            firstPoint = nextPoint;
        }

        remoteShape.drawLine();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            remoteShape.getCanvas().requestFocusInWindow();
            Point newPoint = e.getPoint();

            drawLine(newPoint);
        System.out.println("clicked");
        } catch (RemoteException ee) {
            ee.printStackTrace();
        }
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

        System.out.println("entered");
    }

    @Override
    public void mouseExited(MouseEvent e) {

        System.out.println("exited");
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
